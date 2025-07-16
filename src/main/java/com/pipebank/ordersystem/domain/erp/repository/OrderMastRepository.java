package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.OrderMast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderMastRepository extends JpaRepository<OrderMast, OrderMast.OrderMastId> {

    // 복합키로 단일 조회
    Optional<OrderMast> findByOrderMastDateAndOrderMastSosokAndOrderMastUjcdAndOrderMastAcno(
            String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno);

    // 주문번호 조회용 (DATE + ACNO 조합)
    List<OrderMast> findByOrderMastDateAndOrderMastAcno(String orderMastDate, Integer orderMastAcno);

    // ACNO별 조회
    List<OrderMast> findByOrderMastAcnoOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAsc(Integer orderMastAcno);

    // 주문일자별 조회
    List<OrderMast> findByOrderMastDateOrderByOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(String orderMastDate);

    // 주문일자 범위 조회
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastDate BETWEEN :startDate AND :endDate " +
           "ORDER BY o.orderMastDate ASC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    List<OrderMast> findByOrderMastDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // 주문일자 범위 조회 (페이징)
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastDate BETWEEN :startDate AND :endDate " +
           "ORDER BY o.orderMastDate ASC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    Page<OrderMast> findByOrderMastDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // 거래처별 주문 조회
    List<OrderMast> findByOrderMastCustOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(Integer orderMastCust);

    // 거래처별 주문 조회 (페이징)
    Page<OrderMast> findByOrderMastCustOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(Integer orderMastCust, Pageable pageable);

    // 담당자별 주문 조회
    List<OrderMast> findByOrderMastSawonOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(Integer orderMastSawon);

    // 담당자별 주문 조회 (페이징)
    Page<OrderMast> findByOrderMastSawonOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(Integer orderMastSawon, Pageable pageable);

    // 소속별 주문 조회
    List<OrderMast> findByOrderMastSosokOrderByOrderMastDateDescOrderMastUjcdAscOrderMastAcnoAsc(Integer orderMastSosok);

    // 업장코드별 주문 조회
    List<OrderMast> findByOrderMastUjcdOrderByOrderMastDateDescOrderMastSosokAscOrderMastAcnoAsc(String orderMastUjcd);

    // 프로젝트별 주문 조회
    List<OrderMast> findByOrderMastProjectOrderByOrderMastDateDescOrderMastSosokAscOrderMastUjcdAscOrderMastAcnoAsc(Integer orderMastProject);

    // 회사명으로 검색
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastComname LIKE %:companyName% " +
           "ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    List<OrderMast> findByOrderMastComnameContaining(@Param("companyName") String companyName);

    // 복합 조건 검색
    @Query("SELECT o FROM OrderMast o WHERE " +
           "(:startDate IS NULL OR o.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderMastDate <= :endDate) AND " +
           "(:orderMastCust IS NULL OR o.orderMastCust = :orderMastCust) AND " +
           "(:orderMastSawon IS NULL OR o.orderMastSawon = :orderMastSawon) AND " +
           "(:orderMastSosok IS NULL OR o.orderMastSosok = :orderMastSosok) AND " +
           "(:orderMastUjcd IS NULL OR o.orderMastUjcd = :orderMastUjcd) AND " +
           "(:orderMastProject IS NULL OR o.orderMastProject = :orderMastProject) AND " +
           "(:companyName IS NULL OR o.orderMastComname LIKE %:companyName%) " +
           "ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    Page<OrderMast> findOrdersWithConditions(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orderMastCust") Integer orderMastCust,
            @Param("orderMastSawon") Integer orderMastSawon,
            @Param("orderMastSosok") Integer orderMastSosok,
            @Param("orderMastUjcd") String orderMastUjcd,
            @Param("orderMastProject") Integer orderMastProject,
            @Param("companyName") String companyName,
            Pageable pageable);

    // 통계 쿼리들
    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastDate = :orderDate")
    long countByOrderMastDate(@Param("orderDate") String orderDate);

    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastDate BETWEEN :startDate AND :endDate")
    long countByOrderMastDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastCust = :custId")
    long countByOrderMastCust(@Param("custId") Integer custId);

    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastSawon = :sawonId")
    long countByOrderMastSawon(@Param("sawonId") Integer sawonId);

    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastSosok = :sosokId")
    long countByOrderMastSosok(@Param("sosokId") Integer sosokId);

    @Query("SELECT COUNT(o) FROM OrderMast o WHERE o.orderMastProject = :projectId")
    long countByOrderMastProject(@Param("projectId") Integer projectId);

    // 최신 주문 조회
    @Query("SELECT o FROM OrderMast o ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    List<OrderMast> findLatestOrders(Pageable pageable);

    // 특정 거래처의 최신 주문 조회
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastCust = :custId " +
           "ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    List<OrderMast> findLatestOrdersByCustomer(@Param("custId") Integer custId, Pageable pageable);

    // 특정 담당자의 최신 주문 조회
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastSawon = :sawonId " +
           "ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    List<OrderMast> findLatestOrdersBySawon(@Param("sawonId") Integer sawonId, Pageable pageable);

    // 거래처별 주문 조회 (필터링)
    @Query("SELECT o FROM OrderMast o WHERE o.orderMastCust = :custId AND " +
           "(:orderDate IS NULL OR o.orderMastDate = :orderDate) AND " +
           "(:startDate IS NULL OR o.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderMastDate <= :endDate) AND " +
           "(:orderNumber IS NULL OR CONCAT(o.orderMastDate, '-', o.orderMastAcno) LIKE %:orderNumber%) AND " +
           "(:sdiv IS NULL OR o.orderMastSdiv = :sdiv) AND " +
           "(:comName IS NULL OR o.orderMastComname LIKE %:comName%) " +
           "ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    Page<OrderMast> findByCustomerWithFilters(
            @Param("custId") Integer custId,
            @Param("orderDate") String orderDate,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orderNumber") String orderNumber,
            @Param("sdiv") String sdiv,
            @Param("comName") String comName,
            Pageable pageable);

    // 거래처별 주문 조회 (출하번호 검색 포함)
    @Query("SELECT DISTINCT o FROM OrderMast o LEFT JOIN ShipOrder so ON " +
           "o.orderMastDate = so.shipOrderOdate AND " +
           "o.orderMastSosok = so.shipOrderSosok AND " +
           "o.orderMastUjcd = so.shipOrderUjcd AND " +
           "o.orderMastAcno = so.shipOrderOacno " +
           "WHERE o.orderMastCust = :custId AND " +
           "(:orderDate IS NULL OR o.orderMastDate = :orderDate) AND " +
           "(:startDate IS NULL OR o.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderMastDate <= :endDate) AND " +
           "(:orderNumber IS NULL OR CONCAT(o.orderMastDate, '-', o.orderMastAcno) LIKE %:orderNumber%) AND " +
           "(:shipNumber IS NULL OR CONCAT(so.shipOrderDate, '-', so.shipOrderAcno) LIKE %:shipNumber%) AND " +
           "(:sdiv IS NULL OR o.orderMastSdiv = :sdiv) AND " +
           "(:comName IS NULL OR o.orderMastComname LIKE %:comName%) " +
           "ORDER BY o.orderMastDate DESC, o.orderMastSosok ASC, o.orderMastUjcd ASC, o.orderMastAcno ASC")
    Page<OrderMast> findByCustomerWithFiltersIncludingShipNumber(
            @Param("custId") Integer custId,
            @Param("orderDate") String orderDate,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orderNumber") String orderNumber,
            @Param("shipNumber") String shipNumber,
            @Param("sdiv") String sdiv,
            @Param("comName") String comName,
            Pageable pageable);

    // 거래처별 출하 조회 (ShipOrder 기준) - 모든 출하번호 표시
    @Query("SELECT o, so FROM OrderMast o INNER JOIN ShipOrder so ON " +
           "o.orderMastDate = so.shipOrderOdate AND " +
           "o.orderMastSosok = so.shipOrderSosok AND " +
           "o.orderMastUjcd = so.shipOrderUjcd AND " +
           "o.orderMastAcno = so.shipOrderOacno " +
           "WHERE o.orderMastCust = :custId AND " +
           "(:orderDate IS NULL OR o.orderMastDate = :orderDate) AND " +
           "(:startDate IS NULL OR o.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderMastDate <= :endDate) AND " +
           "(:orderNumber IS NULL OR CONCAT(o.orderMastDate, '-', o.orderMastAcno) LIKE %:orderNumber%) AND " +
           "(:shipNumber IS NULL OR CONCAT(so.shipOrderDate, '-', so.shipOrderAcno) LIKE %:shipNumber%) AND " +
           "(:sdiv IS NULL OR o.orderMastSdiv = :sdiv) AND " +
           "(:comName IS NULL OR o.orderMastComname LIKE %:comName%) " +
           "ORDER BY o.orderMastDate DESC, so.shipOrderDate DESC, so.shipOrderAcno DESC")
    Page<Object[]> findShipmentsByCustomerWithFilters(
            @Param("custId") Integer custId,
            @Param("orderDate") String orderDate,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orderNumber") String orderNumber,
            @Param("shipNumber") String shipNumber,
            @Param("sdiv") String sdiv,
            @Param("comName") String comName,
            Pageable pageable);

    // 거래처별 출하 조회 (ShipOrder 기준) - 중복 제거용 (페이징 없음)
    @Query("SELECT o, so FROM OrderMast o INNER JOIN ShipOrder so ON " +
           "o.orderMastDate = so.shipOrderOdate AND " +
           "o.orderMastSosok = so.shipOrderSosok AND " +
           "o.orderMastUjcd = so.shipOrderUjcd AND " +
           "o.orderMastAcno = so.shipOrderOacno " +
           "WHERE o.orderMastCust = :custId AND " +
           "(:orderDate IS NULL OR o.orderMastDate = :orderDate) AND " +
           "(:startDate IS NULL OR o.orderMastDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderMastDate <= :endDate) AND " +
           "(:orderNumber IS NULL OR CONCAT(o.orderMastDate, '-', o.orderMastAcno) LIKE %:orderNumber%) AND " +
           "(:shipNumber IS NULL OR CONCAT(so.shipOrderDate, '-', so.shipOrderAcno) LIKE %:shipNumber%) AND " +
           "(:sdiv IS NULL OR o.orderMastSdiv = :sdiv) AND " +
           "(:comName IS NULL OR o.orderMastComname LIKE %:comName%) " +
           "ORDER BY o.orderMastDate DESC, so.shipOrderDate DESC, so.shipOrderAcno DESC")
    List<Object[]> findShipmentsByCustomerWithFiltersForDeduplication(
            @Param("custId") Integer custId,
            @Param("orderDate") String orderDate,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orderNumber") String orderNumber,
            @Param("shipNumber") String shipNumber,
            @Param("sdiv") String sdiv,
            @Param("comName") String comName);

    // 🔥 ERP DB에서 ACNO 최댓값 조회 (웹에서 ACNO 생성용)
    @Query("SELECT COALESCE(MAX(o.orderMastAcno), 0) FROM OrderMast o WHERE " +
           "o.orderMastDate = :orderDate AND o.orderMastSosok = :sosok AND o.orderMastUjcd = :ujcd")
    Integer findMaxAcnoByDateAndSosokAndUjcd(@Param("orderDate") String orderDate, 
                                            @Param("sosok") Integer sosok, 
                                            @Param("ujcd") String ujcd);
} 