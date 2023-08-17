package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}
