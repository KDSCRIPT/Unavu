package com.unavu.lists.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name="Add Item To List")
public class AddItemToUserListDto {

    @NotNull
    @Schema(description="Id of the list", example="1")
    private Long listId;

    @NotNull
    @Schema(description="Id of the restaurant to add", example="45")
    private Long restaurantId;
}

