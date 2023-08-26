package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(Long id);

//    Long countByFirstname(String firstName);
//    Long countByLastname(String lastName);
}

