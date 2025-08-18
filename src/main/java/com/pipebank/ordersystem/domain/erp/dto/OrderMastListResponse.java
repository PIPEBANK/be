package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;

import com.pipebank.ordersystem.domain.erp.entity.OrderMast;

import lombok.Builder;
import lombok.Getter;

/**
 * 주문 목록 조회용 응답 DTO (성능 최적화)
 * 목록 화면에 필요한 최소한의 정보만 포함
 */
@Getter
@Builder
public class OrderMastListResponse {

    private String orderNumber;              // 주문번호 (DATE-ACNO)
    private String orderMastSdiv;           // 출고형태 코드
    private String orderMastSdivDisplayName; // 출고형태명
    private String orderMastComname;        // 납품현장명
    private String orderMastDate;           // 주문일자
    private Integer orderMastCust;          // 거래처코드
    private String orderMastStatus;         // 주문상태 코드 (계산된 값)
    private String orderMastStatusDisplayName; // 주문상태명 (계산된 값)
    
    // 🆕 추가된 금액 정보
    private BigDecimal orderTranTotalAmount;   // 주문 총 금액 (orderTranTot 합계)
    private BigDecimal pendingTotalAmount;     // 미출고금액 총액

    /**
     * OrderMast 엔티티에서 기본 정보만 변환 (금액 정보 없음)
     */
    public static OrderMastListResponse from(OrderMast orderMast) {
        return OrderMastListResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName("") // 기본값, Service에서 설정
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastCust(orderMast.getOrderMastCust())
                .orderMastStatus("") // 기본값, Service에서 설정
                .orderMastStatusDisplayName("") // 기본값, Service에서 설정
                .orderTranTotalAmount(BigDecimal.ZERO) // 기본값
                .pendingTotalAmount(BigDecimal.ZERO) // 기본값
                .build();
    }

    /**
     * 출고형태명과 함께 생성 (금액 정보 없음)
     */
    public static OrderMastListResponse fromWithDisplayName(OrderMast orderMast, String sdivDisplayName) {
        return OrderMastListResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName != null ? sdivDisplayName : "")
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastCust(orderMast.getOrderMastCust())
                .orderMastStatus("") // 기본값, Service에서 설정
                .orderMastStatusDisplayName("") // 기본값, Service에서 설정
                .orderTranTotalAmount(BigDecimal.ZERO) // 기본값
                .pendingTotalAmount(BigDecimal.ZERO) // 기본값
                .build();
    }

    /**
     * 상태 정보와 함께 생성 (금액 정보 없음)
     */
    public static OrderMastListResponse fromWithStatusAndDisplayNames(OrderMast orderMast, 
                                                                      String sdivDisplayName,
                                                                      String status,
                                                                      String statusDisplayName) {
        return OrderMastListResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName != null ? sdivDisplayName : "")
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastCust(orderMast.getOrderMastCust())
                .orderMastStatus(status != null ? status : "")
                .orderMastStatusDisplayName(statusDisplayName != null ? statusDisplayName : "")
                .orderTranTotalAmount(BigDecimal.ZERO) // 기본값
                .pendingTotalAmount(BigDecimal.ZERO) // 기본값
                .build();
    }
    
    /**
     * 🆕 금액 정보를 포함한 완전한 생성 메서드
     */
    public static OrderMastListResponse fromWithAmounts(OrderMast orderMast, 
                                                        String sdivDisplayName,
                                                        String status,
                                                        String statusDisplayName,
                                                        BigDecimal totalAmount,
                                                        BigDecimal pendingAmount) {
        return OrderMastListResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName != null ? sdivDisplayName : "")
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastCust(orderMast.getOrderMastCust())
                .orderMastStatus(status != null ? status : "")
                .orderMastStatusDisplayName(statusDisplayName != null ? statusDisplayName : "")
                .orderTranTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO)
                .pendingTotalAmount(pendingAmount != null ? pendingAmount : BigDecimal.ZERO)
                .build();
    }
} 