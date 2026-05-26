package com.weborder.ordersystem.domain.admin.ship.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShipCreateRequest {
    private String orderDate;    // 수주일자
    private Integer orderSosok;  // 수주 소속
    private String orderUjcd;    // 수주 업장
    private Integer orderAcno;   // 수주 전표번호
    private String shipDate;     // 출하일자 (없으면 오늘)
    private String naddr;        // 납품주소
    private String tdiv;         // 거래구분코드
    private String remark;       // 비고
}
