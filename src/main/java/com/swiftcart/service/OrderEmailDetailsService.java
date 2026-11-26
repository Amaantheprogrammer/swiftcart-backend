package com.swiftcart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.dto.OrderEmailDetails;
import com.swiftcart.entity.Order;
import com.swiftcart.entity.User;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderEmailDetailsService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderEmailDetails getOrderEmailDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        User customer = order.getUser();
        return new OrderEmailDetails(
            orderId,
            customer.getName(),
            customer.getEmail(),
            order.getTotalAmount(),
            order.getOrderDate()
        );
    }

}
