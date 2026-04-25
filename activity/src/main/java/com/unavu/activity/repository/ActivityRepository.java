package com.unavu.activity.repository;

import com.unavu.activity.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findTop20ByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

}