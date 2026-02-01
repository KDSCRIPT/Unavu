package com.unavu.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
        name="Update User",
        description="Schema to hold User inundation information"
)
public class UpdateUserDto {


    @Schema(
            description = "Display Name of user account", example="Foodie@123"
    )
    private String displayName;
    @Schema(
            description = "Description of user account", example="Desert lover and food vlogger"
    )
    private String description;
}
