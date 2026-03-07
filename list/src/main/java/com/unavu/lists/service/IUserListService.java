package com.unavu.lists.service;

import com.unavu.lists.dto.*;
import com.unavu.lists.entity.ListVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserListService {
    void createUserList(CreateUserListDto createUserListDto);
    void updateUserList(Long id, UpdateUserListDto updateUserListDto);
    void deleteUserList(Long id);

    void addItemToList(AddItemToUserListDto addItemToUserListDto);
    void removeItemFromList(Long listItemId);

    Page<UserListDto> getListsByVisibility(ListVisibility listVisibility, Pageable pageable);
    Page<UserListDto> getListsByOwner(Pageable pageable);

    Page<UserListDto> getOwnedList(Pageable pageable);//do not throw optional for controllers
    UserListDto getListById(Long id);

    UserListItemDto getListItemById(Long id);

    Page<UserListItemDto> getListItems(Long listId, Pageable pageable);
}
