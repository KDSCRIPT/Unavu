package com.unavu.users.service;

import com.unavu.users.dto.CreateUserDto;
import com.unavu.users.dto.UpdateUserDto;
import com.unavu.users.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserService {

    Page<UserDto> listUsers(Pageable pageable);

    Page<UserDto> searchUsers(String searchTerm, Pageable pageable);

    UserDto getUserByKeyCloakId(String keyCloakId);

    UserDto getUserByDisplayName(String displayName);

    void createUser(String keyCloakId,CreateUserDto createUserDto);

    void updateUser(String keyCloakId, UpdateUserDto updateUserDto);

    void deleteUser(String keyCloakId);
}
