package com.emmydev.ecommerce.client.entity;

import com.emmydev.ecommerce.client.enums.Manufacturer;
import com.emmydev.ecommerce.client.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private  String image;

    @Column(nullable = false)
    private double discountRate;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private int minimumQuantity;

    @Column(nullable = false)
    private ProductCategory productCategory;

    @Column(nullable = false)
    private Manufacturer manufacturer;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean freeShipping;

    private int numberOfReviews;

    private double averageRating;

    @Column(nullable = false)
    private String storeLocation;

    private Date createdAt = new Date();

    private Date updatedAt = new Date();
}
