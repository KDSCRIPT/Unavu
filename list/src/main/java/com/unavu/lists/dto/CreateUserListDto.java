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

    @Schema(description="owner id", example="4250595d-de09-48a0-be80-3a0a57aea99c")
    private String ownerId;

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
