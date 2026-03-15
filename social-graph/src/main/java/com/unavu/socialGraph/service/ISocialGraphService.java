package com.unavu.socialGraph.service;

import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.entity.RelationshipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ISocialGraphService {

    Page<SocialGraphDto> listFollowers(Pageable pageable);

    Page<SocialGraphDto> listFollowing(Pageable pageable);

    Page<SocialGraphDto> listBlockedUsers(Pageable pageable);

    Page<SocialGraphDto> listBlockedByUsers(Pageable pageable);

    Page<SocialGraphDto> listMutedUsers(Pageable pageable);

    void followUser(String toUserId);

    void unFollowUser(String toUserId);

    void muteUser(String toUserId);

    void unMuteUser(String toUserId);

    void blockUser(String toUserId);

    void unBlockUser(String toUserId);

    boolean isFollowing(String toUserId);

    boolean isBlocked(String toUserId);

    boolean isMuted(String toUserId);

    List<String> findFollowerIds(String userId);
}
