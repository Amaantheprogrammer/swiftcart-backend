package com.swiftcart.service;

import java.math.BigDecimal;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmation(String recipient, Long orderId, BigDecimal totalAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Swiftcart - Order confirmation: #" + orderId);
        message.setText("""
            Hi,

                Your order has been placed successfully!

                Order ID: %d
                Total Amount: ₹%.2f

                Thank you for shopping with Swift Cart!

                Regards,
                Swift Cart Team
            """.formatted(orderId, totalAmount)
        );
        mailSender.send(message);
    }

}
