package com.unavu.lists.service.impl;

import com.unavu.lists.dto.*;
import com.unavu.lists.entity.ListVisibility;
import com.unavu.lists.entity.UserList;
import com.unavu.lists.entity.UserListItem;
import com.unavu.lists.exception.UserListNotFoundException;
import com.unavu.lists.mapper.UserListMapper;
import com.unavu.lists.repository.UserListItemRepository;
import com.unavu.lists.repository.UserListRepository;
import com.unavu.lists.service.IUserListService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserListServiceImpl implements IUserListService {

    private final UserListRepository userListRepository;
    private final UserListItemRepository userListItemRepository;
    @Override
    @Transactional
    public void createUserList(CreateUserListDto createUserListDto) {
        UserList userList= UserListMapper.toUserListEntity(createUserListDto);
        userListRepository.save(userList);
    }

    @Override
    @Transactional
    public void updateUserList(Long id, UpdateUserListDto updateUserListDto) {
        log.info("Updating UserList with id={}",id);
        UserList userList=userListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("UserList with id={} not found for update",id);
                    return new UserListNotFoundException("id",id.toString());
                });
        UserListMapper.updateUserListEntity(updateUserListDto,userList);
        userListRepository.save(userList);
    }

    @Override
    @Transactional
    public void deleteUserList(Long id) {
        log.info("Deleting UserList with id={}",id);
        UserList userList=userListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("UserList with id={} not found for delete",id);
                    return new UserListNotFoundException("id",id.toString());
                });
        userListItemRepository.deleteByListId(id);
        userListRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addItemToList(AddItemToUserListDto addItemToUserListDto) {
        log.info("Adding UserListItem to list with listId={}",addItemToUserListDto.getListId());
        UserList userList = userListRepository.findById(addItemToUserListDto.getListId())
                .orElseThrow(() -> {
                    log.warn("UserList not found with listId={}",addItemToUserListDto.getListId());
                    return new UserListNotFoundException("listId",addItemToUserListDto.getListId().toString());
                });
        int position =
                Optional.ofNullable(
                        userListItemRepository.findMaxPositionByListId(addItemToUserListDto.getListId())
                ).orElse(0) + 1;
        UserListItem userListItem=UserListMapper.toUserListItemEntity(addItemToUserListDto);
        userListItem.setPosition(position);
        userListItemRepository.save(userListItem);
    }

    @Override
    @Transactional
    public void removeItemFromList(Long id) {
        log.info("Removing UserListItem with id={}",id);
       UserListItem userListItem=userListItemRepository.findById(id).orElseThrow(
               () -> {
                   log.warn("UserListItem not found with id={}",id);
                   return new RuntimeException("UserListItem not found with id:"+id);
               }
       );
        userListItemRepository.deleteById(id);

    }

    @Override
    public Page<UserListDto> getListsByVisibility(ListVisibility listVisibility, Pageable pageable) {
        log.info("Getting UserList with visibility={}",listVisibility);
        Page<UserList>result=userListRepository.findByListVisibility(listVisibility,pageable);
        return result.map(UserListMapper::toUserListDto);
    }

    @Override
    public Page<UserListDto> getListsByOwner(Long ownerUserId, Pageable pageable) {
        log.info("Getting UserList with ownerId={}",ownerUserId);
        Page<UserList>result=userListRepository.findByOwnerUserId(ownerUserId,pageable);
        return result.map(UserListMapper::toUserListDto);
    }

    @Override
    public Page<UserListDto> getListsByOwnerAndVisibility(Long ownerUserId, ListVisibility listVisibility, Pageable pageable) {
        log.info("Getting UserList with ownerId={} and visibility={}",ownerUserId,listVisibility);
        Page<UserList>result=userListRepository.findByOwnerUserIdAndListVisibility(ownerUserId, listVisibility, pageable);
        return result.map(UserListMapper::toUserListDto);

    }

    @Override
    public UserListDto getOwnedList(Long id, Long ownerUserId) {
        log.info("Getting UserList with ownerId={} and id={}",ownerUserId,id);
        UserList userList=userListRepository.findByIdAndOwnerUserId(id,ownerUserId)
                .orElseThrow(() -> {
                    log.warn("UserList not found with ownerId={} and id={}",ownerUserId,id);
                    return new UserListNotFoundException("ownerId",ownerUserId.toString());
                });
        return UserListMapper.toUserListDto(userList);
    }

    @Override
    public UserListDto getListById(Long id) {
        log.info("Getting list with id={}", id);

        UserList userList=userListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("UserList not found, id={}", id);
                    return new UserListNotFoundException("id",id.toString());
                });

        return UserListMapper.toUserListDto(userList);
    }
}
