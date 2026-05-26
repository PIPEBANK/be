package com.weborder.ordersystem.domain.admin.srate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemSrateRequest {

    @NotNull
    private Integer itemCode;

    @NotNull
    private Integer custCode;

    @NotNull
    private BigDecimal rate;

    private String remark;
}
