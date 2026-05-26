package com.weborder.ordersystem.domain.web.order.dto;

import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
public class OrderTranResponse {
    private Integer seq;
    private Integer itemCode;
    private String itemNum;
    private String itemName;
    private String spec;
    private String unit;
    private BigDecimal rate;
    private BigDecimal displayPrice;
    private BigDecimal cnt;
    private BigDecimal dcPer;
    private BigDecimal dcAmt;
    private BigDecimal amt;
    private BigDecimal net;
    private BigDecimal vat;
    private BigDecimal tot;
    private String remark;
    private String stau;
    private String stauName;
    private Long customItemId;
    private String thumbnailUrl;

    public static OrderTranResponse from(WebOrderTran t) {
        return from(t, null, null, null);
    }

    public static OrderTranResponse from(WebOrderTran t, String stauName) {
        return from(t, stauName, null, null);
    }

    public static OrderTranResponse from(WebOrderTran t, String stauName, String itemNum) {
        return from(t, stauName, itemNum, null);
    }

    public static OrderTranResponse from(WebOrderTran t, String stauName, String itemNum, String thumbnailUrl) {
        BigDecimal dp = t.getOrderTranRate();
        if (t.getOrderTranVdiv() != null && t.getOrderTranVdiv() == 1 && dp != null) {
            dp = dp.multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.HALF_UP);
        }

        return OrderTranResponse.builder()
                .seq(t.getOrderTranSeq())
                .itemCode(t.getOrderTranItem())
                .itemNum(itemNum)
                .itemName(t.getOrderTranDeta())
                .spec(t.getOrderTranSpec())
                .unit(t.getOrderTranUnit())
                .rate(t.getOrderTranRate())
                .displayPrice(dp)
                .cnt(t.getOrderTranCnt())
                .dcPer(t.getOrderTranDcPer())
                .dcAmt(t.getOrderTranDcAmt())
                .amt(t.getOrderTranAmt())
                .net(t.getOrderTranNet())
                .vat(t.getOrderTranVat())
                .tot(t.getOrderTranTot())
                .remark(t.getOrderTranRemark())
                .stau(t.getOrderTranStau())
                .stauName(stauName)
                .customItemId(t.getOrderTranCustomitem())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
