package com.unavu.socialGraph.service.impl;

import com.unavu.common.web.exception.ResourceActionNotAllowedException;
import com.unavu.common.web.exception.ResourceAlreadyExistsException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;
import com.unavu.socialGraph.mapper.SocialGraphMapper;
import com.unavu.socialGraph.provider.CurrentUserProvider;
import com.unavu.socialGraph.repository.SocialGraphRepository;
import com.unavu.socialGraph.service.ISocialGraphService;
import com.unavu.socialGraph.service.client.UserFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class SocialGraphServiceImpl implements ISocialGraphService {

    private final SocialGraphRepository socialGraphRepository;
    private UserFeignClient userFeignClient;
    private CurrentUserProvider currentUserProvider;
    @Override
    public Page<SocialGraphDto> listFollowers(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByToUserIdAndRelationshipType(userId, RelationshipType.FOLLOW, pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listFollowing(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByFromUserIdAndRelationshipType(userId, RelationshipType.FOLLOW, pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listBlockedUsers(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByFromUserIdAndRelationshipType(userId, RelationshipType.BLOCK, pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listBlockedByUsers(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByToUserIdAndRelationshipType(userId,RelationshipType.BLOCK,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listMutedUsers(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.findByFromUserIdAndRelationshipType(userId,RelationshipType.MUTE,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    @Transactional
    public void followUser(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        if (!userFeignClient.userWithKeycloakIdExists(toUserId)) {
            throw new ResourceNotFoundException("toUser","id", toUserId);
        }
        if (isBlocked(toUserId)) {
            throw new ResourceActionNotAllowedException(
                    "User " + fromUserId + " cannot follow user " + toUserId + " due to block"
            );
        }
        if (isFollowing(toUserId)) {
            throw new ResourceAlreadyExistsException("Follow","fromUserId and toUserId", RelationshipType.FOLLOW);
        }
        SocialGraph socialGraph= SocialGraphMapper.toEntity(fromUserId,toUserId,RelationshipType.FOLLOW);
        socialGraphRepository.save(socialGraph);
    }

    @Override
    @Transactional
    public void unFollowUser(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        if (!userFeignClient.userWithKeycloakIdExists(toUserId)) {
            throw new ResourceNotFoundException("toUser","id", toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (!isFollowing(toUserId)) {
            throw new ResourceNotFoundException("Follow","fromUserId and toUserId", RelationshipType.FOLLOW);
        }
        socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId,RelationshipType.FOLLOW);
    }

    @Override
    @Transactional
    public void muteUser(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        if (!userFeignClient.userWithKeycloakIdExists(toUserId)) {
            throw new ResourceNotFoundException("toUser","id", toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (isBlocked(toUserId)) {
            throw new ResourceActionNotAllowedException("Blocked users cannot be muted");
        }
        else if (isMuted(toUserId)) {
            throw new ResourceAlreadyExistsException("Mute","fromUserId and toUserId",RelationshipType.MUTE);
        }
        else if(isFollowing(toUserId))
        {
            socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId, toUserId,RelationshipType.FOLLOW);
        }
        SocialGraph newEdge=SocialGraphMapper.toEntity(fromUserId,toUserId,RelationshipType.MUTE);
        socialGraphRepository.save(newEdge);
    }
    @Override
    @Transactional
    public void unMuteUser(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        if (!userFeignClient.userWithKeycloakIdExists(toUserId)) {
            throw new ResourceNotFoundException("toUser","id", toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (!isMuted(toUserId)) {
            throw new ResourceNotFoundException("Mute","fromUserId and toUserId",RelationshipType.MUTE);
        }
        socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId,RelationshipType.MUTE);
    }


    @Override
    @Transactional
    public void blockUser(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        if (!userFeignClient.userWithKeycloakIdExists(toUserId)) {
            throw new ResourceNotFoundException("toUser","id", toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (isBlocked(toUserId) || isBlocked(fromUserId)) {
            throw new ResourceAlreadyExistsException("Block","fromUserId and toUserId",RelationshipType.BLOCK);
        }
        else if(isMuted(toUserId))
        {
            socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId, toUserId,RelationshipType.MUTE);
        }
        else if(isFollowing(toUserId))
        {
            socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId, toUserId,RelationshipType.FOLLOW);
        }
        SocialGraph newEdge=SocialGraphMapper.toEntity(fromUserId,toUserId,RelationshipType.BLOCK);
        socialGraphRepository.save(newEdge);
    }

    @Override
    @Transactional
    public void unBlockUser(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        if (!userFeignClient.userWithKeycloakIdExists(toUserId)) {
            throw new ResourceNotFoundException("toUser","id", toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (!isBlocked(toUserId)) {
            throw new ResourceNotFoundException("Block","fromUserId and toUserId",RelationshipType.BLOCK);
        }
        socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId,RelationshipType.BLOCK);
    }

    @Override
    public boolean isFollowing(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.existsByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId, RelationshipType.FOLLOW);
    }

    @Override
    public boolean isBlocked(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.existsByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId, RelationshipType.BLOCK);
    }

    @Override
    public boolean isMuted(String toUserId) {
        String fromUserId = currentUserProvider.getCurrentUserId();
        return socialGraphRepository.existsByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId, RelationshipType.MUTE);
    }
}
