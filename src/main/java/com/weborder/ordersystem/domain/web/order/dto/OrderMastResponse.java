package com.weborder.ordersystem.domain.web.order.dto;

import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class OrderMastResponse {
    private String orderDate;
    private Integer sosok;
    private String ujcd;
    private Integer acno;
    private Integer cust;
    private Integer sawon;
    private String odate;
    private Integer project;
    private String remark;
    private String fdate;
    private String fuser;
    private String ldate;
    private String luser;
    private Long webMemberId;
    private String webOrderStatus;
    private String orderKey;
    private List<OrderTranResponse> items;
    private Integer itemCount;
    private BigDecimal totalAmount;
    private String stau;
    private String stauName;
    private String memberName;
    private String custCodeName;
    private String custCodeAddr;
    private Long webDriverId;
    private String driverName;
    private String driverMemberId;
    private String confirmedAt;
    private String driverSign;
    private String driverSignAt;
    private String driverSignName;
    private String custSign;
    private String custSignAt;
    private String custSignName;

    // 비회원 주문 정보
    private String guestCompanyName;
    private String guestManagerName;
    private String guestContact;
    private String guestAddress;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static OrderMastResponse from(WebOrderMast m) {
        return OrderMastResponse.builder()
                .orderDate(m.getOrderMastDate())
                .sosok(m.getOrderMastSosok())
                .ujcd(m.getOrderMastUjcd())
                .acno(m.getOrderMastAcno())
                .cust(m.getOrderMastCust())
                .sawon(m.getOrderMastSawon())
                .odate(m.getOrderMastOdate())
                .project(m.getOrderMastProject())
                .remark(m.getOrderMastRemark())
                .fdate(m.getOrderMastFdate() != null ? m.getOrderMastFdate().format(DT_FMT) : "")
                .fuser(m.getOrderMastFuser())
                .ldate(m.getOrderMastLdate() != null ? m.getOrderMastLdate().format(DT_FMT) : "")
                .luser(m.getOrderMastLuser())
                .webMemberId(m.getWebMemberId())
                .webOrderStatus(m.getWebOrderStatus())
                .orderKey(m.getOrderKey())
                .build();
    }

    public static OrderMastResponse from(WebOrderMast m, List<OrderTranResponse> items) {
        return from(m, items, null, null);
    }

    public static OrderMastResponse from(WebOrderMast m, List<OrderTranResponse> items, String stau, String stauName) {
        BigDecimal total = items.stream()
                .map(OrderTranResponse::getTot)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderMastResponse.builder()
                .orderDate(m.getOrderMastDate())
                .sosok(m.getOrderMastSosok())
                .ujcd(m.getOrderMastUjcd())
                .acno(m.getOrderMastAcno())
                .cust(m.getOrderMastCust())
                .sawon(m.getOrderMastSawon())
                .odate(m.getOrderMastOdate())
                .project(m.getOrderMastProject())
                .remark(m.getOrderMastRemark())
                .fdate(m.getOrderMastFdate() != null ? m.getOrderMastFdate().format(DT_FMT) : "")
                .fuser(m.getOrderMastFuser())
                .ldate(m.getOrderMastLdate() != null ? m.getOrderMastLdate().format(DT_FMT) : "")
                .luser(m.getOrderMastLuser())
                .webMemberId(m.getWebMemberId())
                .webOrderStatus(m.getWebOrderStatus())
                .orderKey(m.getOrderKey())
                .items(items)
                .itemCount(items.size())
                .totalAmount(total)
                .stau(stau)
                .stauName(stauName)
                .build();
    }
}
