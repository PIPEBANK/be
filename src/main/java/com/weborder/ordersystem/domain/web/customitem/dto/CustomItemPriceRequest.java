package com.weborder.ordersystem.domain.web.customitem.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomItemPriceRequest {

    @NotNull(message = "판매단가는 필수입니다")
    private BigDecimal srate;

    private String spec;
    private String unit;
    private Integer vatDiv;
    private String remark;

    private String orderDate;
    private Integer orderSosok;
    private String orderUjcd;
    private Integer orderAcno;
    private Integer orderSeq;
}
