package com.emmydev.ecommerce.client.entity;

import com.emmydev.ecommerce.client.enums.DeliveryOption;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "orderId"
    )
    @Column(nullable = false)
    private List<Product> products;

    @Column(nullable = false)
    private Double tax;

    @Column(nullable = false)
    private Double shippingFee;

    @Column(nullable = false)
    private Double subTotal;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(nullable = false)
    private DeliveryOption deliveryOption;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN")
    )
    @Column(nullable = false)
    private Address address;

    @Column(nullable = false)
    private String clientSecret;

    @Column(nullable = false)
    private String paymentIntent;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN")
    )
    @Column(nullable = false)
    private User user;
}
