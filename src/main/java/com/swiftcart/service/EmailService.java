package com.swiftcart.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.swiftcart.dto.OrderEmailDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmation(OrderEmailDetails details) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(details.customerEmail());
        message.setSubject("Swiftcart - Order confirmation: #" + details.orderId());
        message.setText("""
            Hi %s,

            Your order has been placed successfully!

            Order ID: %d
            Order Date: %s
            Total Amount: ₹%s

            Thank you for shopping with Swift Cart!

            Regards,
            Swift Cart Team
            """.formatted(
                details.customerName(),
                details.orderId(),
                details.orderDate(),
                details.totalAmount().toPlainString()
            )
        );
        mailSender.send(message);
    }

}
