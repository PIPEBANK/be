package com.weborder.ordersystem.domain.web.notification.service;

import com.weborder.ordersystem.domain.web.notification.dto.NotificationResponse;
import com.weborder.ordersystem.domain.web.notification.entity.Notification;
import com.weborder.ordersystem.domain.web.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void send(Long memberId, String type, String title, String message, String orderKey) {
        Notification n = Notification.builder()
                .memberId(memberId)
                .type(type)
                .title(title)
                .message(message)
                .orderKey(orderKey)
                .build();
        notificationRepository.save(n);
        log.info("알림 발송 - memberId: {}, type: {}, orderKey: {}", memberId, type, orderKey);
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<NotificationResponse> getMyNotifications(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public long getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndIsReadFalse(memberId);
    }

    @Transactional(transactionManager = "webTransactionManager")
    public void markAsRead(Long notificationId, Long memberId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다"));
        if (!n.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }
        n.markAsRead();
        notificationRepository.save(n);
    }

    @Transactional(transactionManager = "webTransactionManager")
    public void markAllAsRead(Long memberId) {
        notificationRepository.markAllAsRead(memberId);
    }
}
