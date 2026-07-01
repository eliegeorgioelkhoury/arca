package com.arca.web;

import com.arca.service.DemoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Demo")
public class DemoController {

    private final DemoService demo;
    private final boolean resetEnabled;

    public DemoController(DemoService demo, @Value("${app.demo.reset-enabled}") boolean resetEnabled) {
        this.demo = demo;
        this.resetEnabled = resetEnabled;
    }

    /** Reset demo data to a known-good state so recruiter tinkering can't wreck it. */
    @PostMapping("/reset")
    public Map<String, Object> reset() {
        if (!resetEnabled) {
            throw new IllegalStateException("Demo reset is disabled");
        }
        return demo.reset();
    }
}
