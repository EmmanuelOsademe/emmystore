package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

}