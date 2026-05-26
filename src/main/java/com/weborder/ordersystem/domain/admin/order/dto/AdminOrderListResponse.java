package com.weborder.ordersystem.domain.admin.order.dto;

import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class AdminOrderListResponse {
    private String orderDate;
    private Integer sosok;
    private String ujcd;
    private Integer acno;
    private Integer cust;
    private String custName;
    private String custCodeName;
    private String memberName;
    private String odate;
    private String remark;
    private String webOrderStatus;
    private String orderKey;
    private String fdate;
    private BigDecimal totalAmount;
    private Integer itemCount;
    private String guestCompanyName;
    private String driverSignAt;
    private Long webMemberId;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static AdminOrderListResponse from(WebOrderMast m, String custName,
                                                BigDecimal totalAmount, Integer itemCount) {
        return from(m, custName, totalAmount, itemCount, null);
    }

    public static AdminOrderListResponse from(WebOrderMast m, String custName,
                                                BigDecimal totalAmount, Integer itemCount,
                                                String guestCompanyName) {
        String displayName = (guestCompanyName != null && !guestCompanyName.isBlank())
                ? guestCompanyName : custName;

        return AdminOrderListResponse.builder()
                .orderDate(m.getOrderMastDate())
                .sosok(m.getOrderMastSosok())
                .ujcd(m.getOrderMastUjcd())
                .acno(m.getOrderMastAcno())
                .cust(m.getOrderMastCust())
                .custName(displayName)
                .custCodeName(displayName)
                .memberName(displayName)
                .odate(m.getOrderMastOdate())
                .remark(m.getOrderMastRemark())
                .webOrderStatus(m.getWebOrderStatus())
                .orderKey(m.getOrderKey())
                .fdate(m.getOrderMastFdate() != null ? m.getOrderMastFdate().format(DT_FMT) : "")
                .totalAmount(totalAmount)
                .itemCount(itemCount)
                .guestCompanyName(guestCompanyName)
                .driverSignAt(m.getWebDriverSignAt() != null ? m.getWebDriverSignAt().format(DT_FMT) : null)
                .webMemberId(m.getWebMemberId())
                .build();
    }
}
