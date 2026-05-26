package com.weborder.ordersystem.domain.web.order.controller;

import com.weborder.ordersystem.domain.web.order.dto.GuestOrderCreateRequest;
import com.weborder.ordersystem.domain.web.order.dto.OrderMastResponse;
import com.weborder.ordersystem.domain.web.order.service.GuestOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestOrderController {

    private final GuestOrderService guestOrderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderMastResponse> createGuestOrder(@Valid @RequestBody GuestOrderCreateRequest request) {
        log.info("비회원 주문 생성 API - 업체: {}, 담당자: {}", request.getCompanyName(), request.getManagerName());
        OrderMastResponse response = guestOrderService.createGuestOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders/lookup")
    public ResponseEntity<?> lookupGuestOrders(
            @RequestParam String companyName,
            @RequestParam String managerName,
            @RequestParam String contact) {
        if (companyName.isBlank() || managerName.isBlank() || contact.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "업체명, 담당자명, 연락처 모두 입력해주세요."));
        }
        log.info("비회원 주문 조회 API - 업체: {}, 담당자: {}, 연락처: {}", companyName, managerName, contact);
        try {
            List<OrderMastResponse> response = guestOrderService.lookupGuestOrders(companyName, managerName, contact);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("비회원 업체정보 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류: " + e.getMessage()));
        }
    }

    @GetMapping("/orders/{orderKey}")
    public ResponseEntity<?> getGuestOrder(@PathVariable String orderKey) {
        log.info("비회원 주문 조회 API - orderKey: {}", orderKey);
        try {
            OrderMastResponse response = guestOrderService.getGuestOrderByKey(orderKey);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("비회원 주문 조회 실패 - orderKey: {}, 사유: {}", orderKey, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("비회원 주문 조회 오류 - orderKey: {}", orderKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류: " + e.getMessage()));
        }
    }

    @PatchMapping("/orders/{orderKey}/cust-sign")
    public ResponseEntity<?> submitGuestCustSign(
            @PathVariable String orderKey,
            @RequestBody Map<String, String> body) {
        if (body == null || body.get("signData") == null || body.get("signData").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "서명 데이터가 필요합니다."));
        }
        String signData = body.get("signData");
        log.info("비회원 확인서명 API - orderKey: {}", orderKey);
        try {
            OrderMastResponse response = guestOrderService.submitGuestCustSign(orderKey, signData);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("비회원 확인서명 오류 - orderKey: {}", orderKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류: " + e.getMessage()));
        }
    }
}
