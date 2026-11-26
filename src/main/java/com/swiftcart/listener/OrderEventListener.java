package com.swiftcart.listener;

import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.swiftcart.dto.OrderEmailDetails;
import com.swiftcart.event.OrderPlacedEvent;
import com.swiftcart.service.EmailService;
import com.swiftcart.service.OrderEmailDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderEmailDetailsService orderEmailDetailsService;
    private final EmailService emailService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info(
                "Processing order notification for order ID: {} on thread: {}",
                event.orderId(),
                Thread.currentThread().getName()
        );

        try {
            OrderEmailDetails details = orderEmailDetailsService.getOrderEmailDetails(event.orderId());
            emailService.sendOrderConfirmation(details);
            log.info("Order confirmation email sent for order ID: {}", event.orderId());
        } catch (MailException e) {
            log.error(
                    "Failed to send order confirmation email for order ID: {}",
                    event.orderId(),
                    e
            );
        } catch (Exception e) {
            log.error(
                    "Failed to process order notification for order ID: {}",
                    event.orderId(),
                    e
            );
        }
    }
}
