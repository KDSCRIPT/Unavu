package com.unavu.restaurants.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
        name="Restaurant Search criteria",
        description="Schema to hold Restaurant Search Criteria"
)
public class SearchRestaurantDto {
    private String name;
    private String city;
    private String area;
    private String state;
    private String cuisine;
    private Boolean isVegOnly;
}
