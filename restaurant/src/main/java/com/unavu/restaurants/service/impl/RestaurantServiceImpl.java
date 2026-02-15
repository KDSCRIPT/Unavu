package com.unavu.restaurants.service.impl;

import com.unavu.common.web.exception.ResourceAlreadyExistsException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.restaurants.dto.CreateRestaurantDto;
import com.unavu.restaurants.dto.RestaurantDto;
import com.unavu.restaurants.dto.SearchRestaurantDto;
import com.unavu.restaurants.dto.UpdateRestaurantDto;
import com.unavu.restaurants.entity.Restaurant;
import com.unavu.restaurants.mapper.RestaurantMapper;
import com.unavu.restaurants.repository.RestaurantRepository;
import com.unavu.restaurants.service.IRestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class RestaurantServiceImpl implements IRestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Boolean doesRestaurantExistWithId(Long restaurantId) {
        return restaurantRepository.existsById(restaurantId);
    }

    @Override
    public Page<RestaurantDto> restaurantList(Pageable pageable) {
        log.info("Fetching restaurant list with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return restaurantRepository.findAll(pageable).map(RestaurantMapper::toDto);
    }

    @Override
    @Transactional
    public void createRestaurant(CreateRestaurantDto createRestaurantDto) {
        log.info("Creating restaurant: name={}, area={}, city={}",
                createRestaurantDto.getName(),
                createRestaurantDto.getArea(),
                createRestaurantDto.getCity());

        Optional<Restaurant> optionalRestaurant =
                restaurantRepository.findByNameAndAreaAndCity(
                        createRestaurantDto.getName(),
                        createRestaurantDto.getArea(),
                        createRestaurantDto.getCity()
                );

        if (optionalRestaurant.isPresent()) {
            log.warn("Duplicate restaurant detected: name={}, area={}, city={}",
                    createRestaurantDto.getName(),
                    createRestaurantDto.getArea(),
                    createRestaurantDto.getCity());
            throw new ResourceAlreadyExistsException(
                    "Restaurant","name, area and city",optionalRestaurant
            );
        }

        Restaurant restaurant = RestaurantMapper.toEntity(createRestaurantDto);
        restaurantRepository.save(restaurant);

        log.info("Restaurant created successfully with id={}", restaurant.getId());
    }

    @Override
    @Transactional
    public void updateRestaurant(Long id, UpdateRestaurantDto updateRestaurantDto) {
        log.info("Updating restaurant with id={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Restaurant not found for update, id={}", id);
                    return new ResourceNotFoundException("Restaurant", "id", id.toString());
                });

        RestaurantMapper.updateEntity(updateRestaurantDto, restaurant);
        restaurantRepository.save(restaurant);

        log.info("Restaurant updated successfully, id={}", id);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant with id={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Restaurant not found for delete, id={}", id);
                    return new ResourceNotFoundException("Restaurant", "id", id.toString());
                });

        restaurantRepository.delete(restaurant);
        log.info("Restaurant deleted successfully, id={}", id);
    }

    @Override
    public RestaurantDto getRestaurantById(Long id) {
        log.info("Fetching restaurant by id={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Restaurant not found for fetch, id={}", id);
                    return new ResourceNotFoundException("Restaurant", "id", id.toString());
                });

        return RestaurantMapper.toDto(restaurant);
    }

    @Override
    public Page<RestaurantDto> searchRestaurants(SearchRestaurantDto searchRestaurantDto,Pageable pageable) {
        log.info("Searching restaurants with filters: name={}, city={}, area={}, state={}, vegOnly={}",
                searchRestaurantDto.getName(),
                searchRestaurantDto.getCity(),
                searchRestaurantDto.getArea(),
                searchRestaurantDto.getState(),
                searchRestaurantDto.getIsVegOnly());

        Specification<Restaurant> spec = Specification
                .where(RestaurantSpecification.hasName(searchRestaurantDto.getName()))
                .and(RestaurantSpecification.hasCity(searchRestaurantDto.getCity()))
                .and(RestaurantSpecification.hasArea(searchRestaurantDto.getArea()))
                .and(RestaurantSpecification.hasState(searchRestaurantDto.getState()))
                .and(RestaurantSpecification.isVegOnly(searchRestaurantDto.getIsVegOnly()))
                .and(RestaurantSpecification.hasCuisine(searchRestaurantDto.getCuisine()));

        return restaurantRepository.findAll(spec,pageable)
                .map(RestaurantMapper::toDto);
    }
}
