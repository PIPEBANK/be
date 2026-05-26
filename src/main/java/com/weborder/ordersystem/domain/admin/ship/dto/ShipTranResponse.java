package com.weborder.ordersystem.domain.admin.ship.dto;

import com.weborder.ordersystem.domain.erp.entity.ShipTran;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ShipTranResponse {
    private Integer seq;
    private Integer itemCode;
    private String itemName;
    private String spec;
    private String unit;
    private BigDecimal rate;
    private BigDecimal cnt;
    private BigDecimal ocnt;
    private BigDecimal net;
    private BigDecimal vat;
    private BigDecimal tot;
    private String remark;

    public static ShipTranResponse from(ShipTran t) {
        return ShipTranResponse.builder()
                .seq(t.getShipTranSeq())
                .itemCode(t.getShipTranItem())
                .itemName(t.getShipTranDeta())
                .spec(t.getShipTranSpec())
                .unit(t.getShipTranUnit())
                .rate(t.getShipTranRate())
                .cnt(t.getShipTranCnt())
                .ocnt(t.getShipTranOcnt())
                .net(t.getShipTranNet())
                .vat(t.getShipTranVat())
                .tot(t.getShipTranTot())
                .remark(t.getShipTranRemark())
                .build();
    }
}
