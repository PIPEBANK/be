package com.weborder.ordersystem.domain.web.notification.dto;

import com.weborder.ordersystem.domain.web.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String orderKey;
    private Boolean isRead;
    private String createdAt;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .orderKey(n.getOrderKey())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().format(FMT) : "")
                .build();
    }
}
