package com.emmydev.ecommerce.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponseDto {

    private List<ViolationDto> violationDtos = new ArrayList<>();
}
