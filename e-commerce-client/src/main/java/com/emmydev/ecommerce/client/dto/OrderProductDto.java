package com.emmydev.ecommerce.client.dto;

import com.emmydev.ecommerce.client.entity.Product;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrderProductDto {

    @NotBlank(message = "Product cannot be empty")
    private Product product;

    @NotBlank(message = "Quantity cannot be empty")
    private Integer quantity;
}
