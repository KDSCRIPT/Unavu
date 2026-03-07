package com.unavu.reviews.controller;

import com.unavu.common.web.dto.ErrorResponseDto;
import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.dto.ResponseDto;
import com.unavu.reviews.dto.*;
import com.unavu.reviews.provider.CurrentUserProvider;
import com.unavu.reviews.service.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "CRUD REST APIs for Reviews",
        description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE Review details"
)
@Slf4j
@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class ReviewController {

    private final IReviewService iReviewService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Create Review REST API",
            description = "REST API to create new Review"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping(value="/reviews",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createReview(@Valid @RequestBody CreateReviewDto createReviewDto)
    {
        String reviewerId = currentUserProvider.getCurrentUserId();
        createReviewDto.setReviewerId(reviewerId);
        log.info("Creating review: restaurantId={}, reviewerId={}, rating={}, title={}, comment={}, isRecommended={}",
                createReviewDto.getRestaurantId(),
                createReviewDto.getReviewerId(),
                createReviewDto.getRating(),
                createReviewDto.getTitle(),
                createReviewDto.getComment(),
                createReviewDto.getIsRecommended());

        iReviewService.createReview(createReviewDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"Review")));
    }

    @Operation(
            summary = "Search Reviews REST API",
            description = "REST API to search reviews based on filters"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping(value="/reviews/search",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ReviewDto>>searchReviews(
            @Valid @RequestBody SearchReviewDto searchReviewDto,
            Pageable pageable
            ) {
        log.info("Searching reviews with filters: {}", searchReviewDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                iReviewService.searchReviews(searchReviewDto,pageable)
        );
    }

    @Operation(
            summary = "Fetch Review by user id REST API",
            description = "REST API to fetch Review details based on user id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewDto>> listReviews(
            @RequestParam(required = false) String reviewerId,
            @RequestParam(required = false) Long restaurantId,
            Pageable pageable
    )
    {
        if(reviewerId!=null)
        {
            return ResponseEntity.ok(iReviewService.getReviewsByUser(reviewerId,pageable));
        }
        if(restaurantId!=null)
        {
            return ResponseEntity.ok(iReviewService.getReviewsByRestaurant(restaurantId,pageable));
        }
        throw new IllegalArgumentException("Either userId or restaurantId must be provided");
    }

    @Operation(
            summary = "Fetch Review by review id REST API",
            description = "REST API to fetch Review details based on review id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        log.info("Fetch Review based on review id: id={}",id);
        return ResponseEntity.status(HttpStatus.OK).body(
                iReviewService.getReviewById(id)
        );
    }

    @Operation(
            summary = "Update Review details REST API",
            description = "REST API to update Review details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PatchMapping(value="/reviews/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateReviewDetails(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewDto updateReviewDto
    ) {
        log.info("Updating review with id={}", id);
        iReviewService.updateReview(id,updateReviewDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        ResponseConstants.STATUS_OK,String.format(ResponseConstants.MESSAGE_OK,"Review")
                ));
    }

    @Operation(
            summary = "Delete Review REST API",
            description = "REST API to delete Review details by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "HTTP Status No Content"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @DeleteMapping(value="/reviews/{id}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable long id) {
        log.info("Deleting review with id={}", id);
        iReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
