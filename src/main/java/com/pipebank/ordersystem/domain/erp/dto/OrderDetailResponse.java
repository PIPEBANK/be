package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;
import java.util.List;

import com.pipebank.ordersystem.domain.erp.entity.OrderMast;

import lombok.Builder;
import lombok.Getter;

/**
 * 주문 상세조회 응답 DTO
 * OrderMast(헤더) + OrderTran(상세) 정보 포함
 */
@Getter
@Builder
public class OrderDetailResponse {

    // ===== OrderMast 정보 =====
    private String orderNumber;                    // 주문번호 (DATE-ACNO)
    private String orderMastDate;                  // 주문일자
    private String orderMastSdiv;                  // 출고형태 코드
    private String orderMastSdivDisplayName;       // 출고형태명
    private String orderMastOdate;                 // 도착요구일(납기일자)
    private String orderMastOtime;                 // 납기시간
    private String orderMastDcust;                 // 수요처
    private String orderMastComaddr;               // 납품현장주소 (Comaddr1+2 합쳐진 것)
    private String orderMastComname;               // 현장명
    private String orderMastCurrency;              // 화폐코드
    private String orderMastCurrencyDisplayName;   // 화폐코드명
    private String orderMastCurrencyPer;           // 환율
    private String orderMastReason;                // 용도코드
    private String orderMastReasonDisplayName;     // 용도코드명
    private String orderMastComuname;              // 인수자
    private String orderMastComutel;               // 인수자연락처
    private String orderMastRemark;                // 비고

    // ===== OrderTran 정보 =====
    private List<OrderTranDetailResponse> orderTranList;   // 주문 상세 목록
    private BigDecimal orderTranTotalAmount;               // 주문 총 금액 (orderTranTot 합계)
    
    // ===== 🔥 미출고 정보 =====
    private BigDecimal pendingTotalAmount;                 // 미출고금액 총액 (각 Tran의 pendingAmount 합계)

    // ===== 🧮 합계 정보(요청 추가) =====
    private BigDecimal orderTranCntTotal;                  // 주문수량 합계 (orderTranCnt 합)
    private BigDecimal shipQuantityTotal;                  // 출하수량 합계 (shipQuantity 합)
    private BigDecimal orderTranAmtTotal;                  // 단가 합계 (orderTranAmt 합)
    private BigDecimal orderTranNetTotal;                  // 공급가 합계 (orderTranNet 합)
    private BigDecimal orderTranVatTotal;                  // 부가세 합계 (orderTranVat 합)
    private BigDecimal orderTranTotTotal;                  // 주문금액 합계 (orderTranTot 합)

    /**
     * OrderMast 엔티티에서 기본 정보 변환
     */
    public static OrderDetailResponse fromOrderMast(OrderMast orderMast) {
        return OrderDetailResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName("") // Service에서 설정
                .orderMastOdate(orderMast.getOrderMastOdate())
                .orderMastOtime(orderMast.getOrderMastOtime())
                .orderMastDcust(orderMast.getOrderMastDcust())
                .orderMastComaddr(buildFullAddress(orderMast.getOrderMastComaddr1(), orderMast.getOrderMastComaddr2()))
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastCurrency(orderMast.getOrderMastCurrency())
                .orderMastCurrencyDisplayName("") // Service에서 설정
                .orderMastCurrencyPer(orderMast.getOrderMastCurrencyPer())
                .orderMastReason(orderMast.getOrderMastReason())
                .orderMastReasonDisplayName("") // Service에서 설정
                .orderMastComuname(orderMast.getOrderMastComuname())
                .orderMastComutel(orderMast.getOrderMastComutel())
                .orderMastRemark(orderMast.getOrderMastRemark())
                .orderTranList(null) // 추후 설정
                .build();
    }

    /**
     * 주소 합치기 헬퍼 메서드
     */
    private static String buildFullAddress(String addr1, String addr2) {
        if (addr1 != null && addr2 != null) {
            return addr1 + " " + addr2;
        } else if (addr1 != null) {
            return addr1;
        } else if (addr2 != null) {
            return addr2;
        }
        return "";
    }

} 