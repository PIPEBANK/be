package com.weborder.ordersystem.domain.web.customitem.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "co_custom_item_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CustomItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "custom_item_id", nullable = false)
    private Long customItemId;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_thumbnail", nullable = false)
    private Boolean isThumbnail;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public CustomItemImage(Long customItemId, String filePath, String originalName,
                           Integer sortOrder, Boolean isThumbnail) {
        this.customItemId = customItemId;
        this.filePath = filePath;
        this.originalName = originalName;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.isThumbnail = isThumbnail != null ? isThumbnail : false;
    }

    public void setAsThumbnail() {
        this.isThumbnail = true;
    }

    public void unsetThumbnail() {
        this.isThumbnail = false;
    }
}
