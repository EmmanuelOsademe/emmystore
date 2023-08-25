package com.emmydev.ecommerce.client.service.order;

import com.emmydev.ecommerce.client.dto.*;
import com.emmydev.ecommerce.client.entity.*;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import com.emmydev.ecommerce.client.enums.ResponseCodes;
import com.emmydev.ecommerce.client.exception.ComputationErrorException;
import com.emmydev.ecommerce.client.exception.OutOfStockException;
import com.emmydev.ecommerce.client.exception.ProductNotFoundException;
import com.emmydev.ecommerce.client.exception.UserNotFoundException;
import com.emmydev.ecommerce.client.repository.AddressRepository;
import com.emmydev.ecommerce.client.repository.OrderProductRepository;
import com.emmydev.ecommerce.client.repository.OrderRepository;
import com.emmydev.ecommerce.client.repository.ProductRepository;
import com.emmydev.ecommerce.client.service.JwtService;
import com.emmydev.ecommerce.client.service.StripeService;
import com.emmydev.ecommerce.client.service.user.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderProductRepository orderProductRepository;

    private final AddressRepository addressRepository;

    private final StripeService stripeService;

    private final JwtService jwtService;

    private final UserService userService;

    @Override
    public ResponseDto<Object> createOrder(OrderDto orderDto, String jwtToken) throws ProductNotFoundException, ComputationErrorException, OutOfStockException, StripeException {
        // Validate the products
        List<OrderProduct> orderProducts = validateProducts(orderDto);

        // Get the user's email from the token
        String email = jwtService.extractUsername(jwtToken);

        // Configure the StripeChargeDto and charge the user using Stripe
        StripeChargeDto chargeDto = StripeChargeDto
                .builder()
                .amount(orderDto.getTotal())
                .description(orderDto.getProducts().toString())
                .currency(StripeChargeDto.Currency.USD)
                .stripeEmail(email)
                .stripeToken(orderDto.getStripeToken())
                .build();

        Charge charge = stripeService.charge(chargeDto);

        Address address = null;
        // Save the user's address if address is part of the request body
        if(Objects.nonNull(orderDto.getAddress())){
            // Search the database for the address
            List<Address> addresses = addressRepository.findByHouseAddressAndCityAndStateAndCountry(
                    orderDto.getAddress().getHouseAddress(),
                    orderDto.getAddress().getCity(),
                    orderDto.getAddress().getState(),
                    orderDto.getAddress().getCountry()
            );

            // Get address if the address exists. Otherwise, save the new address
            if(addresses.size() > 0){
                address = addresses.get(0);
            }else{
                address.setHouseAddress(orderDto.getAddress().getHouseAddress().toLowerCase());
                address.setCity(orderDto.getAddress().getCity().toLowerCase());
                address.setState(orderDto.getAddress().getState().toLowerCase());
                address.setCountry(orderDto.getAddress().getCountry().toLowerCase());
                address.setZipCode(orderDto.getAddress().getZipCode());

                address = addressRepository.save(address);
            }
        }

        // Get the user from the email
        User user = userService.findUserByEmail(email).get();

        // Save the individual order products
        List<OrderProduct> savedOrderProducts = orderProductRepository.saveAll(orderProducts);

        // Create the new order object and save it;
        Order order = new Order();
        order.setProducts(savedOrderProducts);
        order.setTax(orderDto.getTax());
        order.setShippingFee(order.getShippingFee());
        order.setSubTotal(orderDto.getSubTotal());
        order.setTotal(order.getTotal());
        order.setChargeId(charge.getId());
        order.setOrderStatus(matchStatus(charge.getStatus()));
        order.setBalanceTransactionId(charge.getBalanceTransaction());
        order.setUser(user);
        if(Objects.nonNull(address)){
            order.setAddress(address);
        }
        Order savedOrder = orderRepository.save(order);

        // Return the response
        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Payment successful")
                .data(savedOrder)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchOrdersByUser(PageRequestDto pageRequestDto, String jwtToken) throws UserNotFoundException {

        // Get the user's email from the token
        String email = jwtService.extractUsername(jwtToken);

        // Fetch the user;
        Optional<User> user = userService.findUserByEmail(email);
        if(user.isEmpty()) throw new UserNotFoundException("User with " + email + " not found");

        User userData = user.get();

        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

        Page<Order> orders = orderRepository.findByUser(userData, pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchOrdersByDateRange(DateRangeDto dateRangeDto) {

        // Create the pageable object from the DateRangeDto
        Pageable pageable = new DateRangeDto().getPageable(dateRangeDto);

        Page<Order> orders = orderRepository.findByCreatedAt(dateRangeDto.getStart(), dateRangeDto.getEnd(), pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchAllOrders(PageRequestDto pageRequestDto) {
        // Create the pageable object from the PageRequestDto
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

        Page<Order> orders = orderRepository.findAll(pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchOrdersByStatus(String status, PageRequestDto pageRequestDto) {
        // Get the order Status;
        OrderStatus orderStatus = matchStatus(status);

        // Create the page
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

        Page<Order> orders = orderRepository.findByOrderStatus(orderStatus, pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }

    private List<OrderProduct> validateProducts(OrderDto orderDto) throws ProductNotFoundException, OutOfStockException, ComputationErrorException {

        Long subTotal = null;

        List<OrderProduct> orderedProducts = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        for(OrderProductDto orderProductDto: orderDto.getProducts()){
            Optional<Product> product = productRepository.findById(orderProductDto.getProduct().getProductId());

            // Throw an error if the product does not exist
            if(product.isEmpty()){
                throw new ProductNotFoundException("Product does not exist");
            }

            // Get the product from the db, calculate the subTotal and decrement the quantity
            Product dbProduct = product.get();

            // Check that the quantity exists in the store
            if(dbProduct.getAvailableQuantity() < orderProductDto.getQuantity()){
                throw new OutOfStockException(dbProduct.getName() + " is out of stock");
            }

            // Create an instance of the ordered product and add it to the list
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(dbProduct);
            orderProduct.setQuantity(orderProductDto.getQuantity());
            orderProduct.setSubTotal(((long) dbProduct.getPrice() * orderProductDto.getQuantity()));

            orderedProducts.add(orderProduct);

            subTotal +=  (long) dbProduct.getPrice() * orderProduct.getQuantity();

            // Update the product database with the new quantity
            dbProduct.setAvailableQuantity(dbProduct.getAvailableQuantity() - orderProductDto.getQuantity());
            products.add(dbProduct);
        }

        // Validate the integrity of the subtotal
        if(Objects.nonNull(subTotal) && !subTotal.equals(orderDto.getSubTotal())){
            throw new ComputationErrorException("Error computing Order subtotal");
        }

        // Save all products after their quantity has been updated
        productRepository.saveAll(products);

        return orderedProducts;
    }

    private OrderStatus matchStatus(String status){

        OrderStatus outputStatus = null;

        switch (status){
            case "succeeded":
                outputStatus = OrderStatus.SUCCESS;
                break;
            case "pending":
                outputStatus = OrderStatus.PENDING;
                break;
            case "failed":
                outputStatus = OrderStatus.FAILED;
                break;
            case "delivered":
                outputStatus = OrderStatus.DELIVERED;
                break;
        }
        return outputStatus;
    }

}
