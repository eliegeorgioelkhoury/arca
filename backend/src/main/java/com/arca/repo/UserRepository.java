package com.arca.repo;

import com.arca.domain.Role;
import com.arca.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findFirstByRole(Role role);

    boolean existsByEmail(String email);
}
