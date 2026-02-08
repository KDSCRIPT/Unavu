package com.unavu.lists.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name="list_items",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"list_id", "restaurant_id","position"}
        ),
        indexes = {
            @Index(name = "idx_list_items_list", columnList = "list_id"),
            @Index(name = "idx_list_items_restaurant", columnList = "restaurant_id"),
                @Index(name="idx_list_position", columnList="list_id, position")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserListItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="list_id",nullable = false)
    private Long listId;


    @Column(name="restaurant_id",nullable = false)
    private Long restaurantId;

    @Column(nullable = false)
    private int position;

}
