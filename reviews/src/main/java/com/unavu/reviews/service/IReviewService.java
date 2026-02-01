package com.unavu.reviews.service;

import com.unavu.reviews.dto.CreateReviewDto;
import com.unavu.reviews.dto.ReviewDto;
import com.unavu.reviews.dto.SearchReviewDto;
import com.unavu.reviews.dto.UpdateReviewDto;
import com.unavu.reviews.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IReviewService {

        Page<ReviewDto> listReviews(Pageable pageable);

        Page<ReviewDto> searchReviews(SearchReviewDto searchReviewDto, Pageable pageable);

        Page<ReviewDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable);

        Page<ReviewDto> getReviewsByUser(Long userId, Pageable pageable);

        ReviewDto getReviewById(Long id);

        void createReview(CreateReviewDto createReviewDto);

        void updateReview(Long id, UpdateReviewDto updateReviewDto);

        void deleteReview(Long id);

}
