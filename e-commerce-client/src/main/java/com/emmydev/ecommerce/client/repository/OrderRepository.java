package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
