package com.arca.web;

import com.arca.domain.Role;
import com.arca.security.AuthUser;
import com.arca.service.AuthService;
import com.arca.web.dto.LoginRequest;
import com.arca.web.dto.TokenResponse;
import com.arca.web.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req.email(), req.password());
    }

    /** One-click demo sign-in for a role: EMPLOYEE, MANAGER or ADMIN. */
    @PostMapping("/demo/{role}")
    public TokenResponse demo(@PathVariable Role role) {
        return auth.demoLogin(role);
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal AuthUser me) {
        return new UserDto(me.getId(), me.getUsername(), me.getFullName(), me.getRole().name(), null);
    }
}
