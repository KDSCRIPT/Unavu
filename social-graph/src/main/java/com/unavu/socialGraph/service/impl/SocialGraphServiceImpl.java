package com.unavu.socialGraph.service.impl;

import com.unavu.common.messaging.EventPublisher;
import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.common.web.dto.ActivityDto;
import com.unavu.common.web.dto.FeedDto;
import com.unavu.common.web.enums.ActivityType;
import com.unavu.common.web.enums.EntityType;
import com.unavu.common.web.dto.NotificationDto;
import com.unavu.common.web.enums.FeedType;
import com.unavu.common.web.enums.NotificationType;
import com.unavu.common.web.exception.ResourceActionNotAllowedException;
import com.unavu.common.web.exception.ResourceAlreadyExistsException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;
import com.unavu.socialGraph.mapper.SocialGraphMapper;
import com.unavu.socialGraph.repository.SocialGraphRepository;
import com.unavu.socialGraph.service.ISocialGraphService;
import com.unavu.socialGraph.service.client.RestaurantFeignClient;
import com.unavu.socialGraph.service.client.UserFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SocialGraphServiceImpl implements ISocialGraphService {

    private final SocialGraphRepository socialGraphRepository;
    private final UserFeignClient userFeignClient;
    private final RestaurantFeignClient restaurantFeignClient;
    private final CurrentUserProvider currentUserProvider;
    private final EventPublisher eventPublisher;

    @Override
    public Page<SocialGraphDto> listFollowers(Pageable pageable, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByTargetIdAndTargetTypeAndRelationshipType(
                actorId,
                targetType,
                RelationshipType.FOLLOW,
                pageable
        ).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listFollowing(Pageable pageable, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByActorIdAndRelationshipType(
                actorId,
                RelationshipType.FOLLOW,
                pageable
        ).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listBlockedTargets(Pageable pageable, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByActorIdAndRelationshipType(
                actorId,
                RelationshipType.BLOCK,
                pageable
        ).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listBlockedByTargets(Pageable pageable, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByTargetIdAndTargetTypeAndRelationshipType(
                actorId,
                targetType,
                RelationshipType.BLOCK,
                pageable
        ).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listMutedTargets(Pageable pageable, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByActorIdAndRelationshipType(
                actorId,
                RelationshipType.MUTE,
                pageable
        ).map(SocialGraphMapper::toDto);
    }

    @Override
    public boolean isFollowing(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.existsByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
                actorId, targetId, targetType, RelationshipType.FOLLOW
        );
    }

    @Override
    public boolean isBlocked(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.existsByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
                actorId, targetId, targetType, RelationshipType.BLOCK
        );
    }

    @Override
    public boolean isMuted(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.existsByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
                actorId, targetId, targetType, RelationshipType.MUTE
        );
    }

    @Override
    public List<String> findFollowerActorIds(String targetId, EntityType targetType) {
        return socialGraphRepository.findFollowerActorIds(targetId, targetType, RelationshipType.FOLLOW);
    }

    @Override
    @Transactional
    public void follow(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();

        validateTargetExists(targetId, targetType);

        if (targetType == EntityType.USER && actorId.equals(targetId)) {
            throw new ResourceActionNotAllowedException("User cannot follow themselves");
        }

        if (isBlocked(targetId, targetType)) {
            throw new ResourceActionNotAllowedException("Cannot follow blocked target");
        }

        if (isFollowing(targetId, targetType)) {
            throw new ResourceAlreadyExistsException("Follow", "actorId and targetId", RelationshipType.FOLLOW);
        }

        if (isMuted(targetId, targetType)) {
            unMute(targetId, targetType);
        }

        SocialGraph edge = SocialGraphMapper.toEntity(actorId, targetId, targetType, RelationshipType.FOLLOW);
        socialGraphRepository.save(edge);

        String message = String.format("%s started following you", currentUserProvider.getCurrentUserName());
        NotificationDto notification = new NotificationDto(
                targetType == EntityType.USER ? NotificationType.USER_FOLLOWED : NotificationType.RESTAURANT_FOLLOWED,
                actorId,
                targetId,
                targetType,
                edge.getId(),
                message
        );
        eventPublisher.publishNotification(notification);

        FeedDto feedEvent=new FeedDto(
                actorId,
                targetId,
                targetType == EntityType.USER ? FeedType.USER_FOLLOWED : FeedType.RESTAURANT_FOLLOWED,
                targetType,
                edge.getId(),
                message
        );
        eventPublisher.publishFeedEvent(feedEvent);

        ActivityDto activity = new ActivityDto(
                actorId,
                targetType == EntityType.USER ? ActivityType.USER_FOLLOWED : ActivityType.RESTAURANT_FOLLOWED,
                targetType,
                edge.getId(),
                String.format("You started following %s", currentUserProvider.getCurrentUserName())
        );
        eventPublisher.publishActivityEvent(activity);
    }

    @Override
    @Transactional
    public void unFollow(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();

        validateTargetExists(targetId, targetType);

        if (targetType == EntityType.USER && actorId.equals(targetId)) {
            throw new ResourceActionNotAllowedException("Cannot unfollow yourself");
        }

        if (!isFollowing(targetId, targetType)) {
            throw new ResourceNotFoundException("Follow", "actorId and targetId", RelationshipType.FOLLOW);
        }

        socialGraphRepository.deleteByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
                actorId, targetId, targetType, RelationshipType.FOLLOW
        );

        ActivityDto activity = new ActivityDto(
                actorId,
                targetType == EntityType.USER ? ActivityType.USER_UNFOLLOWED : ActivityType.RESTAURANT_UNFOLLOWED,
                targetType,
                0L,
                String.format("You unfollowed %s", currentUserProvider.getCurrentUserName())
        );
        eventPublisher.publishActivityEvent(activity);
    }

    @Override
    @Transactional
    public void mute(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        validateTargetExists(targetId, targetType);

        if (targetType == EntityType.USER && actorId.equals(targetId)) {
            throw new ResourceActionNotAllowedException("Cannot mute yourself");
        }

        if (isBlocked(targetId, targetType)) {
            throw new ResourceActionNotAllowedException("Cannot mute blocked target");
        }

        if (isMuted(targetId, targetType)) {
            throw new ResourceAlreadyExistsException("Mute", "actorId and targetId", RelationshipType.MUTE);
        }

        // Remove existing follow
        if (isFollowing(targetId, targetType)) {
            unFollow(targetId, targetType);
        }

        SocialGraph edge = SocialGraphMapper.toEntity(actorId, targetId, targetType, RelationshipType.MUTE);
        socialGraphRepository.save(edge);
    }

    @Override
    @Transactional
    public void unMute(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        validateTargetExists(targetId, targetType);

        if (targetType == EntityType.USER && actorId.equals(targetId)) {
            throw new ResourceActionNotAllowedException("Cannot unmute yourself");
        }

        if (!isMuted(targetId, targetType)) {
            throw new ResourceNotFoundException("Mute", "actorId and targetId", RelationshipType.MUTE);
        }

        socialGraphRepository.deleteByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
                actorId, targetId, targetType, RelationshipType.MUTE
        );
    }

    @Override
    @Transactional
    public void block(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        validateTargetExists(targetId, targetType);

        if (targetType == EntityType.USER && actorId.equals(targetId)) {
            throw new ResourceActionNotAllowedException("Cannot block yourself");
        }

        if (isBlocked(targetId, targetType) || isBlocked(actorId, targetType)) {
            throw new ResourceAlreadyExistsException("Block", "actorId and targetId", RelationshipType.BLOCK);
        }

        // Remove existing follow or mute
        if (isMuted(targetId, targetType)) unMute(targetId, targetType);
        if (isFollowing(targetId, targetType)) unFollow(targetId, targetType);

        SocialGraph edge = SocialGraphMapper.toEntity(actorId, targetId, targetType, RelationshipType.BLOCK);
        socialGraphRepository.save(edge);
    }

    @Override
    @Transactional
    public void unBlock(String targetId, EntityType targetType) {
        String actorId = currentUserProvider.getCurrentUserId();
        validateTargetExists(targetId, targetType);

        if (targetType == EntityType.USER && actorId.equals(targetId)) {
            throw new ResourceActionNotAllowedException("Cannot unblock yourself");
        }

        if (!isBlocked(targetId, targetType)) {
            throw new ResourceNotFoundException("Block", "actorId and targetId", RelationshipType.BLOCK);
        }

        socialGraphRepository.deleteByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
                actorId, targetId, targetType, RelationshipType.BLOCK
        );
    }

    private void validateTargetExists(String targetId, EntityType targetType) {
        switch (targetType) {
            case USER -> {
                if (!userFeignClient.userWithKeycloakIdExists(targetId)) {
                    throw new ResourceNotFoundException("User", "id", targetId);
                }
            }
            case RESTAURANT -> {
                if (!restaurantFeignClient.doesRestaurantExist(Long.valueOf(targetId))) {
                    throw new ResourceNotFoundException("Restaurant", "id", targetId);
                }
            }
            default -> throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }
}