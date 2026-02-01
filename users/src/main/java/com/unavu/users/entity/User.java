package com.unavu.users.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"keycloakId"})
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", nullable = false, unique = true, length = 36)
    private String keycloakId;

    @Column(length=50,unique = true,nullable = false)
    private String displayName;

    @Column(length=100)
    private String description;
}
