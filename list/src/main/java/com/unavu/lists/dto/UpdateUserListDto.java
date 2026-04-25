package com.unavu.lists.dto;

import com.unavu.lists.entity.ListVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Schema(
        name="Update List",
        description="Schema to hold List Inundation information"
)
public class UpdateUserListDto {

    @Schema(
            description = "Name of the list", example="Top restaurants in India"
    )
    @Size(max=30)
    private String name;

    @Schema(
            description = "Description of the list", example="List of top restaurants located in India"
    )
    @Size(max=100)
    private String description;

    private ListVisibility listVisibility;
}
