package com.weborder.ordersystem.domain.web.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemRequest {

    private Integer itemCode;

    private Long customItemId;

    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    private Integer quantity;

    public CartItemRequest(Integer itemCode, Integer quantity) {
        this.itemCode = itemCode;
        this.quantity = quantity;
    }

    public CartItemRequest(Long customItemId, Integer quantity, boolean isCustom) {
        this.customItemId = customItemId;
        this.quantity = quantity;
    }
}
