package com.unavu.users.mapper;

import com.unavu.users.dto.CreateUserDto;
import com.unavu.users.dto.UpdateUserDto;
import com.unavu.users.dto.UserDto;
import com.unavu.users.entity.User;

public class UserMapper {

    public static User toEntity(CreateUserDto createUserDto) {

        User user = new User();
        user.setEmail(createUserDto.getEmail());
        user.setDisplayName(createUserDto.getDisplayName());
        user.setDescription(createUserDto.getDescription());
        return user;
    }

    public static void updateEntity(UpdateUserDto updateUserDto, User user)
    {
        if(updateUserDto.getDisplayName()!=null)user.setDisplayName(updateUserDto.getDisplayName());
        if(updateUserDto.getDescription()!=null)user.setDescription(updateUserDto.getDescription());
    }

    public static UserDto toDto(User user) {

        UserDto userDto = new UserDto();
        userDto.setDisplayName(user.getDisplayName());
        userDto.setDescription(user.getDescription());
        return userDto;
    }
}
