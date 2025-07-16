package com.pipebank.ordersystem.domain.web.temp.repository;

import java.util.Optional;

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
    
    // 🔥 TempOrderId 자동 생성을 위한 메서드 - 전체 테이블의 최대 TempOrderId 조회
    @Query("SELECT COALESCE(MAX(t.tempOrderId), 0) FROM TempWebOrderMast t")
    Integer findMaxTempOrderId();
    
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
    
    /**
     * 주문번호(orderNumber)로 조회 - DATE와 ACNO로 검색
     * orderNumber 형식: "YYYYMMDD-숫자" (예: "20250710-1")
     */
    @Query("SELECT t FROM TempWebOrderMast t WHERE " +
           "t.orderMastDate = :orderDate AND t.orderMastAcno = :acno")
    Optional<TempWebOrderMast> findByOrderNumber(@Param("orderDate") String orderDate, 
                                               @Param("acno") Integer acno);

    /**
     * 🔥 주문번호로 조회 - 가장 최신 TempOrderId 기준 (tempOrderId가 가장 큰 것)
     * orderNumber 형식: "YYYYMMDD-숫자" (예: "20250710-1")
     */
    @Query("SELECT t FROM TempWebOrderMast t WHERE " +
           "t.orderMastDate = :orderDate AND t.orderMastAcno = :acno " +
           "ORDER BY t.tempOrderId DESC LIMIT 1")
    Optional<TempWebOrderMast> findLatestByOrderNumber(@Param("orderDate") String orderDate, 
                                                      @Param("acno") Integer acno);

    /**
     * 주문번호 + tempOrderId로 정확한 데이터 조회
     * orderNumber 형식: "YYYYMMDD-숫자" (예: "20250710-1")
     */
    @Query("SELECT t FROM TempWebOrderMast t WHERE " +
           "t.orderMastDate = :orderDate AND t.orderMastAcno = :acno AND t.tempOrderId = :tempOrderId")
    Optional<TempWebOrderMast> findByOrderNumberAndTempId(@Param("orderDate") String orderDate, 
                                                         @Param("acno") Integer acno,
                                                         @Param("tempOrderId") Integer tempOrderId);
} 