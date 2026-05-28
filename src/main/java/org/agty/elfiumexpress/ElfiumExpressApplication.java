package org.agty.elfiumexpress;

import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.storage.service.StorageService;
import org.agty.elfiumexpress.storage.types.FileMime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ElfiumExpressApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElfiumExpressApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
            FileMime.init();
            try (var sql = ConnectionPool.POOL.borrow()) {
                sql.sql().getConnector().getConnection();
            }
        };
    }
}
