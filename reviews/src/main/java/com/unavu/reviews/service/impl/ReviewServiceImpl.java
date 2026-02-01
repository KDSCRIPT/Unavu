package com.unavu.reviews.service.impl;

import com.unavu.reviews.dto.CreateReviewDto;
import com.unavu.reviews.dto.ReviewDto;
import com.unavu.reviews.dto.SearchReviewDto;
import com.unavu.reviews.dto.UpdateReviewDto;
import com.unavu.reviews.entity.Review;
import com.unavu.reviews.exception.ResourceNotFoundException;
import com.unavu.reviews.exception.UserAlreadyPostedReviewForRestaurant;
import com.unavu.reviews.mapper.ReviewMapper;
import com.unavu.reviews.repository.ReviewRepository;
import com.unavu.reviews.service.IReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    @Override
    public Page<ReviewDto> listReviews(Pageable pageable) {
        log.info("Fetching review list with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return reviewRepository.findAll(pageable).map(ReviewMapper::toDto);
    }

    @Override
    public Page<ReviewDto> searchReviews(SearchReviewDto searchReviewDto, Pageable pageable) {
        log.info("Searching reviews with filters: restaurantId={}, userId={}, rating={}, isRecommended={}",
        searchReviewDto.getRestaurantId(),
        searchReviewDto.getUserId(),
        searchReviewDto.getRating(),
        searchReviewDto.getIsRecommended());

        Specification<Review> spec=Specification
                .where(ReviewSpecification.hasRestaurantId(searchReviewDto.getRestaurantId()))
                .and(ReviewSpecification.hasUserId(searchReviewDto.getUserId()))
                .and(ReviewSpecification.hasRating(searchReviewDto.getRating()))
                .and(ReviewSpecification.hasIsRecommended(searchReviewDto.getIsRecommended()));

        return reviewRepository.findAll(spec,pageable)
                .map(ReviewMapper::toDto);
    }

    @Override
    public Page<ReviewDto> getReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        log.info("Fetching review by restaurantId={}", restaurantId);

        return reviewRepository.findByRestaurantId(restaurantId,pageable).map(ReviewMapper::toDto);
    }

    @Override
    public Page<ReviewDto> getReviewsByUser(Long userId, Pageable pageable) {
        log.info("Fetching review by userId={}", userId);
        return reviewRepository.findByUserId(userId,pageable).map(ReviewMapper::toDto);
    }

    @Override
    public ReviewDto getReviewById(Long id) {
        log.info("Fetching review by id={}", id);

        Review review=reviewRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Review not found for fetch, id={}", id);
                    return new ResourceNotFoundException("Review", "id", id.toString());
                });

        return ReviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public void createReview(CreateReviewDto createReviewDto) {
        log.info("Creating review: restaurantId={}, userId={}, rating={}, title={}, comment={}, isRecommended={}",
                createReviewDto.getRestaurantId(),
                createReviewDto.getUserId(),
                createReviewDto.getRating(),
                createReviewDto.getTitle(),
                createReviewDto.getComment(),
                createReviewDto.getIsRecommended());
        Optional<Review> optionalReview =
                reviewRepository.findByUserIdAndRestaurantId(createReviewDto.getUserId(), createReviewDto.getRestaurantId());

        if (optionalReview.isPresent()) {
            log.warn("User has posted review already for restaurant: userId={}, restaurantId={}",
                    createReviewDto.getUserId(),createReviewDto.getRestaurantId());
            throw new UserAlreadyPostedReviewForRestaurant(
                    "User has posted review already for restaurant"
            );
        }

        Review review= ReviewMapper.toEntity(createReviewDto);
        reviewRepository.save(review);

        log.info("Review created successfully with id={}", review.getId());
    }

    @Override
    @Transactional
    public void updateReview(Long id, UpdateReviewDto updateReviewDto) {
        log.info("Updating review with id={}", id);

        Review review=reviewRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Review not found for update, id={}", id);
                    return new ResourceNotFoundException("Review", "id", id.toString());
                });

        ReviewMapper.updateEntity(updateReviewDto,review);
        reviewRepository.save(review);

        log.info("Review updated successfully, id={}", id);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        log.info("Deleting review with id={}", id);

        Review review=reviewRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Review not found for delete, id={}", id);
                    return new ResourceNotFoundException("Review", "id", id.toString());
                });

        reviewRepository.delete(review);
        log.info("Review deleted successfully, id={}", id);
    }
}
