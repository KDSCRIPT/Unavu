package com.unavu.socialGraph.service.impl;

import com.unavu.common.web.exception.ResourceActionNotAllowedException;
import com.unavu.common.web.exception.ResourceAlreadyExistsException;
import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;
import com.unavu.socialGraph.mapper.SocialGraphMapper;
import com.unavu.socialGraph.repository.SocialGraphRepository;
import com.unavu.socialGraph.service.ISocialGraphService;
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
    @Override
    public Page<SocialGraphDto> listFollowers(Long userId, Pageable pageable) {
        return socialGraphRepository.findByToUserIdAndRelationshipType(userId,RelationshipType.FOLLOW,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listFollowing(Long userId, Pageable pageable) {
        return socialGraphRepository.findByFromUserIdAndRelationshipType(userId,RelationshipType.FOLLOW,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listBlockedUsers(Long userId, Pageable pageable) {
        return socialGraphRepository.findByFromUserIdAndRelationshipType(userId,RelationshipType.BLOCK,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listBlockedByUsers(Long userId, Pageable pageable) {

        return socialGraphRepository.findByToUserIdAndRelationshipType(userId,RelationshipType.BLOCK,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    public Page<SocialGraphDto> listMutedUsers(Long userId, Pageable pageable) {

        return socialGraphRepository.findByFromUserIdAndRelationshipType(userId,RelationshipType.MUTE,pageable).map(SocialGraphMapper::toDto);
    }

    @Override
    @Transactional
    public void followUser(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (isBlocked(fromUserId, toUserId)) {
            throw new ResourceActionNotAllowedException(
                    "User " + fromUserId + " cannot follow user " + toUserId + " due to block"
            );
        }
        if (isFollowing(fromUserId, toUserId)) {
            throw new ResourceAlreadyExistsException("Follow","fromUserId and toUserId", RelationshipType.FOLLOW);
        }
        SocialGraph socialGraph= SocialGraphMapper.toEntity(fromUserId,toUserId,RelationshipType.FOLLOW);
        socialGraphRepository.save(socialGraph);
    }

    @Override
    @Transactional
    public void unFollowUser(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (!isFollowing(fromUserId, toUserId)) {
            throw new ResourceNotFoundException("Follow","fromUserId and toUserId", RelationshipType.FOLLOW);
        }
        socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId,RelationshipType.FOLLOW);
    }

    @Override
    @Transactional
    public void muteUser(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (isBlocked(fromUserId, toUserId)) {
            throw new ResourceActionNotAllowedException("Blocked users cannot be muted");
        }
        else if (isMuted(fromUserId, toUserId)) {
            throw new ResourceAlreadyExistsException("Mute","fromUserId and toUserId",RelationshipType.MUTE);
        }
        else if(isFollowing(fromUserId,toUserId))
        {
            socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId, toUserId,RelationshipType.FOLLOW);
        }
        SocialGraph newEdge=SocialGraphMapper.toEntity(fromUserId,toUserId,RelationshipType.MUTE);
        socialGraphRepository.save(newEdge);
    }
    @Override
    @Transactional
    public void unMuteUser(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (!isMuted(fromUserId, toUserId)) {
            throw new ResourceNotFoundException("Mute","fromUserId and toUserId",RelationshipType.MUTE);
        }
        socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId,RelationshipType.MUTE);
    }


    @Override
    @Transactional
    public void blockUser(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (isBlocked(fromUserId, toUserId) || isBlocked(toUserId, fromUserId)) {
            throw new ResourceAlreadyExistsException("Block","fromUserId and toUserId",RelationshipType.BLOCK);
        }
        else if(isMuted(fromUserId,toUserId))
        {
            socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId, toUserId,RelationshipType.MUTE);
        }
        else if(isFollowing(fromUserId,toUserId))
        {
            socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId, toUserId,RelationshipType.FOLLOW);
        }
        SocialGraph newEdge=SocialGraphMapper.toEntity(fromUserId,toUserId,RelationshipType.BLOCK);
        socialGraphRepository.save(newEdge);
    }

    @Override
    @Transactional
    public void unBlockUser(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResourceActionNotAllowedException("User cannot perform this action on themselves");
        }
        if (!isBlocked(fromUserId, toUserId)) {
            throw new ResourceNotFoundException("Block","fromUserId and toUserId",RelationshipType.BLOCK);
        }
        socialGraphRepository.deleteByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId,RelationshipType.BLOCK);
    }

    @Override
    public boolean isFollowing(Long fromUserId, Long toUserId) {
        return socialGraphRepository.existsByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId, RelationshipType.FOLLOW);
    }

    @Override
    public boolean isBlocked(Long fromUserId, Long toUserId) {
        return socialGraphRepository.existsByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId, RelationshipType.BLOCK);
    }

    @Override
    public boolean isMuted(Long fromUserId, Long toUserId) {
        return socialGraphRepository.existsByFromUserIdAndToUserIdAndRelationshipType(fromUserId,toUserId, RelationshipType.MUTE);
    }
}
