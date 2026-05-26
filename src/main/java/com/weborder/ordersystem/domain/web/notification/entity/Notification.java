package com.weborder.ordersystem.domain.web.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_NOTIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    @Column(name = "TYPE", nullable = false, length = 50)
    private String type;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "MESSAGE", length = 500)
    private String message;

    @Column(name = "ORDER_KEY", length = 100)
    private String orderKey;

    @Column(name = "IS_READ", nullable = false)
    private Boolean isRead;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    public Notification(Long memberId, String type, String title, String message, String orderKey) {
        this.memberId = memberId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.orderKey = orderKey;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
