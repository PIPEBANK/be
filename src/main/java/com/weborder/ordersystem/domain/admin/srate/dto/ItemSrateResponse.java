package com.weborder.ordersystem.domain.admin.srate.dto;

import com.weborder.ordersystem.domain.erp.entity.ItemSrate;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ItemSrateResponse {
    private Integer itemCode;
    private Integer custCode;
    private String itemName;
    private BigDecimal defaultRate;
    private BigDecimal customerRate;
    private String remark;

    public static ItemSrateResponse from(ItemSrate srate, String itemName, BigDecimal defaultRate) {
        return ItemSrateResponse.builder()
                .itemCode(srate.getItemCode())
                .custCode(srate.getCustCode())
                .itemName(itemName)
                .defaultRate(defaultRate)
                .customerRate(srate.getItemSrateRate())
                .remark(srate.getItemSrateRemark())
                .build();
    }
}
