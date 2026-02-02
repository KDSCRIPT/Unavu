package com.unavu.socialGraph.service;

import com.unavu.socialGraph.dto.SocialGraphDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISocialGraphService {

    Page<SocialGraphDto> listFollowers(Long userId, Pageable pageable);

    Page<SocialGraphDto> listFollowing(Long userId, Pageable pageable);

    Page<SocialGraphDto> listBlockedUsers(Long userId, Pageable pageable);

    Page<SocialGraphDto> listBlockedByUsers(Long userId, Pageable pageable);

    Page<SocialGraphDto> listMutedUsers(Long userId, Pageable pageable);

    void followUser(Long fromUserId,Long toUserId);

    void unFollowUser(Long fromUserId,Long toUserId);

    void muteUser(Long fromUserId,Long toUserId);

    void unMuteUser(Long fromUserId,Long toUserId);

    void blockUser(Long fromUserId,Long toUserId);

    void unBlockUser(Long fromUserId,Long toUserId);

    boolean isFollowing(Long fromUserId, Long toUserId);

    boolean isBlocked(Long fromUserId, Long toUserId);

    boolean isMuted(Long fromUserId, Long toUserId);
}
