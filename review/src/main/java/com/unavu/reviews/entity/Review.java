package com.unavu.reviews.entity;

import com.unavu.common.core.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reviews",
        indexes = {
                @Index(name = "idx_review_restaurant", columnList = "restaurantId"),
                @Index(name = "idx_review_user", columnList = "userId")
        }
)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long restaurantId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 100)
    private String title;

    @Column(length = 2000)
    private String comment;

    private Boolean isRecommended;
}
