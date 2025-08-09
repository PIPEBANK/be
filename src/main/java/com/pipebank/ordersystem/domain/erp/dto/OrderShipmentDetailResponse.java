package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * 주문-출하 통합 상세 조회 응답 DTO
 * OrderMast + OrderTran + ItemCode + ShipTran 통합 정보
 */
@Getter
@Builder
public class OrderShipmentDetailResponse {
    
    // 주문 기본 정보
    private String orderDate;           // 주문일자 (orderMastDate)
    private String orderNumber;         // 주문번호 (orderMastDate-orderMastAcno)
    private String deliveryDate;        // 납기일자 (orderMastOdate)
    private String status;              // 상태 (orderTranStau)
    private String statusDisplayName;   // 상태명
    
    // 품목 정보
    private String itemNumber;          // 품번 (itemCodeNum)
    private String itemName;            // 품명 (orderTranDeta)
    private String spec;                // 규격 (orderTranSpec)
    private String unit;                // 단위 (orderTranUnit)
    
    // 현장 정보
    private String siteName;            // 납품현장명 (orderMastComname)
    private String demander;            // 수요처 (orderMastDcust)
    
    // 수주 정보
    private BigDecimal orderQuantity;   // 수주수량 (orderTranCnt)
    private BigDecimal unitPrice;       // 판매단가 (orderTranAmt)
    private BigDecimal discountRate;    // 할인율 (orderTranDcPer)
    private BigDecimal orderAmount;     // 주문금액 (orderTranTot)
    private BigDecimal orderTranNet;    // 공급가 (orderTranNet)
    private BigDecimal orderTranVat;    // 부가세 (orderTranVat)
    
    // 출하 정보
    private BigDecimal shipQuantity;    // 출하수량 (shipTranCnt)
    private BigDecimal pendingQuantity; // 미출하수량 (orderTranCnt - shipTranCnt)
    private BigDecimal pendingAmount;   // 미출하금액 (미출하수량 × orderTranAmt)

    /**
     * Entity 데이터로부터 DTO 생성
     * 
     * @param result Repository 쿼리 결과 Object[] 배열
     * @return OrderShipmentDetailResponse
     */
    public static OrderShipmentDetailResponse from(Object[] result) {
        // Object[] 인덱스 매핑:
        // 0: orderMastDate, 1: orderMastAcno, 2: orderMastOdate, 3: orderTranStau
        // 4: itemCodeNum, 5: orderTranDeta, 6: orderTranSpec, 7: orderTranUnit
        // 8: orderMastComname, 9: orderMastDcust
        // 10: orderTranCnt, 11: orderTranAmt, 12: orderTranDcPer, 13: orderTranTot
        // 14: shipTranCnt, 15: statusDisplayName, 16: orderTranNet, 17: orderTranVat
        
        String orderDate = (String) result[0];
        Integer orderAcno = (Integer) result[1];
        String orderNumber = orderDate + "-" + orderAcno;
        
        BigDecimal orderQuantity = (BigDecimal) result[10];
        BigDecimal shipQuantity = result[14] != null ? (BigDecimal) result[14] : BigDecimal.ZERO;
        BigDecimal unitPrice = (BigDecimal) result[11];
        
        // 미출하수량 = 수주수량 - 출하수량
        BigDecimal pendingQuantity = orderQuantity.subtract(shipQuantity);
        
        // 미출하금액 = 미출하수량 × 판매단가
        BigDecimal pendingAmount = pendingQuantity.multiply(unitPrice);
        
        return OrderShipmentDetailResponse.builder()
                .orderDate(orderDate)
                .orderNumber(orderNumber)
                .deliveryDate((String) result[2])
                .status((String) result[3])
                .statusDisplayName((String) result[15])
                .itemNumber((String) result[4])
                .itemName((String) result[5])
                .spec((String) result[6])
                .unit((String) result[7])
                .siteName((String) result[8])
                .demander((String) result[9])
                .orderQuantity(orderQuantity)
                .unitPrice(unitPrice)
                .discountRate((BigDecimal) result[12])
                .orderAmount((BigDecimal) result[13])
                .shipQuantity(shipQuantity)
                .pendingQuantity(pendingQuantity)
                .pendingAmount(pendingAmount)
                .orderTranNet((BigDecimal) result[16])
                .orderTranVat((BigDecimal) result[17])
                .build();
    }
} 