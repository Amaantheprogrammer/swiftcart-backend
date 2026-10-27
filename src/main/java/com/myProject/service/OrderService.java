package com.myProject.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.myProject.dto.request.*;
import com.myProject.dto.response.*;
import com.myProject.entity.*;
import com.myProject.enums.*;
import com.myProject.util.CurrentUserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myProject.repository.*;
import com.myProject.exception.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final DigitalBankingClientService digitalBankingClientService;
    private final CurrentUserUtil currentUserUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAllOrders(pageable);
        return orders.map(order -> modelMapper.map(order, OrderResponse.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return modelMapper.map(order, OrderResponse.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User user = currentUserUtil.getCurrentUser();
        List<Order> orders = user.getOrders();
        return orders.stream()
                    .map(order -> modelMapper.map(order, OrderResponse.class))
                    .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('SELLER')")
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        validateOrderAuthority(order);
        if (request.getOrderStatus() == OrderStatus.SHIPPED && order.getPaymentStatus() == PaymentStatus.FAILED) {
            throw new BadRequestException("Cannot ship an order with payment status " + order.getPaymentStatus() 
            + " and order status " + request.getOrderStatus());
        }
        order.setOrderStatus(request.getOrderStatus());
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @PreAuthorize("hasRole('SELLER')")
    @Transactional
    public OrderResponse updatePaymentStatus(Long orderId, PaymentStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        validateOrderAuthority(order);
        if (request.getPaymentStatus() == PaymentStatus.PAID && order.getOrderStatus() == OrderStatus.PENDING) {
            order.setOrderStatus(OrderStatus.PLACED);
        }
        order.setPaymentStatus(request.getPaymentStatus());
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        User customer = currentUserUtil.getCurrentUser();
        Long productId = orderRequest.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Product not found with ID: " + productId));
        User seller = product.getUser();
        if (seller.getAccountNumber() == null || seller.getAccountNumber().isBlank()) {
            throw new ResourceNotFoundException("Seller does not have a linked bank account for payouts.");
        }
        Integer quantity = orderRequest.getQuantity();
        PaymentMethod paymentMethod = customer.getPaymentMethod();
        Integer stockQuantity = product.getStockQuantity();
        if (stockQuantity < quantity) {
            throw new InsufficientResourceException("Insufficient stock for product: " + product.getName());
        }
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
            if (customer.getAccountNumber() == null && orderRequest.getAccountNumber() == null) {
                throw new BadRequestException("Transfer cannot proceed without account number");
            }
            String customerAccountNumber = orderRequest.getAccountNumber() != null
                    ? orderRequest.getAccountNumber()
                    : customer.getAccountNumber();
            AccountResponse account = digitalBankingClientService.getAccountByNumber(customerAccountNumber);
            if (account.getBalance().subtract(totalAmount).compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientResourceException("Insufficient balance in the account");
            }
            TransferRequest request = TransferRequest.builder()
                    .customerAccountNumber(customerAccountNumber)
                    .sellerAccountNumber(seller.getAccountNumber())
                    .amount(totalAmount)
                    .build();
            digitalBankingClientService.transfer(request);
        }
        Order order = Order.builder()
                .user(customer)
                .seller(product.getUser())
                .address(orderRequest.getAddress() != null ? orderRequest.getAddress() : customer.getAddress())
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(
                        paymentMethod == PaymentMethod.BANK_TRANSFER
                                ? PaymentStatus.PAID
                                : PaymentStatus.PENDING)
                .paymentMethod(paymentMethod)
                .orderItems(new ArrayList<>())
                .build();
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .priceAtPurchase(product.getPrice())
                .build();

        order.getOrderItems().add(orderItem);

        product.setStockQuantity(stockQuantity - quantity);

        productRepository.save(product);

        return modelMapper.map(orderRepository.save(order), OrderResponse.class);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional
    public List<OrderResponse> placeOrderFromCart(OrderFromCartRequest request) {
        User customer = currentUserUtil.getCurrentUser();
        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Order cannot be placed with an empty cart");
        }
        List<CartItem> cartItems = cart.getCartItems();
        PaymentMethod paymentMethod = customer.getPaymentMethod();
        BigDecimal totalAmount = BigDecimal.ZERO;
        // Stock validation loop
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientResourceException("Insufficient stock for product: " + product.getName());
            }
            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }
        String customerAccountNumber = null;
        // Account number and balance check
        if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
            if (customer.getAccountNumber() == null && request.getAccountNumber() == null) {
                throw new BadRequestException("Transfer cannot proceed without account number");
            }
            customerAccountNumber = request.getAccountNumber() != null
                    ? request.getAccountNumber()
                    : customer.getAccountNumber();
            AccountResponse account = digitalBankingClientService.getAccountByNumber(customerAccountNumber);
            if (account.getBalance().subtract(totalAmount).compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientResourceException("Insufficient balance in the account");
            }
        }
        // seller -> item map
        Map<User, List<CartItem>> itemsBySeller = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getUser()));
        // seller account number -> price total
        Map<String, BigDecimal> sellerPayoutMap = new HashMap<>();
        for (Map.Entry<User, List<CartItem>> entry : itemsBySeller.entrySet()) {
            User seller = entry.getKey();
            if (seller.getAccountNumber() == null || seller.getAccountNumber().isBlank()) {
                throw new ResourceNotFoundException("Seller " + seller.getName() + " does not have a linked bank account.");
            }
            BigDecimal sellerOrderTotal = entry.getValue().stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            sellerPayoutMap.put(seller.getAccountNumber(), sellerOrderTotal);
        }
        if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
            // Perform transfer to each seller account
            for (Map.Entry<String, BigDecimal> payout : sellerPayoutMap.entrySet()) {
                TransferRequest transferRequest = TransferRequest.builder()
                        .customerAccountNumber(customerAccountNumber)
                        .sellerAccountNumber(payout.getKey())
                        .amount(payout.getValue())
                        .build();
                digitalBankingClientService.transfer(transferRequest);
            }
        }
        List<Order> savedOrders = new ArrayList<>();
        for (Map.Entry<User, List<CartItem>> entry : itemsBySeller.entrySet()) {
            User seller = entry.getKey();
            List<CartItem> sellerCartItems = entry.getValue();
            BigDecimal sellerOrderTotal = sellerPayoutMap.get(seller.getAccountNumber());

            Order order = Order.builder()
                    .user(customer)
                    .seller(seller)
                    .address(request.getAddress() != null ? request.getAddress() : customer.getAddress())
                    .totalAmount(sellerOrderTotal)
                    .orderDate(LocalDateTime.now())
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(paymentMethod == PaymentMethod.BANK_TRANSFER ? PaymentStatus.PAID : PaymentStatus.PENDING)
                    .paymentMethod(paymentMethod)
                    .orderItems(new ArrayList<>())
                    .build();
            // Stock reduction
            for (CartItem cartItem : sellerCartItems) {
                Product product = cartItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                productRepository.save(product);

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(cartItem.getQuantity())
                        .priceAtPurchase(product.getPrice())
                        .build();
                order.getOrderItems().add(orderItem);
            }

            savedOrders.add(orderRepository.save(order));
        }

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedOrders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    // To be updated
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional
    public void cancelOrder(Long orderId) {
        User currentUser = currentUserUtil.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AuthorizationDeniedException("You can only cancel your own orders");
        }
        if (order.getOrderStatus() == OrderStatus.DELIVERED
                || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel an order with order status: " + order.getOrderStatus());
        }
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });
        if (order.getPaymentStatus() == PaymentStatus.PAID && currentUser.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            List<AccountResponse> accounts = digitalBankingClientService.getMyAccounts();
            if (accounts.isEmpty()) {
                throw new ResourceNotFoundException("No active bank account found for user");
            }
            AccountResponse primaryAccount = accounts.getFirst();
            TransactionRequest depositRequest = TransactionRequest.builder()
                    .accountNumber(primaryAccount.getAccountNumber())
                    .amount(order.getTotalAmount())
                    .build();
            digitalBankingClientService.deposit(depositRequest);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        order.setPaymentStatus(PaymentStatus.REFUNDED);

        orderRepository.save(order);
    }

    // Private method
    private void validateOrderAuthority(Order order) {
        Long sellerId = currentUserUtil.getCurrentUser().getId();
        if (!order.getSeller().getId().equals(sellerId)) {
            throw new AuthorizationDeniedException("You can only manage orders assigned to your seller account");
        }
    }
}
