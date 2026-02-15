package com.unavu.restaurants.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
@Schema(
        name="Restaurant",
        description="Schema to hold Restaurant information"
)
public class CreateRestaurantDto {

    @NotBlank(message="Name of the restaurant cannot be null or empty")
    @Schema(
            description = "Name of the Restaurant", example="Ananda Bhavan"
    )
    private String name;

    @Schema(
            description = "Description or what does the restaurant offer", example="Authentic Karupatti Coffee"
    )
    private String description;

    @Positive(message = "Minimum cost in the restaurant must be greater than zero")
    @Schema(
            description = "Price of the minimum food item there", example="100"
    )
    private Integer minCost;


    @Positive(message = "Maximum cost in the restaurant must be greater than zero")
    @Schema(
            description = "Price of the maximum food item there", example="100"
    )
    private Integer maxCost;

    @Schema(
            description = "Full address of the restaurant"
    )
    private String address;

    @Schema(
            description = "Name of the Area the restaurant is located",example = "Nungambakkam"
    )
    private String area;
    @Schema(
            description = "Name of the city the restaurant is located", example = "Chennai"
    )
    private String city;
    @Schema(
            description = "Name of the state in which the restaurant is located",example = "TamilNadu"
    )
    private String state;

    @Schema(
            description = "Latitude coordinates of the Restaurant "
    )
    @Min(-90) @Max(90)
    private Double latitude;

    @Schema(
            description = "Longitude coordinates of the Restaurant "
    )
    @Min(-180) @Max(180)
    private Double longitude;

    private Boolean isVegOnly;

    @NotEmpty(message = "A restaurant must have some cuisine")
    @Size(max = 10, message = "A restaurant can have at most 10 cuisines")
    private List<String> cuisines;

    private String imageUrl;

    @AssertTrue(message = "Both minCost and maxCost must be provided together")
    private boolean isCostPairValid() {
        return (minCost == null && maxCost == null)
                || (minCost != null && maxCost != null);
    }

}
