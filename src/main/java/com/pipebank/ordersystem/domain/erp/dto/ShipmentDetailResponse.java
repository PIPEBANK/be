package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.ShipTran;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ShipmentDetailResponse {
    
    private final String shipNumber;           // 출하번호 (SHIP_ORDER_DATE + '-' + SHIP_ORDER_ACNO)
    private final String itemCodeNum;          // 제품코드 (ItemCode.itemCodeNum)
    private final Integer shipTranItem;        // 제품번호 (FK)
    private final String shipTranDeta;         // 품목명 (ShipTran.shipTranDeta)
    private final String shipTranSpec;         // 규격
    private final String shipTranUnit;         // 단위
    private final BigDecimal orderQuantity;   // 주문량 (OrderTran.orderTranCnt)
    private final BigDecimal shipQuantity;    // 출고량 (ShipTran.shipTranCnt)
    private final BigDecimal remainQuantity;  // 주문잔량 (주문량 - 출고량)
    private final Integer shipTranSeq;        // 출하순번
    
    /**
     * ShipTran에서 기본 정보를 가져와서 Response 생성
     */
    public static ShipmentDetailResponse fromShipTran(ShipTran shipTran, String shipNumber, 
                                                     String itemCodeNum, BigDecimal orderQuantity) {
        BigDecimal shipQuantity = shipTran.getShipTranCnt() != null ? shipTran.getShipTranCnt() : BigDecimal.ZERO;
        BigDecimal remainQuantity = orderQuantity.subtract(shipQuantity);
        
        return ShipmentDetailResponse.builder()
                .shipNumber(shipNumber)
                .itemCodeNum(itemCodeNum)
                .shipTranItem(shipTran.getShipTranItem())
                .shipTranDeta(shipTran.getShipTranDeta())
                .shipTranSpec(shipTran.getShipTranSpec())
                .shipTranUnit(shipTran.getShipTranUnit())
                .orderQuantity(orderQuantity)
                .shipQuantity(shipQuantity)
                .remainQuantity(remainQuantity)
                .shipTranSeq(shipTran.getShipTranSeq())
                .build();
    }
} 