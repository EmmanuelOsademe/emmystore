package com.emmydev.ecommerce.client.service.order;

import com.emmydev.ecommerce.client.dto.*;
import com.emmydev.ecommerce.client.entity.Order;
import com.emmydev.ecommerce.client.exception.ComputationErrorException;
import com.emmydev.ecommerce.client.exception.OutOfStockException;
import com.emmydev.ecommerce.client.exception.ProductNotFoundException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.stripe.exception.StripeException;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
    ResponseDto<Object> createOrder(OrderDto orderDto, String jwtToken) throws ProductNotFoundException, ComputationErrorException, OutOfStockException, StripeException;

    ResponseDto<Object> fetchOrdersByUser(PageRequestDto pageRequestDto, String jwtToken) throws UserNotFoundException;

    ResponseDto<Object> fetchOrdersByDateRange(DateRangeDto dateRangeDto);

    ResponseDto<Object> fetchAllOrders(PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchOrdersByStatus(String status, PageRequestDto pageRequestDto);

//    ResponseDto<Object> fetchOrdersByCity(String city, PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchOrdersByAddress(AddressDto addressDto, PageRequestDto pageRequestDto);
}
