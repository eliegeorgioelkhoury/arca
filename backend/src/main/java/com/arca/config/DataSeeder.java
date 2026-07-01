package com.arca.config;

import com.arca.service.DemoService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Ensures demo accounts and sample data exist on startup (idempotent). */
@Component
public class DataSeeder implements ApplicationRunner {

    private final DemoService demo;

    public DataSeeder(DemoService demo) {
        this.demo = demo;
    }

    @Override
    public void run(ApplicationArguments args) {
        demo.ensureDemoUsers();
        demo.seedSampleExpensesIfEmpty();
    }
}
