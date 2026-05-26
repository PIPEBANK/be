package com.weborder.ordersystem.domain.web.product.dto;

import com.weborder.ordersystem.domain.erp.entity.ItemCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
public class UserProductListResponse {
    private Integer itemCode;
    private String itemName;
    private String spec;
    private String unit;
    private BigDecimal saleRate;
    private BigDecimal displayPrice;
    private BigDecimal customerRate;
    private BigDecimal customerDisplayPrice;
    private Boolean hidePrice;
    private String thumbnailUrl;

    public static UserProductListResponse from(ItemCode item, String thumbnailUrl) {
        return from(item, thumbnailUrl, null, false);
    }

    public static UserProductListResponse from(ItemCode item, String thumbnailUrl,
                                                BigDecimal custRate, boolean hidePrice) {
        BigDecimal defaultRate = item.getItemCodeSrate();
        BigDecimal defaultDp = defaultRate;
        if (item.getItemCodeVdiv() != null && item.getItemCodeVdiv() == 1 && defaultRate != null) {
            defaultDp = defaultRate.multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal effectiveRate = custRate != null && custRate.compareTo(BigDecimal.ZERO) > 0
                ? custRate : defaultRate;
        BigDecimal effectiveDp = effectiveRate;
        if (item.getItemCodeVdiv() != null && item.getItemCodeVdiv() == 1 && effectiveRate != null) {
            effectiveDp = effectiveRate.multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.HALF_UP);
        }

        return UserProductListResponse.builder()
                .itemCode(item.getItemCodeCode())
                .itemName(item.getItemCodeHnam())
                .spec(item.getItemCodeSpec())
                .unit(item.getItemCodeUnit())
                .saleRate(effectiveRate)
                .displayPrice(hidePrice ? null : effectiveDp)
                .customerRate(custRate)
                .customerDisplayPrice(custRate != null && custRate.compareTo(BigDecimal.ZERO) > 0
                        ? effectiveDp : null)
                .hidePrice(hidePrice)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
