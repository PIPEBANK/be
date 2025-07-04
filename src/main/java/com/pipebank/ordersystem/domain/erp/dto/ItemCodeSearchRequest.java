package com.pipebank.ordersystem.domain.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCodeSearchRequest {
    
    private String keyword;           // 통합 검색 키워드
    private String itemCodeNum;       // 품목번호
    private String itemCodeHnam;      // 품목명(한글)
    private String itemCodeEnam;      // 품목명(영문)
    private String itemCodeSpec;      // 품목규격
    private String itemCodeBrand;     // 브랜드
    private String itemCodeNative;    // 제조사
    private Integer itemCodePcust;    // 매입처
    private Integer itemCodeScust;    // 매출처
    private String itemCodeDiv1;      // 제품종류
    private String itemCodeDiv2;      // 제품군
    private String itemCodeDiv3;      // 제품용도
    private String itemCodeDiv4;      // 제품기능
    private String itemCodeDcod;      // 품목분류코드
    private String itemCodePcod;      // 매입품목구분코드
    private String itemCodeScod;      // 매출품목구분코드
    private String itemCodeLdiv;      // 최종거래가적용구분
    private String itemCodeDsdiv;     // D/S구분
    private String itemCodeKitchen;   // 주방구분
    private String itemCodeLproc;     // 최종공정
    private String itemCodeClass;     // 원재료분류
    private Integer itemCodeUse;      // 사용여부 (1: 사용, 0: 미사용)
    private Integer itemCodeOrder;    // 오더센터 사용여부
    private Integer itemCodeNstock;   // 재고관리 여부
    private Integer itemCodeAuto;     // 자동매입 여부
    private Integer itemCodeMarket;   // 시장사입 여부
    private Integer itemCodeCalc;     // 정산 여부
    private Integer itemCodeVdiv;     // 부가세 여부
    private Integer itemCodeAdiv;     // 예수금 여부
    private Integer itemCodePrint;    // 주문서 출력 여부
    private Integer itemCodeDclock;   // D/C 잠금 여부
    private Integer itemCodeOption;   // 주문옵션 사용 여부
    private Integer startCode;        // 품목코드 시작 범위
    private Integer endCode;          // 품목코드 끝 범위
    private Boolean activeOnly;       // 활성 품목만 조회 여부
} 