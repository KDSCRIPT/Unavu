package com.unavu.restaurants.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private String city;
    private String area;
    private List<String> cuisines;
    private Boolean isVegOnly;
    private Integer minCost;
    private Integer maxCost;
    private String imageUrl;
}
