package com.emmydev.ecommerce.client.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductUpdateDto extends ProductDto {
    private Long id;


    ProductUpdateDto(@NotBlank(message = "Product name cannot be empty") @Size(min = 4, max = 50) String name, @NotBlank(message = "Product description cannot be empty") @Size(min = 10, max = 100) String description, @NotBlank(message = "Product price cannot be empty") Integer price, @NotBlank(message = "Product image cannot be empty") String image, @NotBlank(message = "Discount rate cannot be empty") @Min(0) @Max(20) double discountRate, @NotBlank(message = "Available quantity cannot be empty") int availableQuantity, @NotBlank(message = "Minimum quantity cannot be empty") @Min(1) int minimumQuantity, String productCategory, String manufacturer, @NotBlank(message = "Feature status cannot be empty") boolean featured, @NotBlank(message = "Free shipping status cannot be empty") boolean freeShipping, @NotBlank(message = "Product location in store cannot be empty") String storeLocation) {
        super(name, description, price, image, discountRate, availableQuantity, minimumQuantity, productCategory, manufacturer, featured, freeShipping, storeLocation);
    }
}
