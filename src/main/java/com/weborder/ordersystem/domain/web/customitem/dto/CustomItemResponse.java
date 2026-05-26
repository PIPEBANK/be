package com.weborder.ordersystem.domain.web.customitem.dto;

import java.math.BigDecimal;
import java.util.List;

import com.weborder.ordersystem.domain.web.customitem.entity.CustomItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomItemResponse {

    private Long id;
    private Integer custCode;
    private String name;
    private String description;
    private String spec;
    private String unit;
    private Integer vatDiv;
    private BigDecimal srate;
    private BigDecimal slrate;
    private String lastOrderDate;
    private String remark;
    private boolean active;
    private boolean hasPrice;
    private String thumbnailUrl;
    private List<String> imageUrls;

    public static CustomItemResponse from(CustomItem entity, String thumbnailUrl, List<String> imageUrls) {
        return CustomItemResponse.builder()
                .id(entity.getCustomItemCode())
                .custCode(entity.getCustomItemCust())
                .name(entity.getCustomItemHnam())
                .description(entity.getCustomItemDesc())
                .spec(entity.getCustomItemSpec())
                .unit(entity.getCustomItemUnit())
                .vatDiv(entity.getCustomItemVdiv())
                .srate(entity.getCustomItemSrate())
                .slrate(entity.getCustomItemSlrate())
                .lastOrderDate(entity.getCustomItemSlrdate())
                .remark(entity.getCustomItemRemark())
                .active(entity.isActive())
                .hasPrice(entity.hasPrice())
                .thumbnailUrl(thumbnailUrl)
                .imageUrls(imageUrls)
                .build();
    }
}
