package org.agty.elfiumexpress.dao;

import org.agty.agtysql.AgtySQL;
import org.agty.agtysql.config.AgtySqlConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class AgtySQLPool implements AutoCloseable {
    private static final int CPU = Runtime.getRuntime().availableProcessors();
    private static final int RING_SIZE = 256;

    private final Ring[] rings = new Ring[CPU];
    private final ThreadLocal<Ring> threadRing;
    private final int maxPoolSize;
    private final Duration maxLifetime;
    private final Duration defaultBorrowTimeout;
    private final AtomicInteger totalConnections = new AtomicInteger();
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Object borrowMonitor = new Object();
    private final ScheduledExecutorService housekeeper;

    public AgtySQLPool(AgtySqlConfig config,
                       int maxPoolSize,
                       Duration maxLifetime,
                       Duration defaultBorrowTimeout) {
        this.maxPoolSize = maxPoolSize;
        this.maxLifetime = maxLifetime;
        this.defaultBorrowTimeout = defaultBorrowTimeout;

        for (int i = 0; i < rings.length; i++) {
            rings[i] = new Ring();
        }

        threadRing = ThreadLocal.withInitial(() -> {
            int id = (int) (Thread.currentThread().threadId() % CPU);
            return rings[id];
        });

        housekeeper = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "AgtySQLPool-Housekeeper");
            thread.setDaemon(true);
            return thread;
        });
        housekeeper.scheduleAtFixedRate(this::cleanExtended, 30, 30, TimeUnit.SECONDS);

        this.config = config;
    }

    private final AgtySqlConfig config;

    public PooledAgtySQL borrow() throws java.sql.SQLException {
        return borrow(defaultBorrowTimeout);
    }

    public PooledAgtySQL borrow(Duration timeout) throws java.sql.SQLException {
        if (closed.get()) {
            throw new IllegalStateException("Pool closed");
        }

        Duration effectiveTimeout = (timeout == null || timeout.isZero() || timeout.isNegative())
                ? Duration.ofMillis(1)
                : timeout;

        long deadline = System.nanoTime() + effectiveTimeout.toNanos();

        while (true) {
            PooledAgtySQL connection = tryBorrowOnce();
            if (connection != null) {
                return connection;
            }

            long remainingNanos = deadline - System.nanoTime();
            if (remainingNanos <= 0) {
                throw new java.sql.SQLException("Connection timeout");
            }

            synchronized (borrowMonitor) {
                if (closed.get()) {
                    throw new IllegalStateException("Pool closed");
                }
                try {
                    TimeUnit.NANOSECONDS.timedWait(borrowMonitor, remainingNanos);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new java.sql.SQLException("Interrupted while waiting for a DB connection", e);
                }
            }
        }
    }

    private PooledAgtySQL tryBorrowOnce() {
        Ring ring = threadRing.get();
        PooledAgtySQL connection = ring.poll();
        if (connection != null && connection.tryBorrowFast()) {
            return connection;
        }

        while (true) {
            int current = totalConnections.get();
            if (current >= maxPoolSize) {
                break;
            }
            if (totalConnections.compareAndSet(current, current + 1)) {
                try {
                    return create();
                } catch (RuntimeException e) {
                    totalConnections.decrementAndGet();
                    throw e;
                }
            }
        }

        for (Ring nextRing : rings) {
            connection = nextRing.poll();
            if (connection != null && connection.tryBorrowFast()) {
                return connection;
            }
        }

        return null;
    }

    void release(PooledAgtySQL connection) {
        if (connection.destroyed) {
            return;
        }

        connection.borrowed = false;

        boolean offered = threadRing.get().offer(connection);
        if (!offered) {
            connection.destroy();
            return;
        }

        synchronized (borrowMonitor) {
            borrowMonitor.notify();
        }
    }

    private PooledAgtySQL create() {
        return new PooledAgtySQL(new AgtySQL(config), this);
    }

    private void cleanExtended() {
        Instant now = Instant.now();

        for (Ring ring : rings) {
            synchronized (ring) {
                for (int i = 0; i < RING_SIZE; i++) {
                    PooledAgtySQL connection = ring.buffer[i];
                    if (connection == null) {
                        continue;
                    }

                    if (Duration.between(connection.createdAt, now).compareTo(maxLifetime) > 0) {
                        connection.destroy();
                        ring.buffer[i] = null;
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }

        synchronized (borrowMonitor) {
            borrowMonitor.notifyAll();
        }

        housekeeper.shutdownNow();

        for (Ring ring : rings) {
            for (int i = 0; i < RING_SIZE; i++) {
                PooledAgtySQL connection = ring.buffer[i];
                if (connection != null) {
                    connection.destroy();
                }
            }
        }
    }

    private static final class Ring {
        final PooledAgtySQL[] buffer = new PooledAgtySQL[RING_SIZE];
        int head;
        int tail;

        boolean offer(PooledAgtySQL connection) {
            synchronized (this) {
                int next = (tail + 1) & (RING_SIZE - 1);
                if (next == head) {
                    return false;
                }
                buffer[tail] = connection;
                tail = next;
                return true;
            }
        }

        PooledAgtySQL poll() {
            synchronized (this) {
                if (head == tail) {
                    return null;
                }
                PooledAgtySQL connection = buffer[head];
                buffer[head] = null;
                head = (head + 1) & (RING_SIZE - 1);
                return connection;
            }
        }
    }

    public static final class PooledAgtySQL implements AutoCloseable {
        final AgtySQL delegate;
        final AgtySQLPool pool;
        final Instant createdAt = Instant.now();
        volatile boolean borrowed;
        volatile boolean destroyed;

        PooledAgtySQL(AgtySQL delegate, AgtySQLPool pool) {
            this.delegate = delegate;
            this.pool = pool;
        }

        boolean tryBorrowFast() {
            if (destroyed || borrowed) {
                return false;
            }
            borrowed = true;
            return true;
        }

        public AgtySQL sql() {
            return delegate;
        }

        @Override
        public void close() {
            if (destroyed) {
                return;
            }
            delegate.clearErrors();
            pool.release(this);
        }

        void destroy() {
            if (destroyed) {
                return;
            }
            destroyed = true;
            pool.totalConnections.decrementAndGet();
            synchronized (pool.borrowMonitor) {
                pool.borrowMonitor.notifyAll();
            }
            try {
                delegate.close();
            } catch (Exception ignored) {
            }
        }
    }
}
