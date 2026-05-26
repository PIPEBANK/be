package com.weborder.ordersystem.domain.admin.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    private String itemNum;          // 품목번호
    private String itemName;         // 품목명(한글)
    private String itemEname;        // 품목명(영문)
    private String itemWord;         // 품목명(이니셜)
    private String spec;             // 규격
    private BigDecimal spec2;        // 표준중량
    private String unit;             // 단위

    private Integer pcust;           // 매입처 (Customer)
    private Integer pcust2;          // 매입처[이전] (Customer)
    private Integer scust;           // 매출처 (Customer)

    private BigDecimal purchaseRate; // 매입단가
    private BigDecimal lastPurchaseRate; // 최종매입단가
    private String lastPurchaseDate; // 최종매입일 (char8)
    private BigDecimal saleRate;     // 판매단가
    private BigDecimal lastSaleRate; // 최종판매단가
    private String lastSaleDate;     // 최종판매일 (char8)
    private BigDecimal avrate;       // 평균단가

    private String dcod;             // 품목분류코드
    private String pcod;             // 매입품목구분코드
    private String scod;             // 매출품목구분코드
    private String ldiv;             // 거래단가반영구분코드
    private String dsdiv;            // D/S구분코드
    private String lproc;            // 공정구분코드

    private Integer use;             // 사용여부 (0/1)
    private BigDecimal stock;        // 재고수량
    private String remark;           // 비고 (max 200)
    private String tags;             // 검색태그 (예: #물 #워터 #생수, max 500)
}
