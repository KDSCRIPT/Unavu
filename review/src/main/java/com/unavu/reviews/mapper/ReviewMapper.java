package com.unavu.reviews.mapper;

import com.unavu.reviews.dto.CreateReviewDto;
import com.unavu.reviews.dto.ReviewDto;
import com.unavu.reviews.dto.UpdateReviewDto;
import com.unavu.reviews.entity.Review;

public class ReviewMapper {

    public static Review toEntity(CreateReviewDto createReviewDto)
    {
        Review review = new Review();
        review.setRestaurantId(createReviewDto.getRestaurantId());
        review.setReviewerId(createReviewDto.getReviewerId());
        review.setRating(createReviewDto.getRating());
        review.setTitle(createReviewDto.getTitle());
        review.setComment(createReviewDto.getComment());
        review.setIsRecommended(createReviewDto.getIsRecommended()!=null?createReviewDto.getIsRecommended():false);
        return review;

    }

    public static void updateEntity(UpdateReviewDto updateReviewDto, Review review)
    {
        if(updateReviewDto.getRating()!=null)review.setRating(updateReviewDto.getRating());
        if(updateReviewDto.getTitle()!=null)review.setTitle(updateReviewDto.getTitle());
        if(updateReviewDto.getComment()!=null)review.setComment(updateReviewDto.getComment());
        if(updateReviewDto.getIsRecommended()!=null)review.setIsRecommended(updateReviewDto.getIsRecommended());
    }

    public static ReviewDto toDto(Review review)
    {
        ReviewDto reviewDto=new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setRestaurantId(review.getRestaurantId());
        reviewDto.setReviewerId(review.getReviewerId());
        reviewDto.setRating(review.getRating());
        reviewDto.setTitle(review.getTitle());
        reviewDto.setComment(review.getComment());
        reviewDto.setIsRecommended(review.getIsRecommended());
        return reviewDto;
    }
}
