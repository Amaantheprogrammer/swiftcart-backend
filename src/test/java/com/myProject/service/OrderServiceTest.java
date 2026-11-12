package com.myProject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.swiftcart.dto.response.OrderItemResponse;
import com.swiftcart.dto.response.OrderResponse;
import com.swiftcart.entity.*;
import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.enums.Role;
import com.swiftcart.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.swiftcart.dto.request.OrderStatusUpdateRequest;
import com.swiftcart.repository.OrderRepository;
import com.swiftcart.util.CurrentUserUtil;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CurrentUserUtil currentUserUtil;
    
    private User seller;
    private User customer;
    private Product product;
    private ProductImage image;
    private Category category;
    private OrderItem item;
    private Order order;
    
    @BeforeEach
    void setUp() {
        seller = User.builder()
        .id(1L)
        .name("Jeff Bezos")
        .email("jeff.bezos@gmail.com")
        .address("White tower US East-1")
        .password("jeff123")
        .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
        .role(Role.ROLE_SELLER)
        .accountNumber("ACC123")
        .products(new ArrayList<>())
        .build();

        customer = User.builder()
        .id(2L)
        .name("Mike Grayson")
        .email("mike.grayson@gmail.com")
        .address("City tower US West-2")
        .password("mike123")
        .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
        .role(Role.ROLE_USER)
        .accountNumber("ACC456")
        .build();

        category = Category.builder()
        .id(1L)
        .name("Household products")
        .products(new ArrayList<>())
        .build();

        product = Product.builder()
        .id(1L)
        .name("Comfortable Sofa")
        .category(null)
        .description("L-shaped comfortable red color sofa for 4 people")
        .createdAt(LocalDateTime.now())
        .price(new BigDecimal("15000"))
        .stockQuantity(10)
        .approved(true)
        .user(seller)
        .category(null)
        .build();

        image = ProductImage.builder()
        .id(1L)
        .imageUrl("Image url")
        .product(product)
        .build();

        product.getProductImages().add(image);
        seller.getProducts().add(product);
        category.getProducts().add(product);

        item = OrderItem.builder()
            .id(1L)
            .priceAtPurchase(product.getPrice())
            .quantity(1)
            .product(product)
            .build();

        order = Order.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.PENDING)
            .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
            .seller(seller)
            .user(customer)
            .totalAmount(product.getPrice())
            .paymentStatus(PaymentStatus.PENDING)
            .orderItems(new ArrayList<>())
            .build();
    }
    
    @Test
    void getOrderByIdTest() {
        // Expected response
        OrderItemResponse expectedItemResponse = OrderItemResponse.builder()
        .id(1L)
        .orderId(1L)
        .productId(1L)
        .priceAtPurchase(new BigDecimal("15000"))
        .quantity(1)
        .build();

        OrderResponse expectedOrderResponse = OrderResponse.builder()
        .id(1L)
        .orderDate(LocalDateTime.now())
        .orderStatus(OrderStatus.PENDING)
        .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
        .paymentStatus(PaymentStatus.PENDING)
        .totalAmount(new BigDecimal("15000"))
        .userId(2L)
        .userName("Mike Grayson")
        .orderItems(new ArrayList<>())
        .build();
        
        expectedOrderResponse.getOrderItems().add(expectedItemResponse);
        
        // Stub data
        when(modelMapper.map(any(Order.class), eq(OrderResponse.class))).thenReturn(expectedOrderResponse);
        when(orderRepository.findOrderById(1L)).thenReturn(Optional.of(order));

        OrderResponse actualOrderResponse = orderService.getOrderById(1L);

        // Assertions
        assertNotNull(actualOrderResponse);
        assertEquals(expectedOrderResponse.getId(), actualOrderResponse.getId());
        assertEquals(expectedOrderResponse.getTotalAmount(), actualOrderResponse.getTotalAmount());
        assertEquals(expectedOrderResponse.getUserName(), actualOrderResponse.getUserName());
        assertEquals(1, actualOrderResponse.getOrderItems().size());
    }
    
    @Test
    void getOrderByUserId() {
        // Expected response
        OrderItemResponse expectedItemResponse = OrderItemResponse.builder()
        .id(1L)
        .orderId(1L)
        .productId(1L)
        .priceAtPurchase(new BigDecimal("15000"))
        .quantity(1)
        .build();

        OrderResponse expectedOrderResponse = OrderResponse.builder()
        .id(1L)
        .orderDate(LocalDateTime.now())
        .orderStatus(OrderStatus.PENDING)
        .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
        .paymentStatus(PaymentStatus.PENDING)
        .totalAmount(new BigDecimal("15000"))
        .userId(2L)
        .userName("Mike Grayson")
        .orderItems(new ArrayList<>())
        .build();
        
        expectedOrderResponse.getOrderItems().add(expectedItemResponse);

        List<OrderResponse> expectedOrderResponses = Arrays.asList(expectedOrderResponse);

        // Stub data
        when(modelMapper.map(any(Order.class), eq(OrderResponse.class))).thenReturn(expectedOrderResponse);
        when(orderRepository.findByUserId(2L)).thenReturn(Arrays.asList(order));
    
        List<OrderResponse> actualOrderResponses = orderService.getOrderByUserId(2L);

        assertNotNull(actualOrderResponses);
        assertEquals(expectedOrderResponses.get(0).getId(), actualOrderResponses.get(0).getId());
        assertEquals(expectedOrderResponses.get(0).getUserId(), actualOrderResponses.get(0).getUserId());
        assertEquals(expectedOrderResponses.get(0).getOrderDate(), actualOrderResponses.get(0).getOrderDate());
    }
    
    @Test
    void updateOrderStatusTest() {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest(OrderStatus.SHIPPED);
        Long orderId = order.getId();
        Long sellerId = seller.getId();
        Long customerId = customer.getId();

        // Expected response
        OrderItemResponse expectedItemResponse = OrderItemResponse.builder()
        .id(item.getId())
        .productId(product.getId())
        .quantity(1)
        .priceAtPurchase(product.getPrice())
        .orderId(orderId)
        .build();
        OrderResponse expectedOrderResponse = OrderResponse.builder()
        .id(orderId)
        .orderDate(LocalDateTime.now())
        .orderItems(new ArrayList<>())
        .orderStatus(OrderStatus.SHIPPED)
        .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
        .paymentStatus(PaymentStatus.PENDING)
        .totalAmount(order.getTotalAmount())
        .userId(customerId)
        .userName(customer.getName())
        .build();

        expectedOrderResponse.getOrderItems().add(expectedItemResponse);
        
        // Stub
        when(currentUserUtil.getCurrentUser()).thenReturn(seller);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderResponse.class))).thenReturn(expectedOrderResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Actual Response
        OrderResponse actualOrderResponse = orderService.updateOrderStatus(orderId, request);

        // Assertions
        assertNotNull(actualOrderResponse);
        assertEquals(expectedOrderResponse, actualOrderResponse);
    }
}
