package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ShipSlipListResponse {
    
    private final String orderNumber;           // 주문번호 (ORDER_MAST_DATE + '-' + ORDER_MAST_ACNO)
    private final String shipNumber;            // 출하번호 (SHIP_MAST_DATE + '-' + SHIP_MAST_ACNO)
    private final String shipMastComname;       // 현장명
    private final String shipMastDate;          // 출고일자
    private final BigDecimal totalAmount;      // 출고금액 (ShipTran.shipTranTot 합계)
    
    // 정렬 및 추가 정보용
    private final Integer shipMastAcno;         // 출하번호 (정렬용)
    private final String customerName;          // 거래처명 (참고용)
} 