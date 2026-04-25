package com.unavu.lists.entity;

import com.unavu.common.core.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name="lists",
        indexes={
                @Index(name="idx_list_owner",columnList="owner_user_id"),
                @Index(name="idx_listVisibility",columnList = "listVisibility"),
                @Index(name="idx_listVisibility_owner", columnList="listVisibility, owner_user_id")
        }

)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 300)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListVisibility listVisibility;
}
