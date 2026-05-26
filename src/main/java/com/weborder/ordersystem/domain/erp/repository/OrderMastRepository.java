package com.weborder.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.erp.entity.OrderMast;

@Repository
public interface OrderMastRepository extends JpaRepository<OrderMast, OrderMast.OrderMastId> {

    // 복합키로 단일 조회
    Optional<OrderMast> findByOrderMastDateAndOrderMastSosokAndOrderMastUjcdAndOrderMastAcno(
            String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno);

    // 주문번호 조회용 (DATE + ACNO 조합)
    List<OrderMast> findByOrderMastDateAndOrderMastAcno(String orderMastDate, Integer orderMastAcno);

    // 주문일자 범위 조회
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastDate BETWEEN :startDate AND :endDate " +
           "ORDER BY o.orderMastDate DESC, o.orderMastAcno DESC")
    Page<OrderMast> findByOrderMastDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // 거래처별 주문 조회 (페이징)
    Page<OrderMast> findByOrderMastCustOrderByOrderMastDateDescOrderMastAcnoDesc(Integer orderMastCust, Pageable pageable);

    // 거래처별 기간 필터 주문 조회
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastCust = :custId AND " +
           "(:startDate IS NULL OR o.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderMastDate <= :endDate) " +
           "ORDER BY o.orderMastDate DESC, o.orderMastAcno DESC")
    Page<OrderMast> findByCustomerWithDateFilter(
            @Param("custId") Integer custId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            Pageable pageable);

    // ACNO 최댓값 조회 (주문번호 생성용)
    @Query("SELECT COALESCE(MAX(o.orderMastAcno), 0) FROM OrderMast o WHERE " +
           "o.orderMastDate = :orderDate AND o.orderMastSosok = :sosok AND o.orderMastUjcd = :ujcd")
    Integer findMaxAcnoByDateAndSosokAndUjcd(@Param("orderDate") String orderDate,
                                            @Param("sosok") Integer sosok,
                                            @Param("ujcd") String ujcd);

    // 통계
    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastDate = :orderDate")
    long countByOrderMastDate(@Param("orderDate") String orderDate);

    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastCust = :custId")
    long countByOrderMastCust(@Param("custId") Integer custId);
}
