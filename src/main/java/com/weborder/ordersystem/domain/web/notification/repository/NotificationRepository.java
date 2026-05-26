package com.weborder.ordersystem.domain.web.notification.repository;

import com.weborder.ordersystem.domain.web.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByMemberIdAndIsReadFalse(Long memberId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.memberId = :memberId AND n.isRead = false")
    int markAllAsRead(@Param("memberId") Long memberId);
}
