package com.emmydev.ecommerce.client.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "ROLE", nullable = false, unique = true)
    private String role;
}
