package com.emmydev.ecommerce.client.service.order;

import com.emmydev.ecommerce.client.dto.*;
import com.emmydev.ecommerce.client.entity.Order;
import com.emmydev.ecommerce.client.exception.*;
import com.stripe.exception.StripeException;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
    ResponseDto<Object> createOrder(OrderDto orderDto, String jwtToken) throws ProductNotFoundException, ComputationErrorException, OutOfStockException, StripeException, InvalidOptionException, UserNotFoundException;

    ResponseDto<Object> fetchOrdersByUser(PageRequestDto pageRequestDto, String jwtToken) throws UserNotFoundException;

    ResponseDto<Object> fetchOrdersByDateRange(DateRangeDto dateRangeDto);

    ResponseDto<Object> fetchAllOrders(PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchOrdersByStatus(String status, PageRequestDto pageRequestDto) throws InvalidOptionException;

//    ResponseDto<Object> fetchOrdersByCity(String city, PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchOrdersByDeliveryOption(String deliveryOption, PageRequestDto pageRequestDto) throws InvalidOptionException;

    ResponseDto<Object> fetchOrdersByAddress(AddressDto addressDto, PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchOrderById(Long orderId) throws ObjectNotFoundException;

    ResponseDto<Object> updateOrderById(OrderUpdateDto orderUpdateDto) throws ObjectNotFoundException, InvalidOptionException;
}
