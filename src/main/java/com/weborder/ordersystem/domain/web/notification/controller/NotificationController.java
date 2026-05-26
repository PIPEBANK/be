package com.weborder.ordersystem.domain.web.notification.controller;

import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.notification.dto.NotificationResponse;
import com.weborder.ordersystem.domain.web.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(notificationService.getMyNotifications(memberId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = getMemberId(userDetails);
        long count = notificationService.getUnreadCount(memberId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = getMemberId(userDetails);
        notificationService.markAsRead(id, memberId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = getMemberId(userDetails);
        notificationService.markAllAsRead(memberId);
        return ResponseEntity.ok().build();
    }

    private Long getMemberId(UserDetails userDetails) {
        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        return member.getId();
    }
}
