package com.unavu.restaurants.repository;

import com.unavu.restaurants.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long>, JpaSpecificationExecutor<Restaurant>{

    Optional<Restaurant> findByNameAndAreaAndCity(String name,String Area, String City);

}
