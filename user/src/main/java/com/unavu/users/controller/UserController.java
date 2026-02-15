package com.unavu.users.controller;

import com.unavu.common.web.dto.ErrorResponseDto;
import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.dto.ResponseDto;
import com.unavu.users.dto.*;
import com.unavu.users.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
        name = "CRUD REST APIs for Users",
        description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE Users"
)
@Slf4j
@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class UserController {

    private final IUserService iUserService;
    @Operation(
            summary = "Create User REST API",
            description = "REST API to create new User"
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
    @PostMapping(value="/users",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createUser(@Valid @RequestBody CreateUserDto createUserDto, @RequestParam String keyCloakId)
    {
        log.info("Creating user: keyCloakId={}, displayName={}, description={}",
                keyCloakId,
                createUserDto.getDisplayName(),
                createUserDto.getDescription());

        iUserService.createUser(keyCloakId,createUserDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"User")));
    }

    @Operation(
            summary = "Search User REST API",
            description = "REST API to search User based on displayName"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping(value="/users/search")
    public ResponseEntity<Page<UserDto>>searchUser(
            @NotBlank @RequestParam String searchTerm,
            Pageable pageable
    ) {
        log.info("Searching User with displayName substring : {}", searchTerm);
        return ResponseEntity.status(HttpStatus.OK).body(
                iUserService.searchUsers(searchTerm,pageable)
        );
    }

    @Operation(
            summary = "Fetch User by displayName REST API",
            description = "REST API to fetch User details based on displayName"
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
    @GetMapping("/users/by-display-name/{displayName}")
    public ResponseEntity<UserDto> findUserByDisplayName(
            @NotBlank @PathVariable String displayName
    )
    {
            return ResponseEntity.ok(iUserService.getUserByDisplayName(displayName));
    }

    @Operation(
            summary = "Fetch User by keyCloakId REST API",
            description = "REST API to fetch User details based on keyCloakId"
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
    @GetMapping("/users")
    public ResponseEntity<UserDto> findUserByKeyCloakId(
            @RequestParam String keyCloakId
    )
    {
        return ResponseEntity.ok(iUserService.getUserByKeyCloakId(keyCloakId));
    }

    @Operation(
            summary = "Update User details REST API",
            description = "REST API to update User details"
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
    @PatchMapping(value="/users/me",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateUserDetails(
            @RequestParam String keyCloakId,
            @Valid @RequestBody UpdateUserDto updateUserDto
            ) {
        log.info("Updating user with keyCloakId={}", keyCloakId);
        iUserService.updateUser(keyCloakId,updateUserDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        ResponseConstants.STATUS_OK,String.format(ResponseConstants.MESSAGE_OK,"User")
                ));
    }

    @Operation(
            summary = "Delete User REST API",
            description = "REST API to delete User details by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "HTTP Status No Content"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @DeleteMapping(value="/users/me")
    public ResponseEntity<ResponseDto> deleteUser(@RequestParam String keyCloakId) {
        log.info("Deleting User with keyCloakId={}", keyCloakId);
        iUserService.deleteUser(keyCloakId);
        return ResponseEntity.noContent().build();
    }
}
