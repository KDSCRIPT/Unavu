package com.unavu.users.service.impl;

import com.unavu.common.web.exception.ResourceAlreadyExistsException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.users.dto.CreateUserDto;
import com.unavu.users.dto.UpdateUserDto;
import com.unavu.users.dto.UserDto;
import com.unavu.users.entity.User;
import com.unavu.users.mapper.UserMapper;
import com.unavu.users.repository.UserRepository;
import com.unavu.users.service.IUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public Page<UserDto> listUsers(Pageable pageable) {
        log.info("Fetching user list with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Override
    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        log.info("Searching user with searchTerm: searchTerm={}", searchTerm);

        Specification<User> spec=Specification
                .where(UserSpecification.displayNameContains(searchTerm));

        return userRepository.findAll(spec,pageable)
                .map(UserMapper::toDto);
    }

    @Override
    public UserDto getUserByKeyCloakId(String keyCloakId) {
        log.info("Fetching user by keyCloakId={}", keyCloakId);

        User user=userRepository.findByKeycloakId(keyCloakId)
                .orElseThrow(() -> {
                    log.warn("User not found for fetch, keyCloakId={}", keyCloakId);
                    return new ResourceNotFoundException("User", "keyCloakId", keyCloakId);
                });

        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getUserByDisplayName(String displayName) {
        log.info("Fetching user by displayName={}", displayName);

        User user=userRepository.findByDisplayName(displayName)
                .orElseThrow(() -> {
                    log.warn("User not found for fetch, displayName={}", displayName);
                    return new ResourceNotFoundException("User", "displayName", displayName);
                });

        return UserMapper.toDto(user);
    }

    @Override
    public void createUser(String keyCloakId,CreateUserDto createUserDto) {
        log.info("Creating user: displayName={}, description={}",
                createUserDto.getDisplayName(),
                createUserDto.getDescription());
        Optional<User> optionalUser =
                userRepository.findByDisplayName(createUserDto.getDisplayName());

        if (optionalUser.isPresent()) {
            log.warn("User with mentioned displayName already Exists: displayName={}",
                    createUserDto.getDisplayName());
            throw new ResourceAlreadyExistsException(
                    "User","Display Name",optionalUser
            );
        }

        User user=UserMapper.toEntity(createUserDto);
        user.setKeycloakId(keyCloakId);
        userRepository.save(user);
        log.info("User created successfully with keyCloakId={}", user.getKeycloakId());

    }

    @Override
    public void updateUser(String keyCloakId, UpdateUserDto updateUserDto) {
        log.info("Updating User with keyCloakId={}", keyCloakId);

        User user=userRepository.findByKeycloakId(keyCloakId)
                .orElseThrow(() -> {
                    log.warn("User not found for update, keyCloakId={}",keyCloakId);
                    return new ResourceNotFoundException("User", "keyCloakId", keyCloakId);
                });

        if (updateUserDto.getDisplayName() != null &&
                !updateUserDto.getDisplayName().equals(user.getDisplayName())) {

            userRepository.findByDisplayName(updateUserDto.getDisplayName())
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException(
                                "User","Display Name",updateUserDto
                        );
                    });
        }

        UserMapper.updateEntity(updateUserDto,user);
        userRepository.save(user);

        log.info("User updated successfully, keyCloakId={}", keyCloakId);
    }

    @Override
    @Transactional
    public void deleteUser(String keyCloakId) {
        log.info("Deleting User with keyCloakId={}", keyCloakId);

        User user=userRepository.findByKeycloakId(keyCloakId)
                .orElseThrow(() -> {
                    log.warn("User not found for delete, keyCloakId={}", keyCloakId);
                    return new ResourceNotFoundException("User", "keyCloakId",keyCloakId);
                });

        userRepository.delete(user);
        log.info("User deleted successfully, keyCloakId={}", keyCloakId);

    }
}
