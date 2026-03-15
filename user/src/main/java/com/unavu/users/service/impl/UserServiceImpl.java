package com.unavu.users.service.impl;

import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.common.web.exception.ResourceAlreadyExistsException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.users.dto.CreateUserDto;
import com.unavu.users.dto.UpdateUserDto;
import com.unavu.users.dto.UserDto;
import com.unavu.users.entity.User;
import com.unavu.users.mapper.UserMapper;
import com.unavu.users.repository.UserRepository;
import com.unavu.users.service.IUserService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final String realmName;
    private final CurrentUserProvider currentUserProvider;

    public UserServiceImpl(UserRepository userRepository,
                           Keycloak keycloak,
                           @Value("${keycloak.realm}") String realmName, CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.keycloak = keycloak;
        this.realmName = realmName;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Page<UserDto> listUsers(Pageable pageable) {
        log.info("Fetching user list with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Override
    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        log.info("Searching user with searchTerm: searchTerm={}", searchTerm);

        Specification<User> spec = Specification
                .where(UserSpecification.displayNameContains(searchTerm));

        return userRepository.findAll(spec, pageable)
                .map(UserMapper::toDto);
    }

    @Override
    public Boolean doesUserExistWithKeycloakId(String userId) {
        return userRepository.existsByKeycloakId(userId);
    }

    @Override
    public UserDto getUserByKeyCloakId(String keyCloakId) {
        log.info("Fetching user by keyCloakId={}", keyCloakId);

        User user = userRepository.findByKeycloakId(keyCloakId)
                .orElseThrow(() -> {
                    log.warn("User not found for fetch, keyCloakId={}", keyCloakId);
                    return new ResourceNotFoundException("User", "keyCloakId", keyCloakId);
                });

        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getUserByDisplayName(String displayName) {
        log.info("Fetching user by displayName={}", displayName);

        User user = userRepository.findByDisplayName(displayName)
                .orElseThrow(() -> {
                    log.warn("User not found for fetch, displayName={}", displayName);
                    return new ResourceNotFoundException("User", "displayName", displayName);
                });

        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void createUser(CreateUserDto createUserDto) {

        log.info("Creating user with email={}", createUserDto.getEmail());
        userRepository.findByEmail(createUserDto.getEmail())
                .ifPresent(user -> {
                    throw new ResourceAlreadyExistsException(
                            "User",
                            "email",
                            createUserDto.getEmail()
                    );
                });

        String keycloakId = null;

        try {
            UserRepresentation kcUser = new UserRepresentation();
            kcUser.setEnabled(true);
            kcUser.setUsername(createUserDto.getEmail());
            kcUser.setEmail(createUserDto.getEmail());
            kcUser.setEmailVerified(true);

            Response response = keycloak.realm(realmName)
                    .users()
                    .create(kcUser);

            if (response.getStatus() != 201) {
                throw new RuntimeException(
                        "Failed to create user in Keycloak. Status: " + response.getStatus()
                );
            }

            keycloakId = CreatedResponseUtil.getCreatedId(response);

            log.info("Keycloak user created with id={}", keycloakId);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(createUserDto.getPassword());

            keycloak.realm(realmName)
                    .users()
                    .get(keycloakId)
                    .resetPassword(credential);
            var realmResource = keycloak.realm(realmName);

            var userRole = realmResource
                    .roles()
                    .get("USER")
                    .toRepresentation();

            realmResource
                    .users()
                    .get(keycloakId)
                    .roles()
                    .realmLevel()
                    .add(List.of(userRole));

            log.info("Assigned USER role in Keycloak");
            User user = UserMapper.toEntity(createUserDto);
            user.setKeycloakId(keycloakId);

            userRepository.save(user);

            log.info("User saved in database with keycloakId={}", keycloakId);

        } catch (Exception ex) {

            log.error("User creation failed. Rolling back Keycloak user", ex);
            if (keycloakId != null) {
                try {
                    keycloak.realm(realmName)
                            .users()
                            .delete(keycloakId);

                    log.info("Rolled back Keycloak user with id={}", keycloakId);
                } catch (Exception cleanupEx) {
                    log.error("Failed to rollback Keycloak user {}", keycloakId, cleanupEx);
                }
            }
            throw ex;
        }
    }

    @Override
    public void updateUser(UpdateUserDto updateUserDto) {
        String keyCloakId = currentUserProvider.getCurrentUserId();
        User user = userRepository.findByKeycloakId(keyCloakId)
                .orElseThrow(() -> {
                    log.warn("User not found for update, keyCloakId={}", keyCloakId);
                    return new ResourceNotFoundException("User", "keyCloakId", keyCloakId);
                });

        if (updateUserDto.getDisplayName() != null &&
                !updateUserDto.getDisplayName().equals(user.getDisplayName())) {

            userRepository.findByDisplayName(updateUserDto.getDisplayName())
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException(
                                "User", "Display Name", updateUserDto
                        );
                    });
        }

        UserMapper.updateEntity(updateUserDto, user);
        userRepository.save(user);

        log.info("User updated successfully, keyCloakId={}", keyCloakId);
    }

    @Override
    @Transactional
    public void deleteUser() {
        String keyCloakId = currentUserProvider.getCurrentUserId();
        User user = userRepository.findByKeycloakId(keyCloakId)
                .orElseThrow(() -> {
                    log.warn("User not found for delete, keyCloakId={}", keyCloakId);
                    return new ResourceNotFoundException("User", "keyCloakId", keyCloakId);
                });

        userRepository.delete(user);
        log.info("User deleted successfully, keyCloakId={}", keyCloakId);

    }

    @Override
    public String getEmailByKeyCloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(User::getEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
    }
}
