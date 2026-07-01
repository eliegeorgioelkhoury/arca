package com.arca.service;

import com.arca.domain.Role;
import com.arca.domain.User;
import com.arca.error.NotFoundException;
import com.arca.error.UnauthorizedException;
import com.arca.repo.UserRepository;
import com.arca.security.JwtService;
import com.arca.web.dto.TokenResponse;
import com.arca.web.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public TokenResponse login(String email, String password) {
        User u = users.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return new TokenResponse(jwt.issue(u), UserDto.from(u));
    }

    /** One-click demo sign-in: issue a token for the seeded user of the requested role. */
    public TokenResponse demoLogin(Role role) {
        User u = users.findFirstByRole(role)
                .orElseThrow(() -> new NotFoundException("No demo user for role " + role));
        return new TokenResponse(jwt.issue(u), UserDto.from(u));
    }
}
