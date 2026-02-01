package com.unavu.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(
        name="Create user",
        description="Schema to hold User creation information"
)
public class CreateUserDto {

    @NotBlank(message="Display Name of user account cannot be null or empty")
    @Schema(
            description = "Display Name of user account", example="Foodie@123"
    )
    private String displayName;

    @NotBlank(message="Description of user account cannot be null or empty")
    @Schema(
            description = "Description of user account", example="Desert lover and food vlogger"
    )
    private String description;
}
