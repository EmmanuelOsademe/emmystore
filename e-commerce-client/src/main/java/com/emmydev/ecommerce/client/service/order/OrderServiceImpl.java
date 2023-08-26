package com.emmydev.ecommerce.client.service.order;

import com.emmydev.ecommerce.client.dto.*;
import com.emmydev.ecommerce.client.entity.*;
import com.emmydev.ecommerce.client.enums.DeliveryOption;
import com.emmydev.ecommerce.client.enums.OrderStatus;
import com.emmydev.ecommerce.client.enums.ResponseCodes;
import com.emmydev.ecommerce.client.exception.*;
import com.emmydev.ecommerce.client.repository.*;
import com.emmydev.ecommerce.client.service.JwtService;
import com.emmydev.ecommerce.client.service.StripeService;
import com.emmydev.ecommerce.client.service.user.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private final UserRepository userRepository;

    @Override
    public ResponseDto<Object> createOrder(OrderDto orderDto, String jwtToken) throws ProductNotFoundException, ComputationErrorException, OutOfStockException, StripeException, InvalidOptionException, UserNotFoundException {
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

        Address address = new Address();
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
        Optional<User> userData = userService.findUserByEmail(email);
        if(userData.isEmpty()){
            throw new UserNotFoundException("User with " + email + "does not exist");
        }

        User user = userData.get();

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
        order.setOrderStatus(matchStatus(charge.getStatus().toLowerCase()));
        order.setDeliveryOption(matchDeliveryOption(orderDto.getDeliveryOption().toLowerCase()));
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
    public ResponseDto<Object> fetchOrdersByUser(PageRequestDto pageRequestDto, Long userId) throws UserNotFoundException {

        // Fetch the user;
        Optional<User> user = userRepository.findByUserId(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User with " + userId + " not found");

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
    public ResponseDto<Object> fetchOrdersByStatus(String status, PageRequestDto pageRequestDto) throws InvalidOptionException {
        // Get the order Status;
        OrderStatus orderStatus = matchStatus(status.toLowerCase());

        // Create the page
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

        Page<Order> orders = orderRepository.findByOrderStatus(orderStatus, pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }


    @Override
    public ResponseDto<Object> fetchOrdersByDeliveryOption(String deliveryOption, PageRequestDto pageRequestDto) throws InvalidOptionException {
        // Get the delivery option
        DeliveryOption validDeliveryOption = matchDeliveryOption(deliveryOption.toLowerCase());

        // Create the page
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

        // Make the query
        Page<Order> orders = orderRepository.findByDeliveryOption(validDeliveryOption, pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchOrdersByAddress(AddressDto addressDto) {
        // Create the page;
        Pageable pageable = new PageRequestDto().getPageable(addressDto);

        Page<Order> orders;

        if(Objects.nonNull(addressDto.getZipCode()) && Objects.nonNull(addressDto.getCity()) &&
        Objects.nonNull(addressDto.getState()) && Objects.nonNull(addressDto.getCountry())){
            orders = orderRepository.findByAddress_zipCodeAndAddress_cityAndAddress_stateAndAddress_country(
                    addressDto.getZipCode(), addressDto.getCity(), addressDto.getState(), addressDto.getCountry(), pageable
            );
        }else if(Objects.nonNull(addressDto.getCity()) && Objects.nonNull(addressDto.getState()) && Objects.nonNull(addressDto.getCountry())){
            orders = orderRepository.findByAddress_cityAndAddress_stateAndAddress_country(addressDto.getCity(), addressDto.getState(),
                    addressDto.getCountry(), pageable);
        }else if(Objects.nonNull(addressDto.getState()) && Objects.nonNull(addressDto.getCountry())){
            orders = orderRepository.findByAddress_stateAndAddress_country(addressDto.getState(), addressDto.getCountry(), pageable);
        }else if(Objects.nonNull(addressDto.getCountry())){
            orders = orderRepository.findByAddress_country(addressDto.getCountry(), pageable);
        }else{
            orders = orderRepository.findAll(pageable);
        }

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(orders)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchOrderById(Long orderId) throws ObjectNotFoundException {

        // Fetch the order
        Optional<Order> order = orderRepository.findByOrderId(orderId);

        if(order.isEmpty()){
            throw new ObjectNotFoundException("Order with id: " + orderId + "not found");
        }

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(order.get())
                .build();
    }

    @Override
    public ResponseDto<Object> updateOrderById(OrderUpdateDto orderUpdateDto, Long orderId) throws ObjectNotFoundException, InvalidOptionException {

        Optional<Order> orderData = orderRepository.findByOrderId(orderId);

        if(orderData.isEmpty()){
            throw new ObjectNotFoundException("Order with id: " + orderUpdateDto.getOrderStatus() + "not found");
        }

        Order order = orderData.get();

        OrderStatus status = matchStatus(orderUpdateDto.getOrderStatus().toLowerCase());

        order.setOrderStatus(status);

        orderRepository.save(order);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Orders successfully fetched")
                .data(order)
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

    private OrderStatus matchStatus(String status) throws InvalidOptionException {

        OrderStatus outputStatus;

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
            default: throw new InvalidOptionException(status + " is invalid");

        }
        return outputStatus;
    }

    private DeliveryOption matchDeliveryOption(String deliveryOption) throws InvalidOptionException {
        DeliveryOption option;

        switch (deliveryOption){
            case "pick-up":
                option = DeliveryOption.PICK_UP;
                break;
            case "home-delivery":
                option = DeliveryOption.HOME_DELIVERY;
                break;
            default:
                throw new InvalidOptionException(deliveryOption + " is invalid");
        }
        return option;
    }
}
