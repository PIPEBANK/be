package com.pipebank.ordersystem.domain.web.order.dto;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderTran;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WebOrderTranResponse {

    private String orderTranDate;
    private Integer orderTranSosok;
    private String orderTranUjcd;
    private Integer orderTranAcno;
    private Integer orderTranSeq;
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

    // 비즈니스 메서드 결과
    private String orderTranKey;

    public WebOrderTranResponse(String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno,
                              Integer orderTranSeq, String orderTranItemVer, Integer orderTranItem, String orderTranDeta,
                              String orderTranSpec, String orderTranUnit, Integer orderTranCalc, Integer orderTranVdiv,
                              Integer orderTranAdiv, BigDecimal orderTranRate, BigDecimal orderTranCnt, BigDecimal orderTranConvertWeight,
                              BigDecimal orderTranDcPer, BigDecimal orderTranDcAmt, BigDecimal orderTranForiAmt,
                              BigDecimal orderTranAmt, BigDecimal orderTranNet, BigDecimal orderTranVat,
                              BigDecimal orderTranAdv, BigDecimal orderTranTot, BigDecimal orderTranLrate,
                              BigDecimal orderTranPrice, BigDecimal orderTranPrice2, Integer orderTranLdiv,
                              String orderTranRemark, String orderTranStau, LocalDateTime orderTranFdate, String orderTranFuser,
                              LocalDateTime orderTranLdate, String orderTranLuser, BigDecimal orderTranWamt, String orderTranKey) {
        this.orderTranDate = orderTranDate;
        this.orderTranSosok = orderTranSosok;
        this.orderTranUjcd = orderTranUjcd;
        this.orderTranAcno = orderTranAcno;
        this.orderTranSeq = orderTranSeq;
        this.orderTranItemVer = orderTranItemVer;
        this.orderTranItem = orderTranItem;
        this.orderTranDeta = orderTranDeta;
        this.orderTranSpec = orderTranSpec;
        this.orderTranUnit = orderTranUnit;
        this.orderTranCalc = orderTranCalc;
        this.orderTranVdiv = orderTranVdiv;
        this.orderTranAdiv = orderTranAdiv;
        this.orderTranRate = orderTranRate;
        this.orderTranCnt = orderTranCnt;
        this.orderTranConvertWeight = orderTranConvertWeight;
        this.orderTranDcPer = orderTranDcPer;
        this.orderTranDcAmt = orderTranDcAmt;
        this.orderTranForiAmt = orderTranForiAmt;
        this.orderTranAmt = orderTranAmt;
        this.orderTranNet = orderTranNet;
        this.orderTranVat = orderTranVat;
        this.orderTranAdv = orderTranAdv;
        this.orderTranTot = orderTranTot;
        this.orderTranLrate = orderTranLrate;
        this.orderTranPrice = orderTranPrice;
        this.orderTranPrice2 = orderTranPrice2;
        this.orderTranLdiv = orderTranLdiv;
        this.orderTranRemark = orderTranRemark;
        this.orderTranStau = orderTranStau;
        this.orderTranFdate = orderTranFdate;
        this.orderTranFuser = orderTranFuser;
        this.orderTranLdate = orderTranLdate;
        this.orderTranLuser = orderTranLuser;
        this.orderTranWamt = orderTranWamt;
        this.orderTranKey = orderTranKey;
    }

    public static WebOrderTranResponse from(WebOrderTran entity) {
        return new WebOrderTranResponse(
                entity.getOrderTranDate(),
                entity.getOrderTranSosok(),
                entity.getOrderTranUjcd(),
                entity.getOrderTranAcno(),
                entity.getOrderTranSeq(),
                entity.getOrderTranItemVer(),
                entity.getOrderTranItem(),
                entity.getOrderTranDeta(),
                entity.getOrderTranSpec(),
                entity.getOrderTranUnit(),
                entity.getOrderTranCalc(),
                entity.getOrderTranVdiv(),
                entity.getOrderTranAdiv(),
                entity.getOrderTranRate(),
                entity.getOrderTranCnt(),
                entity.getOrderTranConvertWeight(),
                entity.getOrderTranDcPer(),
                entity.getOrderTranDcAmt(),
                entity.getOrderTranForiAmt(),
                entity.getOrderTranAmt(),
                entity.getOrderTranNet(),
                entity.getOrderTranVat(),
                entity.getOrderTranAdv(),
                entity.getOrderTranTot(),
                entity.getOrderTranLrate(),
                entity.getOrderTranPrice(),
                entity.getOrderTranPrice2(),
                entity.getOrderTranLdiv(),
                entity.getOrderTranRemark(),
                entity.getOrderTranStau(),
                entity.getOrderTranFdate(),
                entity.getOrderTranFuser(),
                entity.getOrderTranLdate(),
                entity.getOrderTranLuser(),
                entity.getOrderTranWamt(),
                entity.getOrderTranKey()
        );
    }
} 