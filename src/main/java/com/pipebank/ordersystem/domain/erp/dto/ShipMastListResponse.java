package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipMastListResponse {
    
    private String shipNumber;              // 출하번호 (SHIP_ORDER_DATE + '-' + SHIP_ORDER_ACNO)
    private String shipOrderDate;           // 주문일 (SHIP_ORDER_DATE)
    private Integer shipMastCust;           // 거래처코드 (SHIP_MAST_CUST)
    private String orderMastSdiv;           // 출고형태 (ORDER_MAST_SDIV)
    private String orderMastSdivDisplayName; // 출고형태명
    private String shipMastComname;         // 현장명 (SHIP_MAST_COMNAME)
    private String orderMastOdate;          // 납품일 (ORDER_MAST_ODATE)
    private String status;                  // 상태 (계산된 값)
    private String statusDisplayName;       // 상태명
    
    // 추가 정보 (필요시)
    private String customerName;            // 거래처명 (Customer 조인)
    private String shipMastRemark;          // 비고
} 