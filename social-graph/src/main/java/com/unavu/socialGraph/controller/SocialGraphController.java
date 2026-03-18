package com.unavu.socialGraph.controller;

import com.unavu.common.web.dto.ResponseDto;
import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.enums.EntityType;
import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.service.ISocialGraphService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Validated
public class SocialGraphController {

    private final ISocialGraphService socialGraphService;

    @PostMapping("/social-graph/follow")
    @Operation(summary = "Follow a target (User or Restaurant)")
    public ResponseEntity<ResponseDto> follow(
            @NotNull @RequestParam String targetId,
            @RequestParam(defaultValue = "USER") EntityType entityType
    ) {
        log.info("Following targetId={} type={}", targetId, entityType);
        socialGraphService.follow(targetId, entityType);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,
                        String.format(ResponseConstants.MESSAGE_CREATED, "Follow")));
    }

    @DeleteMapping("/social-graph/unfollow")
    @Operation(summary = "Unfollow a target (User or Restaurant)")
    public ResponseEntity<ResponseDto> unFollow(
            @NotNull @RequestParam String targetId,
            @RequestParam(defaultValue = "USER") EntityType entityType
    ) {
        log.info("Unfollowing targetId={} type={}", targetId, entityType);
        socialGraphService.unFollow(targetId, entityType);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/social-graph/mute")
    public ResponseEntity<ResponseDto> mute(
            @NotNull @RequestParam String targetId,
            @RequestParam(defaultValue = "USER") EntityType entityType
    ) {
        log.info("Muting targetId={} type={}", targetId, entityType);
        socialGraphService.mute(targetId, entityType);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,
                        String.format(ResponseConstants.MESSAGE_CREATED, "Mute")));
    }

    @DeleteMapping("/social-graph/unmute")
    public ResponseEntity<ResponseDto> unMute(
            @NotNull @RequestParam String targetId,
            @RequestParam(defaultValue = "USER") EntityType entityType
    ) {
        log.info("Unmuting targetId={} type={}", targetId, entityType);
        socialGraphService.unMute(targetId, entityType);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/social-graph/block")
    public ResponseEntity<ResponseDto> block(
            @NotNull @RequestParam String targetId,
            @RequestParam(defaultValue = "USER") EntityType entityType
    ) {
        log.info("Blocking targetId={} type={}", targetId, entityType);
        socialGraphService.block(targetId, entityType);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,
                        String.format(ResponseConstants.MESSAGE_CREATED, "Block")));
    }

    @DeleteMapping("/social-graph/unblock")
    public ResponseEntity<ResponseDto> unBlock(
            @NotNull @RequestParam String targetId,
            @RequestParam(defaultValue = "USER") EntityType entityType
    ) {
        log.info("Unblocking targetId={} type={}", targetId, entityType);
        socialGraphService.unBlock(targetId, entityType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/social-graph/followers/{userId}")
    public ResponseEntity<List<String>> findFollowerIds(@PathVariable String userId) {
        log.info("findFollowerIds called with userId={}", userId);
        List<String> ids = socialGraphService.findFollowerActorIds(userId, EntityType.USER);
        log.info("findFollowerIds returning ids={}", ids);
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/social-graph/followers")
    public ResponseEntity<Page<SocialGraphDto>> getFollowers(Pageable pageable) {
        Page<SocialGraphDto> result = socialGraphService.listFollowers(pageable, EntityType.USER);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/social-graph/following")
    public ResponseEntity<Page<SocialGraphDto>> getFollowing(Pageable pageable) {
        Page<SocialGraphDto> result = socialGraphService.listFollowing(pageable, EntityType.USER);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/social-graph/blocked")
    public ResponseEntity<Page<SocialGraphDto>> getBlocked(Pageable pageable) {
        Page<SocialGraphDto> result = socialGraphService.listBlockedTargets(pageable, EntityType.USER);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/social-graph/blockedBy")
    public ResponseEntity<Page<SocialGraphDto>> getBlockedBy(Pageable pageable) {
        Page<SocialGraphDto> result = socialGraphService.listBlockedByTargets(pageable, EntityType.USER);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/social-graph/muted")
    public ResponseEntity<Page<SocialGraphDto>> getMuted(Pageable pageable) {
        Page<SocialGraphDto> result = socialGraphService.listMutedTargets(pageable, EntityType.USER);
        return ResponseEntity.ok(result);
    }
}