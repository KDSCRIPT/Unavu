package com.unavu.common.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(
        name="Response",
        description="Schema to hold successful response information"
)
@Data @AllArgsConstructor @NoArgsConstructor
public class ResponseDto<T>{

    @Schema(
            description = "Status code in the response"
    )
    private int statusCode;//making this int makes it agnostic across all platforms

    @Schema(
            description= "Status message in the response"
    )
    private String statusMsg;

//    @Schema(
//            description= "Data returned in the response"
//    )
//    private T data;
//
//    @Schema(
//            description= "Timestamp of the response"
//    )
//    private LocalDateTime timestamp;
}
