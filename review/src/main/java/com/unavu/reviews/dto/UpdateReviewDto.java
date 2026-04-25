package com.unavu.reviews.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(
        name="Update Review",
        description="Schema to hold Review inundation information"
)
public class UpdateReviewDto {
    @Schema(
            description = "Rating given by user for the restaurant from 1 to 5"
    )
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;

    @Schema(
            description = "Title of review posted by the user", example="Great Restaurant for Chinese Food"
    )
    private String title;

    @Schema(
            description = "Comment of review posted by the user", example="I went here yesterday and it was so good...."
    )
    private String comment;

    private Boolean isRecommended;
}
