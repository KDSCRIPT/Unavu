package com.unavu.lists.controller;


import com.unavu.lists.constants.UserListConstants;
import com.unavu.lists.dto.*;
import com.unavu.lists.entity.ListVisibility;
import com.unavu.lists.service.IUserListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
        name = "CRUD REST APIs for User List",
        description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE User List"
)
@Slf4j
@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class UserListController {

    private final IUserListService iUserListService;

    @Operation(
            summary = "Create User List",
            description = "REST API to Create User List"
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
    @PostMapping(value="/lists")
    public ResponseEntity<ResponseDto> createUserList(@Valid @RequestBody CreateUserListDto createUserListDto)
    {
        iUserListService.createUserList(createUserListDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(UserListConstants.STATUS_201,UserListConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Update UserList details REST API",
            description = "REST API to update UserList details"
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
    @PatchMapping(value="/lists/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateUserListDetails(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserListDto updateUserListDto
            ) {
        iUserListService.updateUserList(id,updateUserListDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        UserListConstants.STATUS_200,
                        UserListConstants.MESSAGE_200
                ));
    }

    @Operation(
            summary = "Delete User List REST API",
            description = "REST API to delete User List details by ID"
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
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @DeleteMapping(value="/lists/{id}")
    public ResponseEntity<ResponseDto> deleteUserList(@PathVariable long id) {
        iUserListService.deleteUserList(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add Item to User List",
            description = "REST API to add Item to User List"
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
    @PostMapping(value="/lists/item")
    public ResponseEntity<ResponseDto> addUserItemToList(@Valid @RequestBody AddItemToUserListDto addItemToUserListDto)
    {
        iUserListService.addItemToList(addItemToUserListDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(UserListConstants.STATUS_201,UserListConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Delete Item from User List REST API",
            description = "REST API to delete Item from User List details by ID"
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
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @DeleteMapping(value="/lists/item/{id}")
    public ResponseEntity<ResponseDto> deleteUserItemFromList(@PathVariable long id) {
        iUserListService.removeItemFromList(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Fetch Lists by visibility REST API",
            description = "REST API to fetch Lists by visibility"
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
    @GetMapping("/lists")
    public ResponseEntity<Page<UserListDto>> getListsByVisibility(
            @RequestParam ListVisibility listVisibility,
            Pageable pageable
    )
    {

        return ResponseEntity.ok(iUserListService.getListsByVisibility(listVisibility,pageable));
    }

    @Operation(
            summary = "Fetch Lists by visibility REST API",
            description = "REST API to fetch Lists by visibility"
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
    @GetMapping("/users/{userId}/lists")
    public ResponseEntity<Page<UserListDto>> getListsByOwnerAndVisibility(
            @PathVariable Long userId,
            @RequestParam(required = false) ListVisibility listVisibility,
            Pageable pageable
    )
    {
        if(listVisibility==null) {
            return ResponseEntity.ok(iUserListService.getListsByOwner(userId, pageable));
        }
        return ResponseEntity.ok(iUserListService.getListsByOwnerAndVisibility(userId, listVisibility, pageable));
    }
    
    @Operation(
            summary = "Fetch Lists By Id REST API",
            description = "REST API to fetch Lists By Id"
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
    @GetMapping("/lists/{id}")
    public ResponseEntity<UserListDto> getListById(
            @PathVariable Long id
    )
    {
        /// need to add security feature
        return ResponseEntity.ok(iUserListService.getListById(id));
    }

}
