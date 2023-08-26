package com.emmydev.ecommerce.client.controller;

import com.emmydev.ecommerce.client.dto.*;
import com.emmydev.ecommerce.client.exception.*;
import com.emmydev.ecommerce.client.service.order.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ResponseDto<Object>> saveOrder(@Valid @RequestBody OrderDto orderDto, final HttpServletRequest request) throws StripeException, OutOfStockException, ComputationErrorException, ProductNotFoundException, UserNotFoundException, InvalidOptionException {
        String jwtToken = request.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(orderService.createOrder(orderDto, jwtToken));
    }

    @PostMapping("/user")
    public ResponseEntity<ResponseDto<Object>> fetchOrdersByUser(@RequestParam("userId") Long userId, @RequestBody PageRequestDto pageRequestDto) throws UserNotFoundException {
        return ResponseEntity.ok(orderService.fetchOrdersByUser(pageRequestDto, userId));
    }

    @PostMapping("/date-range")
    public ResponseEntity<ResponseDto<Object>> fetchOrdersByDateRange(@Valid @RequestBody DateRangeDto dateRangeDto){
        return ResponseEntity.ok(orderService.fetchOrdersByDateRange(dateRangeDto));
    }

    @PostMapping("/all")
    public ResponseEntity<ResponseDto<Object>> fetchAllOrders(@RequestBody PageRequestDto pageRequestDto){
        return ResponseEntity.ok(orderService.fetchAllOrders(pageRequestDto));
    }

    @PostMapping("/status")
    public ResponseEntity<ResponseDto<Object>> fetchAllOrdersByStatus(@RequestParam("status") String status, @RequestBody PageRequestDto pageRequestDto) throws InvalidOptionException {
        return ResponseEntity.ok(orderService.fetchOrdersByStatus(status, pageRequestDto));
    }

    @PostMapping("/delivery-option")
    public ResponseEntity<ResponseDto<Object>> fetchOrdersByDeliveryOption(@RequestParam("option") String option, @RequestBody PageRequestDto pageRequestDto) throws InvalidOptionException {
        return ResponseEntity.ok(orderService.fetchOrdersByDeliveryOption(option, pageRequestDto));
    }

    @PostMapping("/address")
    public ResponseEntity<ResponseDto<Object>> fetchOrdersByAddress(@RequestBody AddressDto addressDto){
        return ResponseEntity.ok(orderService.fetchOrdersByAddress(addressDto));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<Object>> fetchOrderById(@PathVariable Long orderId) throws ObjectNotFoundException {
        return ResponseEntity.ok(orderService.fetchOrderById(orderId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseDto<Object>> updateOrderById(@Valid @RequestBody OrderUpdateDto orderUpdateDto, @PathVariable Long orderId) throws ObjectNotFoundException, InvalidOptionException {
        return ResponseEntity.ok(orderService.updateOrderById(orderUpdateDto, orderId));
    }
}
