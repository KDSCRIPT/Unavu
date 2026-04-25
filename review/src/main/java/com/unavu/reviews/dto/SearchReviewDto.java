package com.unavu.reviews.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
        name="Review Search criteria",
        description="Schema to hold Review Search Criteria"
)
public class SearchReviewDto {

    private Long restaurantId;
    private String reviewerId;
    private Integer rating;
    private Boolean isRecommended;
}
