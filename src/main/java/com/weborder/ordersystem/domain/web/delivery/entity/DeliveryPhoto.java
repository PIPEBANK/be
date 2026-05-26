package com.weborder.ordersystem.domain.web.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_DELIVERY_PHOTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_KEY", nullable = false, length = 100)
    private String orderKey;

    @Column(name = "FILE_PATH", nullable = false, length = 500)
    private String filePath;

    @Column(name = "ORIGINAL_NAME", length = 300)
    private String originalName;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Builder
    public DeliveryPhoto(String orderKey, String filePath, String originalName, String createdBy) {
        this.orderKey = orderKey;
        this.filePath = filePath;
        this.originalName = originalName;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }
}
