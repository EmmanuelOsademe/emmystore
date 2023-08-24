package com.emmydev.ecommerce.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDER_PRODUCT")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "productId"
    )
    @Column(nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    private Date createdAt = new Date();

    private Date updatedAt = new Date();
}
