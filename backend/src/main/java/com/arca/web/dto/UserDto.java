package com.arca.web.dto;

import com.arca.domain.User;

public record UserDto(Long id, String email, String fullName, String role, String teamName) {

    public static UserDto from(User u) {
        return new UserDto(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getRole().name(),
                u.getTeam() != null ? u.getTeam().getName() : null);
    }
}
