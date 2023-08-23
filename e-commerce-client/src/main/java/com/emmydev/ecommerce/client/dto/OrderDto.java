package com.emmydev.ecommerce.client.dto;

import com.emmydev.ecommerce.client.entity.Address;
import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.validation.ValidOrder;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@ValidOrder
public class OrderDto {
    @NotBlank(message = "Products cannot be empty")
    private List<Product> products;

    @NotBlank(message = "Tax cannot be empty")
    private Double tax;

    @NotBlank(message = "Shipping fee cannot be empty")
    private Double shippingFee;

    @NotBlank(message = "Subtotal cannot be empty")
    private Double subTotal;

    @NotBlank(message = "Total cannot be empty")
    private Double total;

    @NotBlank(message = "Delivery option cannot be empty")
    private String deliveryOption;

    private Address address;
}
