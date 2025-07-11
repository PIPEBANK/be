package com.pipebank.ordersystem.domain.web.temp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;

@Repository
public interface TempWebOrderMastRepository extends JpaRepository<TempWebOrderMast, TempWebOrderMast.TempWebOrderMastId> {
    
    // ACNO 자동 생성을 위한 메서드 - 해당 날짜, 소속, 업장의 최대 ACNO 조회
    @Query("SELECT COALESCE(MAX(t.orderMastAcno), 0) FROM TempWebOrderMast t " +
           "WHERE t.orderMastDate = :date AND t.orderMastSosok = :sosok AND t.orderMastUjcd = :ujcd")
    Integer findMaxAcnoByDateAndSosokAndUjcd(@Param("date") String date, 
                                           @Param("sosok") Integer sosok, 
                                           @Param("ujcd") String ujcd);
    
    /**
     * 거래처별 임시저장 주문 목록 조회 (페이징 + 필터링)
     * - send = false인 것만 조회
     * - 다양한 조건으로 필터링 가능
     */
    @Query("SELECT t FROM TempWebOrderMast t WHERE " +
           "t.send = false AND " +
           "t.orderMastCust = :custId AND " +
           "(:orderDate IS NULL OR t.orderMastDate = :orderDate) AND " +
           "(:startDate IS NULL OR t.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.orderMastDate <= :endDate) AND " +
           "(:orderNumber IS NULL OR CONCAT(t.orderMastDate, '-', t.orderMastAcno) LIKE %:orderNumber%) AND " +
           "(:userId IS NULL OR t.userId LIKE %:userId%) AND " +
           "(:comName IS NULL OR t.orderMastComname LIKE %:comName%) " +
           "ORDER BY t.orderMastDate DESC, t.orderMastAcno DESC")
    Page<TempWebOrderMast> findByCustomerWithFilters(
            @Param("custId") Integer custId,
            @Param("orderDate") String orderDate,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orderNumber") String orderNumber,
            @Param("userId") String userId,
            @Param("comName") String comName,
            Pageable pageable);
    
    /**
     * 거래처별 임시저장 주문 기본 조회 (send = false만)
     */
    @Query("SELECT t FROM TempWebOrderMast t WHERE " +
           "t.send = false AND " +
           "t.orderMastCust = :custId " +
           "ORDER BY t.orderMastDate DESC, t.orderMastAcno DESC")
    Page<TempWebOrderMast> findByOrderMastCustAndSendFalse(@Param("custId") Integer custId, Pageable pageable);
} 