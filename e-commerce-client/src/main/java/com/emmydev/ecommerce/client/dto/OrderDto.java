package com.emmydev.ecommerce.client.dto;

import com.emmydev.ecommerce.client.entity.Address;
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
    private List<OrderProductDto> products;

    @NotBlank(message = "Tax cannot be empty")
    private Long tax;

    private Long shippingFee;

    @NotBlank(message = "Subtotal cannot be empty")
    private Long subTotal;

    @NotBlank(message = "Total cannot be empty")
    private Long total;

    @NotBlank(message = "Delivery option cannot be empty")
    private String deliveryOption;

    private Address address;

    @NotBlank(message = "Stripe token cannot be empty")
    private String stripeToken;
}
