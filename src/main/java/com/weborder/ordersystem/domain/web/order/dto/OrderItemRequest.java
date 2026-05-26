package com.weborder.ordersystem.domain.web.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class OrderItemRequest {
    private Integer itemCode;

    private Long customItemId;

    @NotNull(message = "주문수량은 필수입니다.")
    private BigDecimal quantity;

    private BigDecimal rate;
    private String remark;
}
