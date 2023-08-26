package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Order;
import com.emmydev.ecommerce.client.entity.User;
import com.emmydev.ecommerce.client.enums.DeliveryOption;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByCreatedAt(Date start, Date end, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    Optional<Order> findByOrderId(Long orderId);

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    Page<Order> findByDeliveryOption(DeliveryOption option, Pageable pageable);

    Page<Order> findByAddress_country(String country, Pageable pageable);

    Page<Order> findByAddress_stateAndAddress_country(String state, String country, Pageable pageable);

    Page<Order> findByAddress_cityAndAddress_stateAndAddress_country(String city, String state, String country, Pageable pageable);

    Page<Order> findByAddress_zipCodeAndAddress_cityAndAddress_stateAndAddress_country(Integer zipCode, String city, String state, String country, Pageable pageable);
}
