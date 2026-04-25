package com.unavu.feed.repository;

import com.unavu.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findTop20ByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

}