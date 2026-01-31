package com.unavu.restaurants.service;

import com.unavu.restaurants.dto.*;
import com.unavu.restaurants.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRestaurantService {

    Page<Restaurant> restaurantList(Pageable pageable);

    void createRestaurant(CreateRestaurantDto createRestaurantDto);

    void updateRestaurant(Long id, UpdateRestaurantDto updateRestaurantDto);

    void deleteRestaurant(Long id);

    RestaurantDto getRestaurantById(Long id);

    List<RestaurantDto> searchRestaurants(SearchRestaurantDto searchRestaurantDto);
}
