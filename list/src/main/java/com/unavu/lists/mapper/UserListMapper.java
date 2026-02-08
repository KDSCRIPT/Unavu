package com.unavu.lists.mapper;

import com.unavu.lists.dto.*;
import com.unavu.lists.entity.UserList;
import com.unavu.lists.entity.UserListItem;

public class UserListMapper {

    public static UserList toUserListEntity(CreateUserListDto createUserListDto)
    {
        UserList userList =new UserList();
        userList.setOwnerUserId(createUserListDto.getOwnerUserId());
        userList.setName(createUserListDto.getName());
        userList.setDescription(createUserListDto.getDescription());
        userList.setListVisibility(createUserListDto.getListVisibility());

        return userList;

    }

    public static void updateUserListEntity(UpdateUserListDto updateUserListDto, UserList userList)
    {
        if(updateUserListDto.getName()!=null)userList.setName(updateUserListDto.getName());
        if(updateUserListDto.getDescription()!=null)userList.setDescription(updateUserListDto.getDescription());
        if(updateUserListDto.getListVisibility()!=null)userList.setListVisibility(updateUserListDto.getListVisibility());
    }

    public static UserListDto toUserListDto(UserList userList)
    {
        UserListDto userListDto=new UserListDto();
        userListDto.setId(userList.getId());
        userListDto.setOwnerUserId(userList.getOwnerUserId());
        userListDto.setName(userList.getName());
        userListDto.setDescription(userList.getDescription());
        userListDto.setListVisibility(userList.getListVisibility());

        return userListDto;
    }

    public static UserListItem toUserListItemEntity(AddItemToUserListDto addItemToUserListDto)
    {
        UserListItem userListItem=new UserListItem();
        userListItem.setListId(addItemToUserListDto.getListId());
        userListItem.setRestaurantId(addItemToUserListDto.getRestaurantId());
        return userListItem;

    }

    public static UserListItemDto toUserListItemDto(UserListItem userListItem)
    {
        UserListItemDto userListItemDto =new UserListItemDto();
        userListItemDto.setListId(userListItem.getListId());
        userListItemDto.setRestaurantId(userListItem.getRestaurantId());
        userListItemDto.setPosition(userListItem.getPosition());
        return userListItemDto;
    }
}
