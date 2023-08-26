package com.emmydev.ecommerce.client.dto;

import lombok.Data;

@Data
public class OrderUpdateDto {
    private Long orderId;

    private String orderStatus;
}
