package org.agty.elfiumexpress.storage.thumbs;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ThumbsObserver {
    private static final int MAX_PARALLEL_GENERATIONS = Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final Semaphore GENERATION_SLOTS = new Semaphore(MAX_PARALLEL_GENERATIONS);
    private static final ConcurrentHashMap<String, CompletableFuture<Void>> THUMBS_IN_CREATE = new ConcurrentHashMap<>();

    private ThumbsObserver() {
    }

    public static void createOrWait(String thumb, Duration timeout, ThumbCreation creation) throws IOException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        CompletableFuture<Void> existing = THUMBS_IN_CREATE.putIfAbsent(thumb, future);

        if (existing == null) {
            boolean acquired = false;
            try {
                GENERATION_SLOTS.acquire();
                acquired = true;
                creation.create();
                future.complete(null);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                future.completeExceptionally(e);
                throw new IOException("Interrupted while creating thumb: " + thumb, e);
            } catch (IOException e) {
                future.completeExceptionally(e);
                throw e;
            } catch (RuntimeException e) {
                future.completeExceptionally(e);
                throw e;
            } finally {
                THUMBS_IN_CREATE.remove(thumb, future);
                if (acquired) {
                    GENERATION_SLOTS.release();
                }
            }
            return;
        }

        try {
            existing.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for thumb: " + thumb, e);
        } catch (TimeoutException e) {
            throw new IOException("Timeout while waiting for thumb: " + thumb, e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IOException("Failed while waiting for thumb: " + thumb, cause);
        }
    }

    @FunctionalInterface
    public interface ThumbCreation {
        void create() throws IOException;
    }
}
