package com.pipebank.ordersystem.domain.web.temp.dto;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempWebOrderMastListResponse {
    
    /**
     * 주문번호 (orderMastDate + "-" + orderMastAcno)
     */
    private String orderNumber;
    
    /**
     * 작성자 (userId)
     */
    private String userId;
    
    /**
     * 현장명 (orderMastComname)
     */
    private String orderMastComname;
    
    /**
     * 주문일자 (orderMastDate)
     */
    private String orderMastDate;
    
    /**
     * 🔥 임시주문 ID (tempOrderId) - 중복 주문번호 구분용
     */
    private Integer tempOrderId;
    
    /**
     * TempWebOrderMast Entity를 TempWebOrderMastListResponse로 변환
     */
    public static TempWebOrderMastListResponse from(TempWebOrderMast tempWebOrderMast) {
        String orderNumber = tempWebOrderMast.getOrderMastDate() + "-" + tempWebOrderMast.getOrderMastAcno();
        
        return TempWebOrderMastListResponse.builder()
                .orderNumber(orderNumber)
                .userId(tempWebOrderMast.getUserId())
                .orderMastComname(tempWebOrderMast.getOrderMastComname())
                .orderMastDate(tempWebOrderMast.getOrderMastDate())
                .tempOrderId(tempWebOrderMast.getTempOrderId()) // 🔥 tempOrderId 추가
                .build();
    }
} 