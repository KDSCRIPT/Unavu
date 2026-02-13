package com.unavu.restaurants.entity;

import com.unavu.common.core.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
@Table(
        name="restaurants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name","area","city"})
        }
)
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Integer minCost;

    private Integer maxCost;

    private String address;
    private String area;
    private String city;
    private String state;

    private Double latitude;
    private Double longitude;

    private Boolean isVegOnly;

    @ElementCollection
    @CollectionTable(
            name="restaurant_cuisines",
            joinColumns = @JoinColumn(name = "restaurant_id")
    )
    @Column(name = "cuisine")
    private List<String> cuisines;

    private String imageUrl;

}
