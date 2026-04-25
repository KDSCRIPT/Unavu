package com.unavu.reviews.repository;

import com.unavu.reviews.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long>,
        JpaSpecificationExecutor<Review> {

    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<Review> findByReviewerId(String reviewerId, Pageable pageable);

    Optional<Review> findByReviewerIdAndRestaurantId(String reviewerId, Long restaurantId);

}
