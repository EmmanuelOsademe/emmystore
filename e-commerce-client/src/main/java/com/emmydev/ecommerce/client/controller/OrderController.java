package com.emmydev.ecommerce.client.controller;

import com.emmydev.ecommerce.client.dto.OrderDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.exception.ComputationErrorException;
import com.emmydev.ecommerce.client.exception.OutOfStockException;
import com.emmydev.ecommerce.client.exception.ProductNotFoundException;
import com.emmydev.ecommerce.client.service.order.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ResponseDto<Object>> saveOrder(@Valid @RequestBody OrderDto orderDto, final HttpServletRequest request) throws StripeException, OutOfStockException, ComputationErrorException, ProductNotFoundException {
        String jwtToken = request.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(orderService.createOrder(orderDto, jwtToken));
    }

}
