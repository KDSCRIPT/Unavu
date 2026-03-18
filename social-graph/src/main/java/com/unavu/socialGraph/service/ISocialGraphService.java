package com.unavu.socialGraph.service;

import com.unavu.common.web.enums.EntityType;
import com.unavu.socialGraph.dto.SocialGraphDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISocialGraphService {

    Page<SocialGraphDto> listFollowers(Pageable pageable, EntityType targetType);

    Page<SocialGraphDto> listFollowing(Pageable pageable, EntityType targetType);

    Page<SocialGraphDto> listBlockedTargets(Pageable pageable, EntityType targetType);

    Page<SocialGraphDto> listBlockedByTargets(Pageable pageable, EntityType targetType);

    Page<SocialGraphDto> listMutedTargets(Pageable pageable, EntityType targetType);

    boolean isFollowing(String targetId, EntityType targetType);

    boolean isBlocked(String targetId, EntityType targetType);

    boolean isMuted(String targetId, EntityType targetType);

    List<String> findFollowerActorIds(String targetId, EntityType targetType);

    void follow(String targetId, EntityType targetType);

    void unFollow(String targetId, EntityType targetType);

    void mute(String targetId, EntityType targetType);

    void unMute(String targetId, EntityType targetType);

    void block(String targetId, EntityType targetType);

    void unBlock(String targetId, EntityType targetType);

    default void followUser(String userId) {
        follow(userId, EntityType.USER);
    }

    default void unFollowUser(String userId) {
        unFollow(userId, EntityType.USER);
    }

    default void blockUser(String userId) {
        block(userId, EntityType.USER);
    }

    default void unBlockUser(String userId) {
        unBlock(userId, EntityType.USER);
    }

    default void muteUser(String userId) {
        mute(userId, EntityType.USER);
    }

    default void unMuteUser(String userId) {
        unMute(userId, EntityType.USER);
    }

    default void followRestaurant(String restaurantId) {
        follow(restaurantId, EntityType.RESTAURANT);
    }

    default void unFollowRestaurant(String restaurantId) {
        unFollow(restaurantId, EntityType.RESTAURANT);
    }

    default void blockRestaurant(String restaurantId) {
        block(restaurantId, EntityType.RESTAURANT);
    }

    default void unBlockRestaurant(String restaurantId) {
        unBlock(restaurantId, EntityType.RESTAURANT);
    }

    default void muteRestaurant(String restaurantId) {
        mute(restaurantId, EntityType.RESTAURANT);
    }

    default void unMuteRestaurant(String restaurantId) {
        unMute(restaurantId, EntityType.RESTAURANT);
    }
}