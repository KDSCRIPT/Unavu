package com.unavu.restaurants.service.impl;

import com.unavu.restaurants.entity.Restaurant;
import org.springframework.data.jpa.domain.Specification;

public class RestaurantSpecification {

    public static Specification<Restaurant> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Restaurant> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null :
                        cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<Restaurant> hasArea(String area) {
        return (root, query, cb) ->
                area == null ? null :
                        cb.equal(cb.lower(root.get("area")), area.toLowerCase());
    }

    public static Specification<Restaurant> hasState(String state) {
        return (root, query, cb) ->
                state == null ? null :
                        cb.equal(cb.lower(root.get("state")), state.toLowerCase());
    }

    public static Specification<Restaurant> isVegOnly(Boolean isVegOnly) {
        return (root, query, cb) ->
                isVegOnly == null ? null :
                        cb.equal(root.get("isVegOnly"), isVegOnly);
    }

    public static Specification<Restaurant> hasCuisine(String cuisine) {
        return (root, query, cb) ->
                cuisine == null ? null :
                        cb.isMember(cuisine, root.get("cuisines"));
    }
}
