package com.unavu.reviews.dto;

import lombok.Data;

@Data
public class ReviewDto {

    private Long id;
    private Long restaurantId;
    private String reviewerId;
    private int rating;
    private String title;
    private String comment;
    private Boolean isRecommended;
}
