package com.arca.support;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base for integration tests: boots a real PostgreSQL (zonky embedded — an actual
 * Postgres binary, no Docker daemon required) once per JVM and points Spring's
 * datasource at it. Liquibase migrations (incl. the sum-to-zero constraint trigger)
 * run against this real database, so the ledger invariant is tested for real.
 */
public abstract class AbstractPostgresIT {

    static final EmbeddedPostgres POSTGRES;

    static {
        try {
            POSTGRES = EmbeddedPostgres.builder().start();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start embedded Postgres", e);
        }
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }
}
