package com.weborder.ordersystem.domain.web.customitem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomItemRequest {

    @NotBlank(message = "품목명은 필수입니다")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
