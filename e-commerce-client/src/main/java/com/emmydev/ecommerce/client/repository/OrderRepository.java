package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Order;
import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByCreatedAt(Date start, Date end, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    @Query("SELECT order FROM ORDERS where order.address.city = ?1")
    Page<Order> findOrdersByCity(String city, Pageable pageable);
}
