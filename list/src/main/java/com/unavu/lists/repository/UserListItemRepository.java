package com.unavu.lists.repository;

import com.unavu.lists.dto.UserListItemDto;
import com.unavu.lists.entity.UserList;
import com.unavu.lists.entity.UserListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserListItemRepository extends JpaRepository<UserListItem, Long> {
    void deleteByListId(Long listId);
    @Query("SELECT MAX(i.position) FROM UserListItem i WHERE i.listId = :listId")
    Integer findMaxPositionByListId(Long listId);
    Page<UserListItem> findByListId(Long listId, Pageable pageable);

}
