package com.arca.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health")
public class HealthController {

    /**
     * Liveness probe that intentionally touches no database, so the API stays warm
     * (UptimeRobot) while Neon scales its compute to zero.
     */
    @GetMapping("/health")
    @Operation(summary = "DB-free liveness probe")
    public Map<String, Object> health() {
        return Map.of("status", "UP", "service", "arca");
    }
}
