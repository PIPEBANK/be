package com.weborder.ordersystem.domain.web.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderStatsByItemResponse {
    private Integer itemCode;
    private String itemName;
    private BigDecimal avgRate;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
}
