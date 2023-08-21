package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void saveRole(){
        Role newRole = Role.builder()
                .role("ADMIN")
                .build();

        roleRepository.save(newRole);
    }

    @Test
    public void fetchAllRoles(){
        List<Role> roles = roleRepository.findAll();
        System.out.println("Roles = " + roles);
    }

    @Test
    public void findByRoleWhenRoleExist(){
        Optional<Role> role = roleRepository.findByRole("ADMIN");
        assertTrue(role.isPresent());
        System.out.println("Role = " + role.get());
    }

    @Test
    public void findByRoleWhenRoleDoesNotExist(){
        Optional<Role> role = roleRepository.findByRole("USER");
        assertTrue(role.isEmpty());
        System.out.println("Role does not exist");
    }
}