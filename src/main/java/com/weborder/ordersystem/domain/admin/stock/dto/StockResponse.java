package com.weborder.ordersystem.domain.admin.stock.dto;

import com.weborder.ordersystem.domain.erp.entity.StockDate;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StockResponse {
    private String dcod;
    private Integer itemCode;
    private Integer sosok;
    private Integer buse;
    private BigDecimal cnt;
    private String itemName;
    private String itemSpec;
    private String itemUnit;
    private BigDecimal availableStock;
    private String stockKey;

    public static StockResponse from(StockDate stock, String itemName, String itemSpec,
                                      String itemUnit, BigDecimal availableStock) {
        return StockResponse.builder()
                .dcod(stock.getStockDateDcod())
                .itemCode(stock.getStockDateItem())
                .sosok(stock.getStockDateSosok())
                .buse(stock.getStockDateBuse())
                .cnt(stock.getStockDateCnt())
                .itemName(itemName)
                .itemSpec(itemSpec)
                .itemUnit(itemUnit)
                .availableStock(availableStock)
                .stockKey(stock.getStockKey())
                .build();
    }
}
