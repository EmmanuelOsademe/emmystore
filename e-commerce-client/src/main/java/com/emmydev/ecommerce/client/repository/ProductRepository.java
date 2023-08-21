package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String productName);

    List<Product> findAllProducts(Pageable pageable);
}
