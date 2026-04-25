package com.unavu.users.repository;

import com.unavu.users.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByDisplayName(String displayName);
    Boolean existsByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
}
