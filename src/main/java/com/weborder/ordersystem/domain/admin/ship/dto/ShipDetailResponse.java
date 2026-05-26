package com.weborder.ordersystem.domain.admin.ship.dto;

import com.weborder.ordersystem.domain.erp.entity.ShipMast;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class ShipDetailResponse {
    private String shipDate;
    private Integer sosok;
    private String ujcd;
    private Integer acno;
    private Integer cust;
    private String custName;
    private String naddr;
    private Integer sawon;
    private String tdiv;
    private Integer project;
    private String remark;
    private String shipKey;
    private String fdate;
    private String fuser;
    private String orderKey;
    private BigDecimal totalAmount;
    private List<ShipTranResponse> items;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ShipDetailResponse from(ShipMast m, String custName, String orderKey,
                                            List<ShipTranResponse> items) {
        BigDecimal total = items.stream()
                .map(ShipTranResponse::getTot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ShipDetailResponse.builder()
                .shipDate(m.getShipMastDate())
                .sosok(m.getShipMastSosok())
                .ujcd(m.getShipMastUjcd())
                .acno(m.getShipMastAcno())
                .cust(m.getShipMastCust())
                .custName(custName)
                .naddr(m.getShipMastNaddr())
                .sawon(m.getShipMastSawon())
                .tdiv(m.getShipMastTdiv())
                .project(m.getShipMastProject())
                .remark(m.getShipMastRemark())
                .shipKey(m.getShipKey())
                .fdate(m.getShipMastFdate() != null ? m.getShipMastFdate().format(DT_FMT) : "")
                .fuser(m.getShipMastFuser())
                .orderKey(orderKey)
                .totalAmount(total)
                .items(items)
                .build();
    }
}
