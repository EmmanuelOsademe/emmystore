package com.emmydev.ecommerce.client.service.order;

import com.emmydev.ecommerce.client.dto.OrderDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.entity.Order;
import com.emmydev.ecommerce.client.exception.ComputationErrorException;
import com.emmydev.ecommerce.client.exception.OutOfStockException;
import com.emmydev.ecommerce.client.exception.ProductNotFoundException;
import com.stripe.exception.StripeException;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
    ResponseDto<Object> createOrder(OrderDto orderDto, String jwtToken) throws ProductNotFoundException, ComputationErrorException, OutOfStockException, StripeException;
}
