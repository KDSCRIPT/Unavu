package com.unavu.reviews.service.impl;

import com.unavu.reviews.entity.Review;
import org.springframework.data.jpa.domain.Specification;

public class ReviewSpecification {

    public static Specification<Review> hasRestaurantId(Long restaurantId) {
        return (root, query, cb) ->
                restaurantId == null ? null :
                        cb.equal(root.get("restaurantId"),restaurantId);
    }
    public static Specification<Review> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? null :
                        cb.equal(root.get("userId"),userId);
    }
    public static Specification<Review> hasRating(Integer rating) {
        return (root, query, cb) ->
                rating == null ? null :
                        cb.equal(root.get("rating"),rating);
    }

    public static Specification<Review> hasIsRecommended(Boolean isRecommended) {
        return (root, query, cb) ->
                isRecommended == null ? null :
                        cb.equal(root.get("isRecommended"),isRecommended);
    }
}
