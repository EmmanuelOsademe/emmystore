package com.emmydev.ecommerce.client.model;

import com.emmydev.ecommerce.client.enums.ResponseCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseModel<T> {

    private ResponseCodes responseCode;

    private String message;

    private T data;
}
