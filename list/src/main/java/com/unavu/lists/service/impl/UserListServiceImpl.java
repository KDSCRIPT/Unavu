package com.unavu.lists.service.impl;

import com.unavu.common.messaging.EventPublisher;
import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.common.web.dto.EntityType;
import com.unavu.common.web.dto.NotificationDto;
import com.unavu.common.web.dto.NotificationType;
import com.unavu.common.web.exception.ResourceActionNotAllowedException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.lists.dto.*;
import com.unavu.lists.entity.ListVisibility;
import com.unavu.lists.entity.UserList;
import com.unavu.lists.entity.UserListItem;
import com.unavu.lists.mapper.UserListMapper;
import com.unavu.lists.repository.UserListItemRepository;
import com.unavu.lists.repository.UserListRepository;
import com.unavu.lists.service.IUserListService;
import com.unavu.lists.service.client.RestaurantFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserListServiceImpl implements IUserListService {

    private final UserListRepository userListRepository;
    private final UserListItemRepository userListItemRepository;
    private RestaurantFeignClient restaurantFeignClient;
    private CurrentUserProvider currentUserProvider;
    private EventPublisher eventPublisher;
    @Override
    @Transactional
    public void createUserList(CreateUserListDto createUserListDto) {
        String ownerId = currentUserProvider.getCurrentUserId();
        createUserListDto.setOwnerId(ownerId);
        UserList userList = UserListMapper.toUserListEntity(createUserListDto);
        userListRepository.save(userList);

        String message = String.format(
                "New list %s created by %s",
                userList.getName(),
                userList.getOwnerId()
        );
        if(userList.getListVisibility()!=ListVisibility.PRIVATE) {
            NotificationDto event = new NotificationDto(
                    NotificationType.LIST_CREATED,
                    userList.getOwnerId(),
                    userList.getOwnerId(),
                    EntityType.LIST,
                    userList.getId(),
                    message
            );
            eventPublisher.publishNotification(event);
        }
    }

    @Override
    @Transactional
    public void updateUserList(Long id, UpdateUserListDto updateUserListDto) {
        log.info("Updating UserList with id={}",id);
        String ownerId = currentUserProvider.getCurrentUserId();
        UserList userList = userListRepository
                .findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("UserList", "id", id.toString())
                );
        if(!Objects.equals(userList.getOwnerId(),ownerId))
        {
            throw new ResourceActionNotAllowedException("User cannot update other's List");
        }
        UserListMapper.updateUserListEntity(updateUserListDto, userList);
        userListRepository.save(userList);
    }

    @Override
    @Transactional
    public void deleteUserList(Long id) {
        log.info("Deleting UserList with id={}",id);
        String ownerId = currentUserProvider.getCurrentUserId();
        UserList userList = userListRepository
                .findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("UserList", "id", id.toString())
                );
        if(!Objects.equals(userList.getOwnerId(),ownerId))
        {
            throw new ResourceActionNotAllowedException("User cannot delete other's List");
        }
        userListItemRepository.deleteByListId(id);
        userListRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addItemToList(AddItemToUserListDto addItemToUserListDto) {
        log.info("Adding UserListItem to list with listId={}",addItemToUserListDto.getListId());
        if (!restaurantFeignClient.doesRestaurantExist(addItemToUserListDto.getRestaurantId())) {
            throw new ResourceNotFoundException("Restaurant","id", addItemToUserListDto.getRestaurantId());
        }
        String ownerId = currentUserProvider.getCurrentUserId();
        UserList userList = userListRepository
                .findByIdAndOwnerId(addItemToUserListDto.getListId(), ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("UserList", "id", addItemToUserListDto.getListId())
                );
        int position =
                Optional.ofNullable(
                        userListItemRepository.findMaxPositionByListId(addItemToUserListDto.getListId())
                ).orElse(0) + 1;
        if(!Objects.equals(userList.getOwnerId(),ownerId))
        {
            throw new ResourceActionNotAllowedException("User cannot add item to other's List");
        }
        UserListItem userListItem=UserListMapper.toUserListItemEntity(addItemToUserListDto);
        userListItem.setPosition(position);
        userListItemRepository.save(userListItem);

        String restaurantName=restaurantFeignClient.getRestaurantName(userListItem.getRestaurantId()).getBody();
        String message = String.format(
                "%s added %s to list %s",
                currentUserProvider.getCurrentUserName(),
                restaurantName,
                userList.getName()
        );
        if(userList.getListVisibility()!=ListVisibility.PRIVATE) {
            NotificationDto event = new NotificationDto(
                    NotificationType.LIST_ITEM_ADDED,
                    userList.getOwnerId(),
                    userList.getOwnerId(),
                    EntityType.LIST_ITEM,
                    userListItem.getId(),
                    message
            );
            eventPublisher.publishNotification(event);
        }
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
        String ownerId = currentUserProvider.getCurrentUserId();
        UserList userList = userListRepository
                .findByIdAndOwnerId(userListItem.getListId(), ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("UserList", "id", userListItem.getListId())
                );
        if(!Objects.equals(userList.getOwnerId(),ownerId))
        {
            throw new ResourceActionNotAllowedException("User cannot delete item to other's List");
        }
        userListItemRepository.deleteById(id);
    }

    @Override
    public Page<UserListDto> getListsByVisibility(ListVisibility listVisibility, Pageable pageable) {
        log.info("Getting UserList with visibility={}",listVisibility);
        Page<UserList>result=userListRepository.findByListVisibility(listVisibility,pageable);
        return result.map(UserListMapper::toUserListDto);
    }

    @Override
    public Page<UserListDto> getListsByOwner(Pageable pageable) {
        String ownerId = currentUserProvider.getCurrentUserId();
        log.info("Getting UserList with ownerId={}", ownerId);
        Page<UserList> result = userListRepository.findByOwnerId(ownerId, pageable);

        List<UserListDto> filtered = result.getContent().stream()
                .filter(list -> list.getListVisibility() == ListVisibility.PUBLIC)
                .map(UserListMapper::toUserListDto)
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }
    @Override
    public Page<UserListDto> getOwnedList(Pageable pageable) {
        String ownerId = currentUserProvider.getCurrentUserId();
        Page<UserList>result=userListRepository.findByOwnerId(ownerId,pageable);
        return result.map(UserListMapper::toUserListDto);
    }

    @Override
    public UserListDto getListById(Long id) {
        log.info("Getting list with id={}", id);
        String ownerId = currentUserProvider.getCurrentUserId();
        UserList userList=userListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("UserList not found, id={}", id);
                    return new ResourceNotFoundException("User List","id",id.toString());
                });
        if(!Objects.equals(userList.getOwnerId(),ownerId) && userList.getListVisibility()==ListVisibility.PRIVATE)
        {
            throw new ResourceActionNotAllowedException("User list is Private");
        }
        return UserListMapper.toUserListDto(userList);
    }

    @Override
    public UserListItemDto getListItemById(Long id) {
        log.info("Getting list item with id={}", id);
        String ownerId = currentUserProvider.getCurrentUserId();
        UserListItem userListItem=userListItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("UserList Item not found, id={}", id);
                    return new ResourceNotFoundException("User List","id",id.toString());
                });
        UserList userList=userListRepository.findById(userListItem.getListId())
                .orElseThrow(() -> {
                    log.warn("Item List id is not found, id={}", id);
                    return new ResourceNotFoundException("User List","id",id.toString());
                });
        if(!Objects.equals(userList.getOwnerId(),ownerId) && userList.getListVisibility()==ListVisibility.PRIVATE)
        {
            throw new ResourceActionNotAllowedException("User list is Private");
        }
        return UserListMapper.toUserListItemDto(userListItem);
    }

    @Override
    public Page<UserListItemDto> getListItems(Long listId, Pageable pageable) {
        log.info("Getting contents of list with id={}", listId);
        String ownerId = currentUserProvider.getCurrentUserId();
        UserList userList=userListRepository.findById(listId)
                .orElseThrow(() -> {
                    log.warn("Contents of UserList not found, id={}", listId);
                    return new ResourceNotFoundException("User List","id",listId.toString());
                });
        if(!Objects.equals(userList.getOwnerId(),ownerId) && userList.getListVisibility()==ListVisibility.PRIVATE)
        {
            throw new ResourceActionNotAllowedException("User list is Private");
        }
        Page<UserListItem>contents=userListItemRepository.findByListId(listId,pageable);

        return contents.map(UserListMapper::toUserListItemDto);
    }

}