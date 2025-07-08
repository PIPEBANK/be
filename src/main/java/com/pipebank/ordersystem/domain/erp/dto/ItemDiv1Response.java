package com.pipebank.ordersystem.domain.erp.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 제품종류(DIV1) 조회 응답 DTO
 */
@Getter
@Builder
public class ItemDiv1Response {
    
    private String code;        // 제품종류 코드 (1자리)
    private String name;        // 제품종류명
    private boolean isActive;   // 사용여부
    
    public static ItemDiv1Response of(String code, String name, boolean isActive) {
        return ItemDiv1Response.builder()
                .code(code)
                .name(name)
                .isActive(isActive)
                .build();
    }
} 