package com.pipebank.ordersystem.domain.web.temp.dto;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TempWebOrderTranResponse {
    
    // 복합키 필드들
    private String orderTranDate;
    private Integer orderTranSosok;
    private String orderTranUjcd;
    private Integer orderTranAcno;
    private Integer orderTranSeq;
    
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
    private LocalDateTime orderTranFdate;
    private String orderTranFuser;
    private LocalDateTime orderTranLdate;
    private String orderTranLuser;
    private BigDecimal orderTranWamt;
    
    // 임시저장용 필드들
    private String userId;
    private Boolean send;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 비즈니스 메서드들 (Entity와 동일)
    public String getOrderTranKey() {
        return orderTranDate + "-" + orderTranSosok + "-" + orderTranUjcd + "-" + orderTranAcno + "-" + orderTranSeq;
    }

    // 임시저장 상태인지 확인
    public boolean isTempSaved() {
        return !Boolean.TRUE.equals(send);
    }

    // 전송 완료 상태인지 확인
    public boolean isSent() {
        return Boolean.TRUE.equals(send);
    }
    
    public static TempWebOrderTranResponse from(TempWebOrderTran entity) {
        TempWebOrderTranResponse response = new TempWebOrderTranResponse();
        response.orderTranDate = entity.getOrderTranDate();
        response.orderTranSosok = entity.getOrderTranSosok();
        response.orderTranUjcd = entity.getOrderTranUjcd();
        response.orderTranAcno = entity.getOrderTranAcno();
        response.orderTranSeq = entity.getOrderTranSeq();
        response.orderTranItemVer = entity.getOrderTranItemVer();
        response.orderTranItem = entity.getOrderTranItem();
        response.orderTranDeta = entity.getOrderTranDeta();
        response.orderTranSpec = entity.getOrderTranSpec();
        response.orderTranUnit = entity.getOrderTranUnit();
        response.orderTranCalc = entity.getOrderTranCalc();
        response.orderTranVdiv = entity.getOrderTranVdiv();
        response.orderTranAdiv = entity.getOrderTranAdiv();
        response.orderTranRate = entity.getOrderTranRate();
        response.orderTranCnt = entity.getOrderTranCnt();
        response.orderTranConvertWeight = entity.getOrderTranConvertWeight();
        response.orderTranDcPer = entity.getOrderTranDcPer();
        response.orderTranDcAmt = entity.getOrderTranDcAmt();
        response.orderTranForiAmt = entity.getOrderTranForiAmt();
        response.orderTranAmt = entity.getOrderTranAmt();
        response.orderTranNet = entity.getOrderTranNet();
        response.orderTranVat = entity.getOrderTranVat();
        response.orderTranAdv = entity.getOrderTranAdv();
        response.orderTranTot = entity.getOrderTranTot();
        response.orderTranLrate = entity.getOrderTranLrate();
        response.orderTranPrice = entity.getOrderTranPrice();
        response.orderTranPrice2 = entity.getOrderTranPrice2();
        response.orderTranLdiv = entity.getOrderTranLdiv();
        response.orderTranRemark = entity.getOrderTranRemark();
        response.orderTranStau = entity.getOrderTranStau();
        response.orderTranFdate = entity.getOrderTranFdate();
        response.orderTranFuser = entity.getOrderTranFuser();
        response.orderTranLdate = entity.getOrderTranLdate();
        response.orderTranLuser = entity.getOrderTranLuser();
        response.orderTranWamt = entity.getOrderTranWamt();
        response.userId = entity.getUserId();
        response.send = entity.getSend();
        response.createdAt = entity.getCreatedAt();
        response.updatedAt = entity.getUpdatedAt();
        return response;
    }
} 