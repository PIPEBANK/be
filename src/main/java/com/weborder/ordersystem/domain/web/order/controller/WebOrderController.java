package com.weborder.ordersystem.domain.web.order.controller;

import com.weborder.ordersystem.domain.web.delivery.service.DeliveryService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.order.dto.*;
import com.weborder.ordersystem.domain.web.order.service.WebOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class WebOrderController {

    private final WebOrderService webOrderService;
    private final DeliveryService deliveryService;
    private final MemberRepository memberRepository;

    /**
     * 주문 생성 (장바구니 or 바로주문)
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<OrderMastResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest request) {
        log.info("주문 생성 API - user: {}, fromCart: {}", userDetails.getUsername(), request.isFromCart());

        OrderMastResponse response = webOrderService.createOrder(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 내 주문 목록
     * GET /api/orders/my?startDate=&endDate=&status=
     */
    @GetMapping("/my")
    public ResponseEntity<List<OrderMastResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status) {
        log.info("내 주문 조회 API - user: {}, {}-{}, status: {}", userDetails.getUsername(), startDate, endDate, status);

        List<OrderMastResponse> orders = webOrderService.getMyOrders(
                userDetails.getUsername(), startDate, endDate, status);
        return ResponseEntity.ok(orders);
    }

    /**
     * 주문 상세 조회
     * GET /api/orders/{date}/{sosok}/{ujcd}/{acno}
     */
    @GetMapping("/{date}/{sosok}/{ujcd}/{acno}")
    public ResponseEntity<OrderMastResponse> getOrderDetail(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("주문 상세 조회 API - {}-{}-{}-{}, user: {}", date, sosok, ujcd, acno, userDetails.getUsername());

        OrderMastResponse response = webOrderService.getOrderDetail(date, sosok, ujcd, acno);

        boolean isPrivileged = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_DRIVER"));
        if (!isPrivileged) {
            Member member = memberRepository.findByMemberId(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
            boolean isOwner = false;
            if (response.getWebMemberId() != null && response.getWebMemberId().equals(member.getId())) {
                isOwner = true;
            } else if (response.getCust() != null && member.getCustCode() != null) {
                try {
                    isOwner = response.getCust().equals(Integer.parseInt(member.getCustCode().trim()));
                } catch (NumberFormatException ignored) {}
            }
            if (!isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 상태 변경 (관리자용)
     * PATCH /api/orders/{date}/{sosok}/{ujcd}/{acno}/status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{date}/{sosok}/{ujcd}/{acno}/status")
    public ResponseEntity<OrderMastResponse> updateOrderStatus(
            @PathVariable String date,
            @PathVariable Integer sosok,
            @PathVariable String ujcd,
            @PathVariable Integer acno,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        log.info("주문 상태 변경 API - {}-{}-{}-{} -> {}", date, sosok, ujcd, acno, status);

        OrderMastResponse response = webOrderService.updateOrderStatus(date, sosok, ujcd, acno, status);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 주문 목록 (관리자용)
     * GET /api/orders?startDate=20260512&endDate=20260512
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderMastResponse>> getAllOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status) {
        log.info("전체 주문 조회 API - {} ~ {}, status: {}", startDate, endDate, status);

        List<OrderMastResponse> orders = webOrderService.getAllOrders(startDate, endDate, status);
        return ResponseEntity.ok(orders);
    }

    /**
     * 상태별 주문 목록 (관리자용)
     * GET /api/orders/status/{status}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderMastResponse>> getOrdersByStatus(
            @PathVariable String status) {
        log.info("상태별 주문 조회 API - status: {}", status);

        List<OrderMastResponse> orders = webOrderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * 거래처별 발주 집계
     * GET /api/orders/stats/by-cust?startDate=&endDate=
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/by-cust")
    public ResponseEntity<List<OrderStatsByCustResponse>> getStatsByCust(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("거래처별 발주 집계 API - {} ~ {}", startDate, endDate);
        return ResponseEntity.ok(webOrderService.getStatsByCust(startDate, endDate));
    }

    /**
     * 거래처별 상세 (품목별 그룹핑)
     * GET /api/orders/stats/by-cust/{memberId}?startDate=&endDate=
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/by-cust/{memberId}")
    public ResponseEntity<List<OrderStatsByItemResponse>> getStatsByCustDetail(
            @PathVariable Long memberId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("거래처별 상세 API - memberId: {}, {} ~ {}", memberId, startDate, endDate);
        return ResponseEntity.ok(webOrderService.getStatsByCustDetail(memberId, startDate, endDate));
    }

    /**
     * 품목별 발주 집계
     * GET /api/orders/stats/by-item?startDate=&endDate=&marketOnly=false
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/by-item")
    public ResponseEntity<List<OrderStatsByItemResponse>> getStatsByItem(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "false") boolean marketOnly) {
        log.info("품목별 발주 집계 API - {} ~ {}, marketOnly: {}", startDate, endDate, marketOnly);
        return ResponseEntity.ok(webOrderService.getStatsByItem(startDate, endDate, marketOnly));
    }

    @PatchMapping("/{orderKey}/cust-sign")
    public ResponseEntity<OrderMastResponse> submitCustSign(
            @PathVariable String orderKey,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        String signData = body.get("signData");
        log.info("거래처 확인서명 API - key: {}, member: {}", orderKey, member.getId());

        return ResponseEntity.ok(deliveryService.submitCustSign(orderKey, member.getId(), signData));
    }
}
