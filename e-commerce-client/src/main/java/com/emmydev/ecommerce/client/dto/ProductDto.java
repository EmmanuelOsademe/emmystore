package com.emmydev.ecommerce.client.dto;

import com.emmydev.ecommerce.client.validation.ValidCategory;
import com.emmydev.ecommerce.client.validation.ValidManufacturer;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class ProductDto {
    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 4, max = 50)
    private String name;

    @NotBlank(message = "Product description cannot be empty")
    @Size(min = 10, max = 100)
    private String description;

    @NotBlank(message = "Product price cannot be empty")
    private Integer price;

    @NotBlank(message = "Product image cannot be empty")
    private String image;

    @NotBlank(message = "Discount rate cannot be empty")
    @Min(0)
    @Max(20)
    private double discountRate;

    @NotBlank(message = "Available quantity cannot be empty")
    private int availableQuantity;

    @NotBlank(message = "Minimum quantity cannot be empty")
    @Min(1)
    private int minimumQuantity;

    @ValidCategory
    private String productCategory;

    @ValidManufacturer
    private String manufacturer;

    @NotBlank(message = "Feature status cannot be empty")
    private boolean featured;

    @NotBlank(message = "Free shipping status cannot be empty")
    private boolean freeShipping;

    @NotBlank(message = "Product location in store cannot be empty")
    private String storeLocation;
}
