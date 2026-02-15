package com.unavu.reviews.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(
        name="Create Review",
        description="Schema to hold Review creation information"
)
public class CreateReviewDto {

    @NotNull(message="Id of the restaurant cannot be null or empty")
    @Schema(
            description = "Id of the Restaurant to be reviewed", example="1"
    )
    private Long restaurantId;

    @NotNull(message="Id of the user reviewing cannot be null or empty")
    @Schema(
            description = "Id of the user posting the review", example="2"
    )
    private Long userId;

    @Schema(
            description = "Rating given by user for the restaurant from 1 to 5"
    )
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;

    @NotBlank(message="Title of review posted by the user cannot be null or empty")
    @Schema(
            description = "Title of review posted by the user", example="Great Restaurant for Chinese Food"
    )
    private String title;

    @NotBlank(message="Comment of review posted by the user cannot be null or empty")
    @Schema(
            description = "Comment of review posted by the user", example="I went here yesterday and it was so good...."
    )
    private String comment;

    private Boolean isRecommended=false;
}
