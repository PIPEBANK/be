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

@Slf4j
@RestController
@RequestMapping("/api/driver/deliveries")
@RequiredArgsConstructor
public class DriverDeliveryController {

    private final DeliveryService deliveryService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyDeliveries(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = getMemberId(userDetails);
        log.info("배송기사 목록 API - driverId: {}, {} ~ {}, page: {}", driverId, startDate, endDate, page);

        return ResponseEntity.ok(deliveryService.getMyDeliveriesPaged(driverId, startDate, endDate, status, page, size));
    }

    @GetMapping("/{orderKey}")
    public ResponseEntity<OrderMastResponse> getDeliveryDetail(
            @PathVariable String orderKey,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = getMemberId(userDetails);
        return ResponseEntity.ok(deliveryService.getDeliveryDetail(orderKey, driverId));
    }

    @PatchMapping("/{orderKey}/shipping")
    public ResponseEntity<OrderMastResponse> startDelivery(
            @PathVariable String orderKey,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = getMemberId(userDetails);
        log.info("배송 출발 API - key: {}, driver: {}", orderKey, driverId);

        return ResponseEntity.ok(deliveryService.startDelivery(orderKey, driverId));
    }

    @PatchMapping("/{orderKey}/complete")
    public ResponseEntity<OrderMastResponse> completeDelivery(
            @PathVariable String orderKey,
            @RequestParam("photos") List<MultipartFile> photos,
            @RequestParam(value = "driverSign", required = false) String driverSign,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = getMemberId(userDetails);
        log.info("배송 완료 API - key: {}, driver: {}, 사진 {}장, 서명: {}", orderKey, driverId, photos.size(), driverSign != null);

        return ResponseEntity.ok(deliveryService.completeDelivery(orderKey, driverId, photos, driverSign));
    }

    private Long getMemberId(UserDetails userDetails) {
        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        return member.getId();
    }
}
