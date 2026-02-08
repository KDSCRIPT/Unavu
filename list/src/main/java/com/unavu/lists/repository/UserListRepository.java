package com.unavu.lists.repository;

import com.unavu.lists.dto.UserListDto;
import com.unavu.lists.entity.ListVisibility;
import com.unavu.lists.entity.UserList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserListRepository extends JpaRepository<UserList, Long>, JpaSpecificationExecutor<UserList> {
    Page<UserList> findByListVisibility(ListVisibility listVisibility,Pageable pageable);
    Page<UserList> findByOwnerUserId(Long ownerUserId,Pageable pageable);
    Page<UserList> findByOwnerUserIdAndListVisibility(Long ownerUserId, ListVisibility listVisibility, Pageable pageable);
    Optional<UserList> findByIdAndOwnerUserId(Long id, Long ownerUserId);
}
