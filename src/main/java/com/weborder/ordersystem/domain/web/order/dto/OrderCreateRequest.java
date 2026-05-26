package com.weborder.ordersystem.domain.web.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {
    private Integer sosok;       // 소속 (회계단위)
    private String ujcd;         // 업장코드
    private Integer cust;        // 거래처코드
    private String odate;        // 납기일자 (yyyyMMdd)
    private String remark;       // 비고

    private List<OrderItemRequest> items;             // 직접 주문 품목 리스트
    private List<Long> cartItemIds;                   // 장바구니에서 주문 (CartItem ID 리스트)
    private boolean fromCart;                          // true: 장바구니에서 주문, false: 바로주문
}
