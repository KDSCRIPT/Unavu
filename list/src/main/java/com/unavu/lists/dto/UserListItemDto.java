package com.unavu.lists.dto;

import lombok.Data;

@Data
public class UserListItemDto {
    private Long id;
    private Long listId;
    private Long restaurantId;
    private int position;
}
