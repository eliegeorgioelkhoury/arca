package com.arca;

import org.springframework.boot.SpringApplication;

/**
 * Local dev entrypoint: runs ARCA against an embedded Postgres (no Docker).
 * Start with:  ./mvnw spring-boot:test-run   (serves on http://localhost:8080)
 */
public class TestArcaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(ArcaBackendApplication::main)
                .with(EmbeddedPostgresTestConfig.class)
                .run(args);
    }
}
