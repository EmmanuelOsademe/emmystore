package com.emmydev.ecommerce.client.entity;

import com.emmydev.ecommerce.client.enums.DeliveryOption;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDER")
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
    private List<OrderProduct> products;

    @Column(nullable = false)
    private Long tax;

    @Column(nullable = false)
    private Long shippingFee;

    @Column(nullable = false)
    private Long subTotal;

    @Column(nullable = false)
    private Long total;

    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(nullable = false)
    private DeliveryOption deliveryOption;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ORDER_ID")
    )
    private Address address;

    @Column(nullable = false)
    private String chargeId;

    @Column(nullable = false)
    private String balanceTransactionId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ORDER_ID")
    )
    private User user;
}
