package com.unavu.lists.controller;

import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.common.web.dto.ErrorResponseDto;
import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.dto.ResponseDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final CurrentUserProvider currentUserProvider;

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
    @PostMapping("/lists")
    public ResponseEntity<ResponseDto> createUserList(
            @Valid @RequestBody CreateUserListDto createUserListDto) {
        String ownerId = currentUserProvider.getCurrentUserId();
        createUserListDto.setOwnerId(ownerId);
        iUserListService.createUserList(createUserListDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,
                        String.format(ResponseConstants.MESSAGE_CREATED,"UserList")));
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
                        ResponseConstants.STATUS_OK,String.format(ResponseConstants.MESSAGE_OK,"UserList")
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
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"UserListItem")));
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
    public ResponseEntity<Page<UserListDto>> getPublicLists(
            Pageable pageable
    )
    {
        return ResponseEntity.ok(iUserListService.getListsByVisibility(ListVisibility.PUBLIC,pageable));
    }
    @Operation(
            summary = "Fetch Public Lists REST API",
            description = "REST API to fetch Public Lists"
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

    @Operation(
            summary = "Fetch Owner's Lists REST API",
            description = "REST API to fetch Owner's Lists"
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
    @GetMapping("/lists/me")
    public ResponseEntity<Page<UserListDto>>getOwnedLists(Pageable pageable)
    {
        return ResponseEntity.ok(iUserListService.getOwnedList(pageable));
    }

    @Operation(
            summary = "Fetch List Item by ID",
            description = "REST API to fetch a specific item from a list by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "List Item not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/lists/item/{id}")
    public ResponseEntity<UserListItemDto> getListItemById(@PathVariable Long id) {
        return ResponseEntity.ok(iUserListService.getListItemById(id));
    }

    @Operation(
            summary = "Fetch List Contents",
            description = "REST API to fetch all items inside a user list"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "List not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/lists/{listId}/items")
    public ResponseEntity<Page<UserListItemDto>> getListContents(
            @PathVariable Long listId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(iUserListService.getListItems(listId, pageable));
    }
}
