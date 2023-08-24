package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

}
