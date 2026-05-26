package com.weborder.ordersystem.domain.web.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderStatsByCustResponse {
    private String memberName;
    private Long memberId;
    private int orderCount;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
    private String remark;
}
