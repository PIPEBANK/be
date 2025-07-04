package com.pipebank.ordersystem.domain.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCodeResponse {
    
    private Integer itemCodeCode;
    private String itemCodeNum;
    private String itemCodeDcod;
    private String itemCodeDcodName;  // 품목분류코드명
    private String itemCodePcod;
    private String itemCodePcodName;  // 매입품목구분코드명
    private String itemCodeScod;
    private String itemCodeScodName;  // 매출품목구분코드명
    private String itemCodeHnam;
    private String itemCodeEnam;
    private String itemCodeWord;
    private String itemCodeSpec;
    private BigDecimal itemCodeSpec2;
    private String itemCodeUnit;
    private Integer itemCodePcust;
    private String itemCodePcustName;  // 매입처명
    private Integer itemCodePcust2;
    private String itemCodePcust2Name;  // 매입처[이전]명
    private Integer itemCodeScust;
    private String itemCodeScustName;  // 매출처명
    private Integer itemCodeCalc;
    private boolean isCalculated;
    private Integer itemCodeSdiv;
    private Integer itemCodeVdiv;
    private boolean hasVat;
    private Integer itemCodeAdiv;
    private boolean hasAdvance;
    private BigDecimal itemCodePrate;
    private BigDecimal itemCodePlrate;
    private String itemCodePlrdate;
    private BigDecimal itemCodeSrate;
    private BigDecimal itemCodeSlrate;
    private String itemCodeSlrdate;
    private String itemCodeLdiv;
    private String itemCodeLdivName;  // 최종거래가적용구분명
    private Integer itemCodeUse;
    private boolean isActive;
    private BigDecimal itemCodeAvrate;
    private String itemCodeDsdiv;
    private String itemCodeDsdivName;  // D/S구분명
    private String itemCodeBrand;
    private String itemCodePlace;
    private String itemCodeNative;
    private BigDecimal itemCodeStock;
    private Integer itemCodeBitem;
    private Integer itemCodePitem;
    private Integer itemCodeChng;
    private Integer itemCodeAuto;
    private boolean isAutoProcessed;
    private Integer itemCodeMarket;
    private boolean isMarketItem;
    private String itemCodeKitchen;
    private String itemCodeKitchenName;  // 주방구분명
    private Integer itemCodePrint;
    private boolean isPrintable;
    private Integer itemCodeDclock;
    private boolean isDcLocked;
    private String itemCodeLproc;
    private String itemCodeLprocName;  // 최종공정명
    private Integer itemCodeUrate;
    private Integer itemCodeOption;
    private boolean hasOption;
    private Integer itemCodeNstock;
    private boolean isNoStock;
    private Integer itemCodeSerial;
    private String itemCodeDiv1;
    private String itemCodeDiv1Name;  // 제품종류명
    private String itemCodeDiv2;
    private String itemCodeDiv2Name;  // 제품군명
    private String itemCodeDiv3;
    private String itemCodeDiv3Name;  // 제품용도명
    private String itemCodeDiv4;
    private String itemCodeDiv4Name;  // 제품기능명
    private String itemCodeRemark;
    private LocalDateTime itemCodeFdate;
    private String itemCodeFuser;
    private LocalDateTime itemCodeLdate;
    private String itemCodeLuser;
    private Integer itemCodeMoq;
    private BigDecimal itemCodeMweight;
    private BigDecimal itemCodeEaweight;
    private String itemCodeClass;
    private String itemCodeClassName;  // 원재료 분류명
    private BigDecimal itemCodeUnitsrate;
    private Integer itemCodeOrder;
    private boolean isOrderable;
    private BigDecimal itemCodeWamt;
    
    // 추가 편의 필드들
    private String displayName;
    private String shortName;
    private String fullSpec;
    private String brandInfo;
    private String origin;
} 