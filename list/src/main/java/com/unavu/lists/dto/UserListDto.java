package com.unavu.lists.dto;

import com.unavu.lists.entity.ListVisibility;
import lombok.Data;

@Data
public class UserListDto {

    private Long id;
    private Long ownerUserId;
    private String name;
    private String description;
    private ListVisibility listVisibility;
}
