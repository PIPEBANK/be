package com.weborder.ordersystem.domain.web.delivery.controller;

import com.weborder.ordersystem.domain.web.delivery.service.DeliveryService;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.order.dto.OrderMastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDeliveryController {

    private final DeliveryService deliveryService;
    private final MemberRepository memberRepository;

    @GetMapping("/deliveries")
    public ResponseEntity<Map<String, Object>> getAssignedDeliveries(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("관리자 배송관리 목록 API - {} ~ {}, status: {}, page: {}", startDate, endDate, status, page);
        return ResponseEntity.ok(deliveryService.getAssignedOrdersPaged(startDate, endDate, status, page, size));
    }

    @GetMapping("/deliveries/{orderKey}")
    public ResponseEntity<OrderMastResponse> getDeliveryDetail(@PathVariable String orderKey) {
        log.info("관리자 배송 상세 API - key: {}", orderKey);
        return ResponseEntity.ok(deliveryService.getAdminDeliveryDetail(orderKey));
    }

    @PatchMapping("/orders/{orderKey}/confirm")
    public ResponseEntity<OrderMastResponse> confirmOrder(
            @PathVariable String orderKey,
            @RequestBody Map<String, Long> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = body.get("driverId");
        log.info("관리자 접수완료 API - key: {}, driverId: {}", orderKey, driverId);

        OrderMastResponse result = deliveryService.confirmOrder(orderKey, driverId, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/members/drivers")
    public ResponseEntity<List<Map<String, Object>>> getDriverList() {
        List<Member> drivers = deliveryService.getDriverList();
        List<Map<String, Object>> response = drivers.stream()
                .map(d -> Map.<String, Object>of(
                        "id", d.getId(),
                        "memberId", d.getMemberId(),
                        "memberName", d.getMemberName(),
                        "role", d.getRole().name()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/deliveries/{orderKey}/shipping")
    public ResponseEntity<OrderMastResponse> startDelivery(
            @PathVariable String orderKey,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long adminMemberId = getMemberId(userDetails);
        log.info("관리자 배송 출발 API - key: {}, admin: {}", orderKey, adminMemberId);
        return ResponseEntity.ok(deliveryService.adminStartDelivery(orderKey, adminMemberId));
    }

    @PatchMapping("/deliveries/{orderKey}/complete")
    public ResponseEntity<OrderMastResponse> completeDelivery(
            @PathVariable String orderKey,
            @RequestParam("photos") List<MultipartFile> photos,
            @RequestParam(value = "driverSign", required = false) String driverSign,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long adminMemberId = getMemberId(userDetails);
        log.info("관리자 배송 완료 API - key: {}, admin: {}, 사진 {}장", orderKey, adminMemberId, photos.size());
        return ResponseEntity.ok(deliveryService.adminCompleteDelivery(orderKey, adminMemberId, photos, driverSign));
    }

    private Long getMemberId(UserDetails userDetails) {
        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        return member.getId();
    }
}
