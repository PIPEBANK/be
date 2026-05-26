package com.weborder.ordersystem.domain.admin.order.dto;

import com.weborder.ordersystem.domain.web.order.dto.OrderTranResponse;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class AdminOrderDetailResponse {
    private String orderDate;
    private Integer sosok;
    private String ujcd;
    private Integer acno;
    private Integer cust;
    private String custName;
    private String custCodeName;
    private String memberName;
    private Integer sawon;
    private String odate;
    private Integer project;
    private String remark;
    private String webOrderStatus;
    private String orderKey;
    private Long webMemberId;
    private String fdate;
    private String fuser;
    private String ldate;
    private String luser;
    private BigDecimal totalAmount;
    private List<OrderTranResponse> items;
    private String guestCompanyName;
    private String guestManagerName;
    private String guestContact;
    private String guestAddress;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static AdminOrderDetailResponse from(WebOrderMast m, String custName,
                                                  List<OrderTranResponse> items) {
        return from(m, custName, items, null, null, null, null);
    }

    public static AdminOrderDetailResponse from(WebOrderMast m, String custName,
                                                  List<OrderTranResponse> items,
                                                  String guestCompanyName, String guestManagerName,
                                                  String guestContact, String guestAddress) {
        BigDecimal total = items.stream()
                .map(OrderTranResponse::getTot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String displayName = (guestCompanyName != null && !guestCompanyName.isBlank())
                ? guestCompanyName : custName;

        return AdminOrderDetailResponse.builder()
                .orderDate(m.getOrderMastDate())
                .sosok(m.getOrderMastSosok())
                .ujcd(m.getOrderMastUjcd())
                .acno(m.getOrderMastAcno())
                .cust(m.getOrderMastCust())
                .custName(displayName)
                .custCodeName(displayName)
                .memberName(displayName)
                .sawon(m.getOrderMastSawon())
                .odate(m.getOrderMastOdate())
                .project(m.getOrderMastProject())
                .remark(m.getOrderMastRemark())
                .webOrderStatus(m.getWebOrderStatus())
                .orderKey(m.getOrderKey())
                .webMemberId(m.getWebMemberId())
                .fdate(m.getOrderMastFdate() != null ? m.getOrderMastFdate().format(DT_FMT) : "")
                .fuser(m.getOrderMastFuser())
                .ldate(m.getOrderMastLdate() != null ? m.getOrderMastLdate().format(DT_FMT) : "")
                .luser(m.getOrderMastLuser())
                .totalAmount(total)
                .items(items)
                .guestCompanyName(guestCompanyName)
                .guestManagerName(guestManagerName)
                .guestContact(guestContact)
                .guestAddress(guestAddress)
                .build();
    }
}
