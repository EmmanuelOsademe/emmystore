package com.emmydev.ecommerce.client.dto;

import com.emmydev.ecommerce.client.enums.ResponseCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto<T> {

    private ResponseCodes responseCode;

    private String message;

    private T data;
}
