package com.weborder.ordersystem.domain.admin.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    private String webOrderStatus; // ORDERED, CONFIRMED, SHIPPING, DELIVERED
    private String erpStau;        // ERP 상태코드 (4010010001 등)
}
