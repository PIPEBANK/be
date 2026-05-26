package com.weborder.ordersystem.domain.web.image.dto;

import java.time.LocalDateTime;

import com.weborder.ordersystem.domain.web.image.entity.ItemImage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemImageResponse {

    private Long id;
    private Integer itemCode;
    private String filePath;
    private String originalName;
    private Integer sortOrder;
    private Boolean isThumbnail;
    private LocalDateTime createdAt;
    private String createdBy;
    private String imageUrl;

    public static ItemImageResponse from(ItemImage entity, String baseUrl) {
        return ItemImageResponse.builder()
                .id(entity.getId())
                .itemCode(entity.getItemCode())
                .filePath(entity.getFilePath())
                .originalName(entity.getOriginalName())
                .sortOrder(entity.getSortOrder())
                .isThumbnail(entity.getIsThumbnail())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .imageUrl(baseUrl + "/api/images/files/" + entity.getId())
                .build();
    }
}
