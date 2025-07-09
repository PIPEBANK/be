package com.pipebank.ordersystem.domain.web.temp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempWebOrderTranCreateRequest {
    
    // 복합키 필드들
    private String orderTranDate;
    private Integer orderTranSosok;
    private String orderTranUjcd;
    private Integer orderTranAcno; // 통합 API에서 자동으로 설정됨
    // orderTranSeq는 자동생성
    
    // 기본 필드들
    private String orderTranItemVer;
    private Integer orderTranItem;
    private String orderTranDeta;
    private String orderTranSpec;
    private String orderTranUnit;
    private Integer orderTranCalc;
    private Integer orderTranVdiv;
    private Integer orderTranAdiv;
    private BigDecimal orderTranRate;
    private BigDecimal orderTranCnt;
    private BigDecimal orderTranConvertWeight;
    private BigDecimal orderTranDcPer;
    private BigDecimal orderTranDcAmt;
    private BigDecimal orderTranForiAmt;
    private BigDecimal orderTranAmt;
    private BigDecimal orderTranNet;
    private BigDecimal orderTranVat;
    private BigDecimal orderTranAdv;
    private BigDecimal orderTranTot;
    private BigDecimal orderTranLrate;
    private BigDecimal orderTranPrice;
    private BigDecimal orderTranPrice2;
    private Integer orderTranLdiv;
    private String orderTranRemark;
    private String orderTranStau;
    // orderTranFdate, orderTranFuser, orderTranLdate, orderTranLuser는 자동생성
    private BigDecimal orderTranWamt;
    
    // 임시저장용 필드들
    private String userId;
    @Builder.Default
    private Boolean send = false; // 기본값: 임시저장
} 