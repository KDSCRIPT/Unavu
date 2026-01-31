package com.unavu.restaurants.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRestaurantDto {
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

    @Size(max = 10, message = "A restaurant can have at most 10 cuisines")
    private List<String> cuisines;

    private String imageUrl;

}
