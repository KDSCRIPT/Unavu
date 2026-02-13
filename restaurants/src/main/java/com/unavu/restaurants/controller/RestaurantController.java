package com.unavu.restaurants.controller;

import com.unavu.common.core.ResponseConstants;
import com.unavu.common.web.dto.ResponseDto;
import com.unavu.common.web.dto.ErrorResponseDto;
import com.unavu.restaurants.dto.*;
import com.unavu.restaurants.service.IRestaurantService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for Restaurants",
        description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE Restaurant details"
)
@Slf4j
@RestController
@RequestMapping(path="/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class RestaurantController {

    private IRestaurantService iRestaurantService;
    @Operation(
            summary = "Create Restaurant REST API",
            description = "REST API to create new Restaurant"
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
    @PostMapping(value="/restaurants",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto)
    {
        log.info("Creating restaurant: name={}, area={}, city={}",
                createRestaurantDto.getName(),
                createRestaurantDto.getArea(),
                createRestaurantDto.getCity());
        iRestaurantService.createRestaurant(createRestaurantDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION,"/api/v1/restaurants/{id}")
                .body(new ResponseDto(ResponseConstants.STATUS_CREATED,String.format(ResponseConstants.MESSAGE_CREATED,"Restaurant")));
    }

    @Operation(
            summary = "Search Restaurants REST API",
            description = "REST API to search restaurants based on filters"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping(value="/restaurants/search",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RestaurantDto>> searchRestaurants(
            @Valid @RequestBody SearchRestaurantDto searchRestaurantDto,
            Pageable pageable
    ) {
        log.info("Searching restaurants with filters: {}", searchRestaurantDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                iRestaurantService.searchRestaurants(searchRestaurantDto,pageable)
        );
    }


    @Operation(
            summary = "Fetch Restaurant REST API",
            description = "REST API to fetch Restaurant details"
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

    @GetMapping("/restaurants")
    public ResponseEntity<Page<RestaurantDto>> listRestaurants(Pageable pageable) {
        log.info("Fetching paginated restaurant list: page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return ResponseEntity.status(HttpStatus.OK).body(
                iRestaurantService.restaurantList(pageable)
        );
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDto> fetchRestaurant(@PathVariable Long id) {
        log.info("Fetching restaurant with id={}", id);
        RestaurantDto restaurantDto = iRestaurantService.getRestaurantById(id);
        return ResponseEntity.status(HttpStatus.OK).body(restaurantDto);
    }

    @Operation(
            summary = "Update Restaurant Details REST API",
            description = "REST API to update Restaurant details"
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
    @PatchMapping(value="/restaurants/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateRestaurantDetails(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantDto updateRestaurantDto
    ) {
        log.info("Updating restaurant with id={}", id);
        iRestaurantService.updateRestaurant(id, updateRestaurantDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        ResponseConstants.STATUS_OK,String.format(ResponseConstants.MESSAGE_OK,"Restaurant")
                ));
    }

    @Operation(
            summary = "Delete Restaurant REST API",
            description = "REST API to delete Restaurant details by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "HTTP No Content"
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
    @DeleteMapping(value="/restaurants/{id}")
    public ResponseEntity<ResponseDto> deleteRestaurantDetails(@PathVariable long id) {
        log.info("Deleting restaurant with id={}", id);
        iRestaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}