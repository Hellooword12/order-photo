package com.example.Order_Photo.service;

import com.example.Order_Photo.model.Order;
import com.example.Order_Photo.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void notifyAdminAboutNewOrder(Order order) {
        log.info("Попытка отправки уведомления о новом заказе #{}", order.getId());

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("boss.nefdov@mail.ru");
            message.setTo("boss.nefdov@mail.ru");
            message.setSubject("Новый заказ фото печати #" + order.getId());
            message.setText(createOrderEmailText(order));

            mailSender.send(message);
            log.info("✅ Уведомление успешно отправлено о заказе #{}", order.getId());

        } catch (Exception e) {
            log.error("❌ Ошибка отправки email для заказа #{}: {}", order.getId(), e.getMessage());
            log.error("Детали ошибки:", e);
        }
    }

    private String createOrderEmailText(Order order) {
        return String.format(
                "Поступил новый заказ на фото печать:\n\n" +
                        "Заказ #%d\n" +
                        "Клиент: %s\n" +
                        "Телефон: %s\n" +
                        "Сумма: %.2f руб.\n" +
                        "Скидка: %.2f руб.\n" +
                        "Итого: %.2f руб.\n\n" +
                        "Детали заказа:\n%s",
                order.getId(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getTotalAmount().add(order.getDiscountAmount()),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                createOrderDetailsText(order)
        );
    }

    private String createOrderDetailsText(Order order) {
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            sb.append(String.format("- %s (%s): %d шт. x %.2f руб. = %.2f руб.\n",
                    item.getService().getName(),
                    item.getService().getDimensions(),
                    item.getQuantity(),
                    item.getService().getPrice(),
                    item.getService().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            ));
        }
        return sb.toString();
    }
}