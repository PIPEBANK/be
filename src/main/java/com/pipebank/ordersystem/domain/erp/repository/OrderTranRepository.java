package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.OrderTran;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 주문 상세 Repository
 */
@Repository
public interface OrderTranRepository extends JpaRepository<OrderTran, OrderTran.OrderTranId> {

    /**
     * 복합키로 주문 상세 조회
     */
    Optional<OrderTran> findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoAndOrderTranSeq(
            String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno, Integer orderTranSeq);

    /**
     * 특정 주문의 모든 상세 내역 조회 (SEQ 순 정렬)
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate = :date AND ot.orderTranSosok = :sosok AND ot.orderTranUjcd = :ujcd AND ot.orderTranAcno = :acno ORDER BY ot.orderTranSeq")
    List<OrderTran> findByOrderMastKey(@Param("date") String orderTranDate, 
                                      @Param("sosok") Integer orderTranSosok, 
                                      @Param("ujcd") String orderTranUjcd, 
                                      @Param("acno") Integer orderTranAcno);

    /**
     * 특정 주문의 모든 상세 내역 조회 (페이징)
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate = :date AND ot.orderTranSosok = :sosok AND ot.orderTranUjcd = :ujcd AND ot.orderTranAcno = :acno ORDER BY ot.orderTranSeq")
    Page<OrderTran> findByOrderMastKey(@Param("date") String orderTranDate, 
                                      @Param("sosok") Integer orderTranSosok, 
                                      @Param("ujcd") String orderTranUjcd, 
                                      @Param("acno") Integer orderTranAcno, 
                                      Pageable pageable);

    /**
     * 주문일자별 상세 내역 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate = :date ORDER BY ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranDateOrderByKeys(@Param("date") String orderTranDate);

    /**
     * 주문일자 범위별 상세 내역 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate BETWEEN :startDate AND :endDate ORDER BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranDateBetweenOrderByKeys(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 주문일자 범위별 상세 내역 조회 (페이징)
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate BETWEEN :startDate AND :endDate ORDER BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    Page<OrderTran> findByOrderTranDateBetweenOrderByKeys(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    /**
     * 품목코드별 주문 상세 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranItem = :itemCode ORDER BY ot.orderTranDate DESC, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranItemOrderByDateDesc(@Param("itemCode") Integer orderTranItem);

    /**
     * 품목명으로 검색
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDeta LIKE %:itemName% ORDER BY ot.orderTranDate DESC, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranDetaContainingOrderByDateDesc(@Param("itemName") String orderTranDeta);

    /**
     * 상태코드별 주문 상세 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranStau = :status ORDER BY ot.orderTranDate DESC, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranStauOrderByDateDesc(@Param("status") String orderTranStau);

    /**
     * 소속별 주문 상세 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranSosok = :sosok ORDER BY ot.orderTranDate DESC, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranSosokOrderByDateDesc(@Param("sosok") Integer orderTranSosok);

    /**
     * 업장코드별 주문 상세 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranUjcd = :ujcd ORDER BY ot.orderTranDate DESC, ot.orderTranSosok, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findByOrderTranUjcdOrderByDateDesc(@Param("ujcd") String orderTranUjcd);

    /**
     * 최신 주문 상세 내역 조회
     */
    @Query("SELECT ot FROM OrderTran ot ORDER BY ot.orderTranDate DESC, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<OrderTran> findLatestOrderTrans(Pageable pageable);

    /**
     * 복합 조건으로 주문 상세 검색
     */
    @Query("SELECT ot FROM OrderTran ot WHERE " +
           "(:startDate IS NULL OR ot.orderTranDate >= :startDate) AND " +
           "(:endDate IS NULL OR ot.orderTranDate <= :endDate) AND " +
           "(:sosok IS NULL OR ot.orderTranSosok = :sosok) AND " +
           "(:ujcd IS NULL OR ot.orderTranUjcd = :ujcd) AND " +
           "(:itemCode IS NULL OR ot.orderTranItem = :itemCode) AND " +
           "(:itemName IS NULL OR ot.orderTranDeta LIKE %:itemName%) AND " +
           "(:status IS NULL OR ot.orderTranStau = :status) " +
           "ORDER BY ot.orderTranDate DESC, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    Page<OrderTran> searchOrderTrans(@Param("startDate") String startDate, 
                                    @Param("endDate") String endDate,
                                    @Param("sosok") Integer sosok, 
                                    @Param("ujcd") String ujcd,
                                    @Param("itemCode") Integer itemCode, 
                                    @Param("itemName") String itemName,
                                    @Param("status") String status, 
                                    Pageable pageable);

    /**
     * 특정 주문의 상세 내역 개수 조회
     */
    @Query("SELECT COUNT(ot) FROM OrderTran ot WHERE ot.orderTranDate = :date AND ot.orderTranSosok = :sosok AND ot.orderTranUjcd = :ujcd AND ot.orderTranAcno = :acno")
    long countByOrderMastKey(@Param("date") String orderTranDate, 
                            @Param("sosok") Integer orderTranSosok, 
                            @Param("ujcd") String orderTranUjcd, 
                            @Param("acno") Integer orderTranAcno);

    /**
     * 주문일자별 상세 내역 개수 조회
     */
    long countByOrderTranDate(String orderTranDate);

    /**
     * 주문일자 범위별 상세 내역 개수 조회
     */
    long countByOrderTranDateBetween(String startDate, String endDate);

    /**
     * 품목코드별 주문 내역 존재 여부 확인
     */
    boolean existsByOrderTranItem(Integer orderTranItem);

    /**
     * 주문번호(DATE-ACNO)로 주문 상세 내역 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate = :date AND ot.orderTranAcno = :acno ORDER BY ot.orderTranSosok ASC, ot.orderTranUjcd ASC, ot.orderTranSeq ASC")
    List<OrderTran> findByOrderTranDateAndOrderTranAcnoOrderByOrderTranSosokAscOrderTranUjcdAscOrderTranSeqAsc(@Param("date") String orderTranDate, @Param("acno") Integer orderTranAcno);

    /**
     * 여러 주문의 상태 코드를 배치로 조회 (성능 최적화)
     * 각 주문의 모든 OrderTran 상태를 조회하여 주문별 상태를 계산할 수 있도록 함
     */
    @Query("SELECT ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranStau " +
           "FROM OrderTran ot " +
           "WHERE (ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno) IN :orderKeys " +
           "ORDER BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<Object[]> findStatusByOrderKeys(@Param("orderKeys") List<Object[]> orderKeys);

    /**
     * 거래처별 주문들의 상태 코드를 배치로 조회
     */
    @Query("SELECT ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranStau " +
           "FROM OrderTran ot " +
           "JOIN OrderMast om ON om.orderMastDate = ot.orderTranDate " +
           "    AND om.orderMastSosok = ot.orderTranSosok " +
           "    AND om.orderMastUjcd = ot.orderTranUjcd " +
           "    AND om.orderMastAcno = ot.orderTranAcno " +
           "WHERE om.orderMastCust = :custId " +
           "ORDER BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranSeq")
    List<Object[]> findStatusByCustomer(@Param("custId") Integer custId);

    /**
     * 특정 주문들의 상태 분포를 조회 (각 주문의 상태별 개수)
     */
    @Query("SELECT ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranStau, COUNT(*) " +
           "FROM OrderTran ot " +
           "WHERE (ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno) IN :orderKeys " +
           "GROUP BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranStau " +
           "ORDER BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno")
    List<Object[]> findStatusDistributionByOrderKeys(@Param("orderKeys") List<Object[]> orderKeys);

    /**
     * 거래처별 주문들의 상태 분포를 조회 (더 간단한 방식)
     */
    @Query("SELECT ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranStau, COUNT(*) " +
           "FROM OrderTran ot " +
           "JOIN OrderMast om ON om.orderMastDate = ot.orderTranDate " +
           "    AND om.orderMastSosok = ot.orderTranSosok " +
           "    AND om.orderMastUjcd = ot.orderTranUjcd " +
           "    AND om.orderMastAcno = ot.orderTranAcno " +
           "WHERE om.orderMastCust = :custId " +
           "GROUP BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno, ot.orderTranStau " +
           "ORDER BY ot.orderTranDate, ot.orderTranSosok, ot.orderTranUjcd, ot.orderTranAcno")
    List<Object[]> findStatusDistributionByCustomer(@Param("custId") Integer custId);

    /**
     * 특정 주문 키와 순번으로 OrderTran 조회
     */
    @Query("SELECT ot FROM OrderTran ot WHERE ot.orderTranDate = :date AND ot.orderTranSosok = :sosok AND ot.orderTranUjcd = :ujcd AND ot.orderTranAcno = :acno AND ot.orderTranSeq = :seq")
    List<OrderTran> findByOrderMastKeyAndSeq(@Param("date") String orderTranDate, 
                                            @Param("sosok") Integer orderTranSosok, 
                                            @Param("ujcd") String orderTranUjcd, 
                                            @Param("acno") Integer orderTranAcno,
                                            @Param("seq") Integer orderTranSeq);
} 