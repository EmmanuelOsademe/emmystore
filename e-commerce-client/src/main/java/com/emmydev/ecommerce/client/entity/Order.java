package com.emmydev.ecommerce.client.entity;

import com.emmydev.ecommerce.client.enums.DeliveryOption;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
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
    private List<OrderProduct> products;

    @Column(nullable = false)
    private Long tax;

    @Column(nullable = false)
    private Long shippingFee;

    @Column(nullable = false)
    private Long subTotal;

    @Column(nullable = false)
    private Long total;

    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(nullable = false)
    private DeliveryOption deliveryOption;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ORDER_ADDRESS_ID")
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
            foreignKey = @ForeignKey(name = "FK_ORDER_USER_ID")
    )
    private User user;

    private Date createdAt = new Date();

    private Date updatedAt = new Date();
}
