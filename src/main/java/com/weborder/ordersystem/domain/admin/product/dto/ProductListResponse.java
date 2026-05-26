package com.weborder.ordersystem.domain.admin.product.dto;

import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductListResponse {
    private Integer itemCode;
    private String itemNum;
    private String itemName;
    private String spec;
    private String unit;
    private BigDecimal saleRate;
    private String brand;
    private Integer use;
    private BigDecimal stockQuantity;
    private String thumbnailUrl;

    public static ProductListResponse from(ItemCode item, BigDecimal stockQty, String thumbnailUrl) {
        return ProductListResponse.builder()
                .itemCode(item.getItemCodeCode())
                .itemNum(item.getItemCodeNum())
                .itemName(item.getItemCodeHnam())
                .spec(item.getItemCodeSpec())
                .unit(item.getItemCodeUnit())
                .saleRate(item.getItemCodeSrate())
                .brand(item.getItemCodeBrand())
                .use(item.getItemCodeUse())
                .stockQuantity(stockQty)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
