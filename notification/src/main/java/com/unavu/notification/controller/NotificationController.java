package com.unavu.notification.controller;

import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.common.web.dto.ErrorResponseDto;
import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.dto.ResponseDto;
import com.unavu.notification.entity.Notification;
import com.unavu.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        name = "CRUD REST APIs for Notifications",
        description = "CRUD REST APIs to Get,Read Notifications"
)
@Slf4j
@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Fetch Notifications REST API",
            description = "REST API to fetch Notifications of current user"
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
    })
    @GetMapping("/notifications/me")
    public ResponseEntity<Page<Notification>> getMyNotifications(
            Pageable pageable
    ) {
        String userId=currentUserProvider.getCurrentUserId();
        Page<Notification> response=notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Read Notification REST API",
            description = "REST API to Read single Notification"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
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
    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<ResponseDto> markAsRead(
            @PathVariable Long id
    ) {
        notificationService.markAsRead(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        ResponseConstants.STATUS_OK,String.format(ResponseConstants.MESSAGE_OK,"Notification")
                ));
    }

    @Operation(
            summary = "Read All Notifications REST API",
            description = "REST API to Read All Notification"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
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
    @PatchMapping("/notifications/read-all")
    public ResponseEntity<ResponseDto> markAllAsRead(
    ) {

        String userId = currentUserProvider.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        ResponseConstants.STATUS_OK,String.format(ResponseConstants.MESSAGE_OK,"Notification")
                ));

    }

}