package com.arca;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Provides a real Postgres (zonky embedded — no Docker) as the datasource, so the
 * whole app can be run locally with {@code ./mvnw spring-boot:test-run} for the
 * end-to-end (Playwright) flow without any external database.
 */
@TestConfiguration(proxyBeanMethods = false)
public class EmbeddedPostgresTestConfig {

    @Bean
    public DataSource dataSource() throws IOException {
        return EmbeddedPostgres.builder().start().getPostgresDatabase();
    }
}
