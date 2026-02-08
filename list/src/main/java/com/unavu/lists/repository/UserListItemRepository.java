package com.unavu.lists.repository;

import com.unavu.lists.entity.UserListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserListItemRepository extends JpaRepository<UserListItem, Long> {
    void deleteByListId(Long listId);
    @Query("SELECT MAX(i.position) FROM UserListItem i WHERE i.listId = :listId")
    Integer findMaxPositionByListId(Long listId);
}
