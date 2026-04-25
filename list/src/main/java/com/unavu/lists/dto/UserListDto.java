package com.unavu.lists.dto;

import com.unavu.lists.entity.ListVisibility;
import lombok.Data;

@Data
public class UserListDto {

    private Long id;
    private String ownerId;
    private String name;
    private String description;
    private ListVisibility listVisibility;
}
