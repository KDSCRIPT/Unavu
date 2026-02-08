package com.unavu.lists.dto;

import com.unavu.lists.entity.ListVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name="Create List")
public class CreateUserListDto {

    @NotNull
    @Schema(description="Owner user id", example="1")
    private Long ownerUserId;

    @NotBlank
    @Size(max = 30)
    @Schema(description="List name", example="Top Restaurants in India")
    private String name;

    @Size(max = 150)
    @Schema(description="List description")
    private String description;

    @NotNull
    private ListVisibility listVisibility;
}
