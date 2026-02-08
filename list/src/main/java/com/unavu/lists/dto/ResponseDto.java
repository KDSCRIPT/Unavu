package com.unavu.lists.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(
        name="Response",
        description="Schema to hold successful response information"
)
@Data @AllArgsConstructor
public class ResponseDto {

    @Schema(
            description = "Status code in the response"
    )
    private int statusCode;//making this int makes it agnostic across all platforms

    @Schema(
            description= "Status message in the response"
    )
    private String statusMsg;
}
