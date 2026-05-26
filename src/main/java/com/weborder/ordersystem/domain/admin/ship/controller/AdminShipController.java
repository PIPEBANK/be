package com.weborder.ordersystem.domain.admin.ship.controller;

import com.weborder.ordersystem.domain.admin.ship.dto.ShipCreateRequest;
import com.weborder.ordersystem.domain.admin.ship.dto.ShipDetailResponse;
import com.weborder.ordersystem.domain.admin.ship.service.AdminShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/ship")
@RequiredArgsConstructor
public class AdminShipController {

    private final AdminShipService adminShipService;

    /**
     * 출하 의뢰 생성 (수주 기반)
     * POST /api/admin/ship
     */
    @PostMapping
    public ResponseEntity<ShipDetailResponse> createShipment(
            @RequestBody ShipCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("출하 의뢰 생성 API - order: {}-{}-{}-{}, user: {}",
                request.getOrderDate(), request.getOrderSosok(),
                request.getOrderUjcd(), request.getOrderAcno(),
                userDetails.getUsername());

        ShipDetailResponse response = adminShipService.createShipment(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 출하 목록 조회
     * GET /api/admin/ship?startDate=20260512&endDate=20260512
     */
    @GetMapping
    public ResponseEntity<List<ShipDetailResponse>> getShipList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("출하 목록 조회 API - {} ~ {}", startDate, endDate);

        List<ShipDetailResponse> ships = adminShipService.getShipList(startDate, endDate);
        return ResponseEntity.ok(ships);
    }

    /**
     * 출하 상세 조회
     * GET /api/admin/ship/{date}/{sosok}/{ujcd}/{acno}
     */
    @GetMapping("/{date}/{sosok}/{ujcd}/{acno}")
    public ResponseEntity<ShipDetailResponse> getShipDetail(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno) {
        log.info("출하 상세 조회 API - {}-{}-{}-{}", date, sosok, ujcd, acno);

        ShipDetailResponse detail = adminShipService.getShipDetail(date, sosok, ujcd, acno);
        return ResponseEntity.ok(detail);
    }

    /**
     * 출하 완료 처리
     * PATCH /api/admin/ship/{date}/{sosok}/{ujcd}/{acno}/complete
     */
    @PatchMapping("/{date}/{sosok}/{ujcd}/{acno}/complete")
    public ResponseEntity<ShipDetailResponse> completeShipment(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("출하 완료 API - {}-{}-{}-{}", date, sosok, ujcd, acno);

        ShipDetailResponse response = adminShipService.completeShipment(
                date, sosok, ujcd, acno, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * 수주번호로 출하 조회
     * GET /api/admin/ship/by-order/{orderDate}/{sosok}/{ujcd}/{orderAcno}
     */
    @GetMapping("/by-order/{orderDate}/{sosok}/{ujcd}/{orderAcno}")
    public ResponseEntity<ShipDetailResponse> getShipByOrder(
            @PathVariable String orderDate,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer orderAcno) {
        log.info("수주번호로 출하 조회 API - order: {}-{}-{}-{}", orderDate, sosok, ujcd, orderAcno);

        ShipDetailResponse detail = adminShipService.getShipmentByOrder(orderDate, sosok, ujcd, orderAcno);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }
}
