package com.weborder.ordersystem.domain.web.order.repository;

import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebOrderMastRepository extends JpaRepository<WebOrderMast, WebOrderMast.WebOrderMastId> {

    // 웹 회원의 주문 목록 조회
    List<WebOrderMast> findByWebMemberIdOrderByOrderMastDateDescOrderMastAcnoDesc(Long webMemberId);

    // 날짜별 주문 조회
    List<WebOrderMast> findByOrderMastDateOrderByOrderMastAcnoDesc(String date);

    // 날짜 범위 주문 조회
    @Query("SELECT m FROM WebOrderMast m WHERE m.orderMastDate BETWEEN :startDate AND :endDate ORDER BY m.orderMastDate DESC, m.orderMastAcno DESC")
    List<WebOrderMast> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // 상태별 주문 조회
    List<WebOrderMast> findByWebOrderStatusOrderByOrderMastDateDescOrderMastAcnoDesc(String status);

    // 거래처별 주문 조회
    List<WebOrderMast> findByOrderMastCustOrderByOrderMastDateDescOrderMastAcnoDesc(Integer cust);

    // 거래처 + 날짜범위 주문 조회
    @Query("SELECT m FROM WebOrderMast m WHERE m.orderMastCust = :cust AND m.orderMastDate BETWEEN :startDate AND :endDate ORDER BY m.orderMastDate DESC, m.orderMastAcno DESC")
    List<WebOrderMast> findByCustAndDateRange(@Param("cust") Integer cust, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // 상태 + 날짜범위 조회
    @Query("SELECT m FROM WebOrderMast m WHERE m.webOrderStatus = :status AND m.orderMastDate BETWEEN :startDate AND :endDate ORDER BY m.orderMastDate DESC, m.orderMastAcno DESC")
    List<WebOrderMast> findByStatusAndDateRange(@Param("status") String status, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // 해당 날짜의 최대 ACNO 조회 (새 전표번호 생성용)
    @Query("SELECT COALESCE(MAX(m.orderMastAcno), 0) FROM WebOrderMast m WHERE m.orderMastDate = :date AND m.orderMastSosok = :sosok AND m.orderMastUjcd = :ujcd")
    Integer findMaxAcno(@Param("date") String date, @Param("sosok") Integer sosok, @Param("ujcd") String ujcd);

    // 배송기사 배정된 주문 조회
    List<WebOrderMast> findByWebDriverIdOrderByOrderMastDateDescOrderMastAcnoDesc(Long driverId);

    // 배송기사 + 날짜범위 조회
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId = :driverId AND m.orderMastDate BETWEEN :startDate AND :endDate ORDER BY m.orderMastDate DESC, m.orderMastAcno DESC")
    List<WebOrderMast> findByDriverAndDateRange(@Param("driverId") Long driverId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // 관리자: 배정된 건 전체 조회 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId IS NOT NULL ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findAssignedOrdersPaged(Pageable pageable);

    // 관리자: 배정된 건 + 상태 조회 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId IS NOT NULL AND m.webOrderStatus = :status ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findAssignedOrdersByStatusPaged(@Param("status") String status, Pageable pageable);

    // 관리자: 배정된 건 + 배송일 범위 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId IS NOT NULL AND m.orderMastOdate BETWEEN :startDate AND :endDate ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findAssignedOrdersByDateRangePaged(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // 관리자: 배정된 건 + 배송일 범위 + 상태 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId IS NOT NULL AND m.orderMastOdate BETWEEN :startDate AND :endDate AND m.webOrderStatus = :status ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findAssignedOrdersByDateRangeAndStatusPaged(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("status") String status, Pageable pageable);

    // 배송기사 전체 조회 (페이징) - 배송일(odate) 기준 정렬
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId = :driverId ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findByDriverIdPaged(@Param("driverId") Long driverId, Pageable pageable);

    // 배송기사 + 상태 전체 조회 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId = :driverId AND m.webOrderStatus = :status ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findByDriverIdAndStatusPaged(@Param("driverId") Long driverId, @Param("status") String status, Pageable pageable);

    // 배송기사 + 배송일 범위 조회 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId = :driverId AND m.orderMastOdate BETWEEN :startDate AND :endDate ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findByDriverAndDateRangePaged(@Param("driverId") Long driverId, @Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // 배송기사 + 배송일 범위 + 상태 조회 (페이징)
    @Query("SELECT m FROM WebOrderMast m WHERE m.webDriverId = :driverId AND m.orderMastOdate BETWEEN :startDate AND :endDate AND m.webOrderStatus = :status ORDER BY m.orderMastOdate DESC, m.orderMastAcno DESC")
    Page<WebOrderMast> findByDriverAndDateRangeAndStatusPaged(@Param("driverId") Long driverId, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("status") String status, Pageable pageable);
}
