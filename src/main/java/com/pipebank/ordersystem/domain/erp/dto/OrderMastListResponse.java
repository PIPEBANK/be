package com.pipebank.ordersystem.domain.erp.dto;

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

    /**
     * OrderMast 엔티티에서 기본 정보만 변환
     */
    public static OrderMastListResponse from(OrderMast orderMast) {
        return OrderMastListResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName("") // 기본값, Service에서 설정
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastCust(orderMast.getOrderMastCust())
                .build();
    }

    /**
     * 출고형태명과 함께 생성
     */
    public static OrderMastListResponse fromWithDisplayName(OrderMast orderMast, String sdivDisplayName) {
        return OrderMastListResponse.builder()
                .orderNumber(orderMast.getOrderMastDate() + "-" + orderMast.getOrderMastAcno())
                .orderMastSdiv(orderMast.getOrderMastSdiv())
                .orderMastSdivDisplayName(sdivDisplayName != null ? sdivDisplayName : "")
                .orderMastComname(orderMast.getOrderMastComname())
                .orderMastDate(orderMast.getOrderMastDate())
                .orderMastCust(orderMast.getOrderMastCust())
                .build();
    }
} 