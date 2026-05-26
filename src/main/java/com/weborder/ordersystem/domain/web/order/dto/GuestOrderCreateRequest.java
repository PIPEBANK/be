package com.weborder.ordersystem.domain.web.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GuestOrderCreateRequest {
    @NotBlank(message = "업체명은 필수입니다.")
    @Size(max = 100, message = "업체명은 100자 이하로 입력해주세요.")
    private String companyName;

    @NotBlank(message = "담당자명은 필수입니다.")
    @Size(max = 50, message = "담당자명은 50자 이하로 입력해주세요.")
    private String managerName;

    @NotBlank(message = "연락처는 필수입니다.")
    @Size(max = 20, message = "연락처는 20자 이하로 입력해주세요.")
    private String contact;

    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 200, message = "주소는 200자 이하로 입력해주세요.")
    private String address;

    private String odate;
    private String remark;

    @NotEmpty(message = "주문 품목은 1개 이상 필요합니다.")
    @Valid
    private List<OrderItemRequest> items;
}
