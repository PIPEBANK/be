package com.weborder.ordersystem.domain.web.delivery.controller;

import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderCreateRequest;
import com.weborder.ordersystem.domain.admin.order.dto.AdminOrderListResponse;
import com.weborder.ordersystem.domain.admin.order.service.AdminOrderService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
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
@RequestMapping("/api/driver/orders")
@RequiredArgsConstructor
public class DriverOrderController {

    private final AdminOrderService adminOrderService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<?> createDriverOrder(
            @Valid @RequestBody AdminOrderCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("배송기사 출고주문 생성 API - user: {}, type: {}",
                userDetails.getUsername(), request.getCustomerType());
        try {
            Long driverId = getMemberId(userDetails);
            OrderMastResponse response = adminOrderService.createDriverOrder(request, driverId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("배송기사 출고주문 생성 실패 (입력값 오류): {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("배송기사 출고주문 생성 실패", e);
            return ResponseEntity.internalServerError().body("주문 생성에 실패했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/cust/{custCode}")
    public ResponseEntity<List<AdminOrderListResponse>> getOrdersByCust(
            @PathVariable Integer custCode) {
        log.info("배송기사 거래처별 주문 조회 API - cust: {}", custCode);
        List<AdminOrderListResponse> orders = adminOrderService.getOrdersByCust(custCode);
        return ResponseEntity.ok(orders);
    }

    private Long getMemberId(UserDetails userDetails) {
        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        return member.getId();
    }
}
