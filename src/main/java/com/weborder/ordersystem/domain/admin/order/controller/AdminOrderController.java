package com.weborder.ordersystem.domain.admin.order.controller;

import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderCreateRequest;
import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderDetailResponse;
import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderListResponse;
import com.weborder.ordersystem.domain.admin.order.dto.OrderStatusUpdateRequest;
import com.weborder.ordersystem.domain.admin.order.service.AdminOrderService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.notification.service.NotificationService;
import com.weborder.ordersystem.domain.web.order.dto.OrderMastResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    /**
     * 관리자 출고주문 생성
     * POST /api/admin/orders
     */
    @PostMapping
    public ResponseEntity<?> createAdminOrder(
            @Valid @RequestBody AdminOrderCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("관리자 출고주문 생성 API - type: {}, directDelivery: {}, user: {}",
                request.getCustomerType(), request.getDirectDelivery(), userDetails.getUsername());
        try {
            OrderMastResponse response;
            if (Boolean.TRUE.equals(request.getDirectDelivery())) {
                response = adminOrderService.createDirectDeliveryOrder(request, userDetails.getUsername());
            } else {
                response = adminOrderService.createAdminOrder(request, userDetails.getUsername());
                // 일반 출고주문: 거래처 소속 회원에게 알림
                if (response.getCust() != null && response.getCust() > 0) {
                    try {
                        List<Member> custMembers = memberRepository.findByCustCodeAndUseYn(
                                String.valueOf(response.getCust()), true);
                        String custName = response.getCustCodeName() != null ? response.getCustCodeName() : "";
                        for (Member m : custMembers) {
                            notificationService.send(m.getId(), "ORDER_CREATED",
                                    "출고주문", custName + " 출고주문이 등록되었습니다.",
                                    response.getOrderKey());
                        }
                    } catch (Exception notiEx) {
                        log.warn("출고주문 생성 알림 발송 실패", notiEx);
                    }
                }
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("관리자 출고주문 생성 실패 (입력값 오류): {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("관리자 출고주문 생성 실패", e);
            return ResponseEntity.internalServerError().body("주문 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 전체 주문 목록 조회 (필터 지원)
     * GET /api/admin/orders?startDate=20260512&endDate=20260512&status=ORDERED&cust=1
     */
    @GetMapping
    public ResponseEntity<List<AdminOrderListResponse>> getOrderList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer cust) {
        log.info("관리자 주문 목록 API - {} ~ {}, status: {}, cust: {}", startDate, endDate, status, cust);

        List<AdminOrderListResponse> orders = adminOrderService.getOrderList(startDate, endDate, status, cust);
        return ResponseEntity.ok(orders);
    }

    /**
     * 주문 상세 조회
     * GET /api/admin/orders/{date}/{sosok}/{ujcd}/{acno}
     */
    @GetMapping("/{date}/{sosok}/{ujcd}/{acno}")
    public ResponseEntity<AdminOrderDetailResponse> getOrderDetail(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno) {
        log.info("관리자 주문 상세 API - {}-{}-{}-{}", date, sosok, ujcd, acno);

        AdminOrderDetailResponse detail = adminOrderService.getOrderDetail(date, sosok, ujcd, acno);
        return ResponseEntity.ok(detail);
    }

    /**
     * 주문 상태 변경 (Web DB + ERP DB 동시)
     * PATCH /api/admin/orders/{date}/{sosok}/{ujcd}/{acno}/status
     */
    @PatchMapping("/{date}/{sosok}/{ujcd}/{acno}/status")
    public ResponseEntity<AdminOrderDetailResponse> updateOrderStatus(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno,
            @RequestBody OrderStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("관리자 주문 상태 변경 API - {}-{}-{}-{}", date, sosok, ujcd, acno);

        AdminOrderDetailResponse detail = adminOrderService.updateOrderStatus(
                date, sosok, ujcd, acno, request, userDetails.getUsername());
        return ResponseEntity.ok(detail);
    }

    /**
     * 거래처별 주문 목록 조회
     * GET /api/admin/orders/cust/{custCode}
     */
    @GetMapping("/cust/{custCode}")
    public ResponseEntity<List<AdminOrderListResponse>> getOrdersByCust(
            @PathVariable Integer custCode) {
        log.info("거래처별 주문 조회 API - cust: {}", custCode);

        List<AdminOrderListResponse> orders = adminOrderService.getOrdersByCust(custCode);
        return ResponseEntity.ok(orders);
    }
}
