package com.unavu.restaurants.mapper;

import com.unavu.restaurants.dto.CreateRestaurantDto;
import com.unavu.restaurants.dto.RestaurantDto;
import com.unavu.restaurants.dto.UpdateRestaurantDto;
import com.unavu.restaurants.entity.Restaurant;

public class RestaurantMapper {

    public static Restaurant toEntity(CreateRestaurantDto createRestaurantDto)
    {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(createRestaurantDto.getName());
        restaurant.setDescription(createRestaurantDto.getDescription());
        restaurant.setMinCost(createRestaurantDto.getMinCost());
        restaurant.setMaxCost(createRestaurantDto.getMaxCost());
        restaurant.setAddress(createRestaurantDto.getAddress());
        restaurant.setArea(createRestaurantDto.getArea());
        restaurant.setCity(createRestaurantDto.getCity());
        restaurant.setState(createRestaurantDto.getState());
        restaurant.setLatitude(createRestaurantDto.getLatitude());
        restaurant.setLongitude(createRestaurantDto.getLongitude());
        restaurant.setIsVegOnly(createRestaurantDto.getIsVegOnly() != null
                ? createRestaurantDto.getIsVegOnly()
                : false);
        restaurant.setCuisines(createRestaurantDto.getCuisines());
        restaurant.setImageUrl(createRestaurantDto.getImageUrl());
        return restaurant;

    }

    public static void updateEntity(UpdateRestaurantDto updateRestaurantDto, Restaurant restaurant)
    {
        if(updateRestaurantDto.getName()!=null)restaurant.setName(updateRestaurantDto.getName());
        if(updateRestaurantDto.getDescription()!=null)restaurant.setDescription(updateRestaurantDto.getDescription());
        if(updateRestaurantDto.getMinCost()!=null)restaurant.setMinCost(updateRestaurantDto.getMinCost());
        if(updateRestaurantDto.getMaxCost()!=null)restaurant.setMaxCost(updateRestaurantDto.getMaxCost());
        if(updateRestaurantDto.getAddress()!=null)restaurant.setAddress(updateRestaurantDto.getAddress());
        if(updateRestaurantDto.getArea()!=null)restaurant.setArea(updateRestaurantDto.getArea());
        if(updateRestaurantDto.getCity()!=null)restaurant.setCity(updateRestaurantDto.getCity());
        if(updateRestaurantDto.getState()!=null)restaurant.setState(updateRestaurantDto.getState());
        if(updateRestaurantDto.getLatitude()!=null)restaurant.setLatitude(updateRestaurantDto.getLatitude());
        if(updateRestaurantDto.getLongitude()!=null)restaurant.setLongitude(updateRestaurantDto.getLongitude());
        if (updateRestaurantDto.getIsVegOnly()!=null) restaurant.setIsVegOnly(updateRestaurantDto.getIsVegOnly());
        if(updateRestaurantDto.getCuisines()!=null)restaurant.setCuisines(updateRestaurantDto.getCuisines());
        if(updateRestaurantDto.getImageUrl()!=null)restaurant.setImageUrl(updateRestaurantDto.getImageUrl());
    }

    public static RestaurantDto toDto(Restaurant restaurant)
    {
        RestaurantDto restaurantDto=new RestaurantDto();
        restaurantDto.setId(restaurant.getId());
        restaurantDto.setName(restaurant.getName());
        restaurantDto.setCity(restaurant.getCity());
        restaurantDto.setArea(restaurant.getArea());
        restaurantDto.setCuisines(restaurant.getCuisines());
        restaurantDto.setIsVegOnly(restaurant.getIsVegOnly());
        restaurantDto.setMinCost(restaurant.getMinCost());
        restaurantDto.setMaxCost(restaurant.getMaxCost());
        restaurantDto.setImageUrl(restaurant.getImageUrl());
        return restaurantDto;
    }
}
