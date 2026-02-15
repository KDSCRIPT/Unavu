package com.unavu.socialGraph.controller;

import com.unavu.common.web.dto.ErrorResponseDto;
import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.dto.ResponseDto;
import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.service.ISocialGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(
        name = "CRUD REST APIs for SocialGraph",
        description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE SocialGraph"
)
@Slf4j
@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class SocialGraphController {

    private final ISocialGraphService iSocialGraphService;

    @Operation(
            summary = "Follow User",
            description = "REST API to Follow user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping(value="/social-graph/follow")
    public ResponseEntity<ResponseDto> followUser(@NotNull @RequestParam Long fromUserId,@NotNull @RequestParam Long toUserId)
    {
        log.info("Creating follow from: fromUserId={} to toUserId={}",fromUserId,toUserId);
        iSocialGraphService.followUser(fromUserId,toUserId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"Social Graph Follow")));
    }

    @Operation(
            summary = "Unfollow User",
            description = "REST API to unfollow user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "NO CONTENT"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @DeleteMapping(value="/social-graph/unfollow")
    public ResponseEntity<ResponseDto> unFollowUser(@NotNull @RequestParam Long fromUserId,@NotNull @RequestParam Long toUserId)
    {
        log.info("Unfollowing from: fromUserId={} to toUserId={}",fromUserId,toUserId);
        iSocialGraphService.unFollowUser(fromUserId,toUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Mute User",
            description = "REST API to Mute user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping(value="/social-graph/mute")
    public ResponseEntity<ResponseDto> muteUser(@NotNull @RequestParam Long fromUserId,@NotNull @RequestParam Long toUserId)
    {
        log.info("Creating mute from: fromUserId={} to toUserId={}",fromUserId,toUserId);
        iSocialGraphService.muteUser(fromUserId,toUserId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"Social Graph Mute")));
    }

    @Operation(
            summary = "Unmute User",
            description = "REST API to unfollow user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "NO CONTENT"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @DeleteMapping(value="/social-graph/unmute")
    public ResponseEntity<ResponseDto> unMuteUser(@NotNull @RequestParam Long fromUserId,@NotNull @RequestParam Long toUserId)
    {
        log.info("Unmuting from: fromUserId={} to toUserId={}",fromUserId,toUserId);
        iSocialGraphService.unMuteUser(fromUserId,toUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Block User",
            description = "REST API to Mute user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping(value="/social-graph/block")
    public ResponseEntity<ResponseDto> blockUser(@NotNull @RequestParam Long fromUserId,@NotNull @RequestParam Long toUserId)
    {
        log.info("Creating block from: fromUserId={} to toUserId={}",fromUserId,toUserId);
        iSocialGraphService.blockUser(fromUserId,toUserId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"Social Graph Block")));
    }

    @Operation(
            summary = "Unblock User",
            description = "REST API to unblock user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "NO CONTENT"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @DeleteMapping(value="/social-graph/unblock")
    public ResponseEntity<ResponseDto> unBlockUser(@NotNull @RequestParam Long fromUserId,@NotNull @RequestParam Long toUserId)
    {
        log.info("UnBlocking from: fromUserId={} to toUserId={}",fromUserId,toUserId);
        iSocialGraphService.unBlockUser(fromUserId,toUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get followers of user",
            description = "REST API to Get followers of user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping(value="/social-graph/followers")
    public ResponseEntity<Page<SocialGraphDto>> getFollowersOfUser(@NotNull @RequestParam Long userId, Pageable pageable)
    {
        log.info("Getting followers of user: {}",userId);
        Page<SocialGraphDto> result=iSocialGraphService.listFollowers(userId,pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @Operation(
            summary = "Get following of user",
            description = "REST API to Get following of user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping(value="/social-graph/following")
    public ResponseEntity<Page<SocialGraphDto>> getFollowingOfUser(@NotNull @RequestParam Long userId,Pageable pageable)
    {
        log.info("Getting following of user: {}",userId);
        Page<SocialGraphDto> result =iSocialGraphService.listFollowing(userId,pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @Operation(
            summary = "Get Blocked list of user",
            description = "REST API to get blocked list of user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping(value="/social-graph/blocked")
    public ResponseEntity<Page<SocialGraphDto>> getBlockedOfUser(@NotNull @RequestParam Long userId,Pageable pageable)
    {
        log.info("Getting Blocked list of user: {}",userId);
        Page<SocialGraphDto> result =iSocialGraphService.listBlockedUsers(userId,pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @Operation(
            summary = "Get BlockedBy list of user",
            description = "REST API to get blockedBy list of user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping(value="/social-graph/blockedBy")
    public ResponseEntity<Page<SocialGraphDto>> getBlockedByOfUser(@NotNull @RequestParam Long userId,Pageable pageable)
    {
        log.info("Getting BlockedBy list of user: {}",userId);
        Page<SocialGraphDto> result =iSocialGraphService.listBlockedByUsers(userId,pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @Operation(
            summary = "Get Muted list of user",
            description = "REST API to get muted list of user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping(value="/social-graph/muted")
    public ResponseEntity<Page<SocialGraphDto>> getMutedOfUser(@NotNull @RequestParam Long userId,Pageable pageable)
    {
        log.info("Getting Mute list of user: {}",userId);
        Page<SocialGraphDto>result=iSocialGraphService.listMutedUsers(userId,pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }


}
