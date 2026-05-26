package com.weborder.ordersystem.domain.web.image.entity;

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
@Table(name = "co_item_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false)
    private Integer itemCode;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_thumbnail", nullable = false)
    private Boolean isThumbnail = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Builder
    private ItemImage(Integer itemCode, String filePath, String originalName,
                     Integer sortOrder, Boolean isThumbnail, String createdBy) {
        this.itemCode = itemCode;
        this.filePath = filePath;
        this.originalName = originalName;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.isThumbnail = isThumbnail != null ? isThumbnail : false;
        this.createdBy = createdBy;
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setAsThumbnail() {
        this.isThumbnail = true;
    }

    public void unsetThumbnail() {
        this.isThumbnail = false;
    }
}
