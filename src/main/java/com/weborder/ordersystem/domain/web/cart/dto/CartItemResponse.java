package com.weborder.ordersystem.domain.web.cart.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.weborder.ordersystem.domain.web.cart.entity.CartItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {

    private Long id;
    private Long memberId;
    private Integer itemCode;
    private Long customItemId;
    private boolean isCustomItem;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String itemName;
    private String itemSpec;
    private String itemUnit;
    private String itemCodeNum;
    private BigDecimal unitPrice;
    private BigDecimal displayPrice;
    private Boolean hidePrice;
    private String thumbnailUrl;

    private String customItemDesc;

    public static CartItemResponse from(CartItem entity) {
        return CartItemResponse.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .itemCode(entity.getItemCode())
                .customItemId(entity.getCustomItemId())
                .isCustomItem(entity.isCustomItem())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static CartItemResponse from(CartItem entity, String itemName, String itemSpec,
                                        String itemUnit, String itemCodeNum,
                                        BigDecimal unitPrice, Integer vdiv, String thumbnailUrl) {
        return from(entity, itemName, itemSpec, itemUnit, itemCodeNum, unitPrice, vdiv, thumbnailUrl, false);
    }

    public static CartItemResponse from(CartItem entity, String itemName, String itemSpec,
                                        String itemUnit, String itemCodeNum,
                                        BigDecimal unitPrice, Integer vdiv, String thumbnailUrl,
                                        boolean hidePrice) {
        BigDecimal dp = unitPrice;
        if (vdiv != null && vdiv == 1 && unitPrice != null) {
            dp = unitPrice.multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.HALF_UP);
        }

        return CartItemResponse.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .itemCode(entity.getItemCode())
                .customItemId(entity.getCustomItemId())
                .isCustomItem(entity.isCustomItem())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .itemName(itemName)
                .itemSpec(itemSpec)
                .itemUnit(itemUnit)
                .itemCodeNum(itemCodeNum)
                .unitPrice(hidePrice ? null : unitPrice)
                .displayPrice(hidePrice ? null : dp)
                .hidePrice(hidePrice)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    public static CartItemResponse fromCustomItem(CartItem entity, String itemName,
                                                   String description, String thumbnailUrl) {
        return CartItemResponse.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .itemCode(null)
                .customItemId(entity.getCustomItemId())
                .isCustomItem(true)
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .itemName(itemName)
                .customItemDesc(description)
                .unitPrice(null)
                .displayPrice(null)
                .hidePrice(true)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
