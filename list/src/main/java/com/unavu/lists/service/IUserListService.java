package com.unavu.lists.service;

import com.unavu.lists.dto.*;
import com.unavu.lists.entity.ListVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserListService {
    void createUserList(CreateUserListDto createUserListDto);
    void updateUserList(Long id, UpdateUserListDto updateUserListDto);
    void deleteUserList(Long id);

    void addItemToList(AddItemToUserListDto addItemToUserListDto);
    void removeItemFromList(Long listItemId);

    Page<UserListDto> getListsByVisibility(ListVisibility listVisibility, Pageable pageable);
    Page<UserListDto> getListsByOwner(Long ownerUserId, Pageable pageable);
    Page<UserListDto> getListsByOwnerAndVisibility(Long ownerUserId, ListVisibility listVisibility, Pageable pageable);

    UserListDto getOwnedList(Long id, Long ownerUserId);//do not throw optional for controllers
    UserListDto getListById(Long id);


}
