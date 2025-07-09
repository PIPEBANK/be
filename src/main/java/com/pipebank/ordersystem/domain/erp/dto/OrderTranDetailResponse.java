package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * 주문 상세조회용 OrderTran 응답 DTO (필수 필드만 포함)
 */
@Getter
@Builder
public class OrderTranDetailResponse {
    
    private String itemCodeNum;                     // 제품코드 (co_item_code.item_code_num)
    private Integer orderTranItem;                  // 제품번호 (FK - item_code_code)
    private String orderTranDeta;                   // 제품명
    private String orderTranSpec;                   // 규격
    private String orderTranUnit;                   // 단위
    private BigDecimal orderTranCnt;                // 수량
    private BigDecimal orderTranDcPer;              // DC(%)
    private BigDecimal orderTranAmt;                // 단가
    private BigDecimal orderTranTot;                // 금액
    private String orderTranStau;                   // 상태코드
    private String orderTranStauDisplayName;        // 상태코드명
    
    // ===== 출하정보 =====
    private String shipNumber;                      // 출하번호 (SHIP_ORDER_DATE + "-" + SHIP_ORDER_ACNO)
    private BigDecimal shipQuantity;                // 출하량 (SHIP_TRAN_CNT)
} 