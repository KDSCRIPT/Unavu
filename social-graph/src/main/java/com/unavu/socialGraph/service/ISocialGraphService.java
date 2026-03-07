package com.unavu.socialGraph.service;

import com.unavu.socialGraph.dto.SocialGraphDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
