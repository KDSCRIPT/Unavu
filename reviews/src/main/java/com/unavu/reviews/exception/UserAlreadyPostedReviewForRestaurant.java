package com.unavu.reviews.exception;

public class UserAlreadyPostedReviewForRestaurant extends RuntimeException {
    public UserAlreadyPostedReviewForRestaurant(String message) {
        super(message);
    }
}
