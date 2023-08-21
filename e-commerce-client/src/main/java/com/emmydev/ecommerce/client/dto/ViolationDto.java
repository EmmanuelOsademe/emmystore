package com.emmydev.ecommerce.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViolationDto {

    private String fieldName;

    private String message;
}
