package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(String role);
}
