package com.weborder.ordersystem.domain.admin.stock.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class StockUpdateRequest {
    private BigDecimal cnt;        // 변경할 재고수량 (직접 설정)
    private BigDecimal adjustment; // 증감량 (+/- 값)
    private String dcod;           // 창고코드 (신규등록시)
    private Integer buse;          // 부서 (신규등록시, 기본 7)
}
