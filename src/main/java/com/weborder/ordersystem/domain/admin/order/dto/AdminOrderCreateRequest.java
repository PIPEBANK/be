package com.weborder.ordersystem.domain.admin.order.dto;

import com.weborder.ordersystem.domain.web.order.dto.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminOrderCreateRequest {

    @NotBlank(message = "거래처 유형은 필수입니다. (erp 또는 guest)")
    private String customerType;  // "erp" 또는 "guest"

    private Integer custCode;     // ERP 거래처코드 (customerType=erp)

    private String companyName;   // 비회원 업체명 (customerType=guest)
    private String managerName;   // 비회원 담당자명
    private String contact;       // 비회원 연락처
    private String address;       // 비회원 주소

    @NotBlank(message = "납기일자는 필수입니다.")
    private String odate;

    private String remark;

    @NotEmpty(message = "주문 품목은 1개 이상 필요합니다.")
    @Valid
    private List<OrderItemRequest> items;

    private Boolean directDelivery;
}
