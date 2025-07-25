package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.ShipMast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipMastRepository extends JpaRepository<ShipMast, ShipMast.ShipMastId> {

    /**
     * 거래처별 출하 마스터 조회 (페이징 + 필터링)
     * ShipOrder를 통해 OrderMast와 조인하여 출고형태 정보도 함께 조회
     */
    @Query("""
        SELECT sm, om
        FROM ShipMast sm
        JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
        JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR sm.shipMastDate = :shipDate)
        AND (:startDate IS NULL OR sm.shipMastDate >= :startDate)
        AND (:endDate IS NULL OR sm.shipMastDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:sdiv IS NULL OR om.orderMastSdiv = :sdiv)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY sm.shipMastDate DESC, sm.shipMastAcno DESC
        """)
    Page<Object[]> findShipMastWithOrderMastByCustomerWithFilters(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("sdiv") String sdiv,
        @Param("comName") String comName,
        Pageable pageable
    );

    /**
     * 출하일자와 출하번호로 ShipMast 조회
     */
    @Query("""
        SELECT sm
        FROM ShipMast sm
        WHERE sm.shipMastDate = :shipDate
        AND sm.shipMastAcno = :shipAcno
        """)
    List<ShipMast> findByShipMastDateAndShipMastAcno(
        @Param("shipDate") String shipDate,
        @Param("shipAcno") Integer shipAcno
    );

    /**
     * 거래처별 출고전표 목록 조회 (페이징 + 필터링)
     * ShipOrder를 통해 OrderMast와 조인하여 주문번호 정보도 함께 조회
     */
    @Query("""
        SELECT sm, om
        FROM ShipMast sm
        JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
        JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR sm.shipMastDate = :shipDate)
        AND (:startDate IS NULL OR sm.shipMastDate >= :startDate)
        AND (:endDate IS NULL OR sm.shipMastDate <= :endDate)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        GROUP BY sm.shipMastDate, sm.shipMastSosok, sm.shipMastUjcd, sm.shipMastAcno,
                 om.orderMastDate, om.orderMastSosok, om.orderMastUjcd, om.orderMastAcno
        ORDER BY sm.shipMastDate DESC, sm.shipMastAcno DESC
        """)
    List<Object[]> findShipSlipListByCustomerWithFiltersNative(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orderNumber") String orderNumber,
        @Param("shipNumber") String shipNumber,
        @Param("comName") String comName
    );

    /**
     * 거래처별 현장별 출하조회 (ShipTran 단위) - 페이징 + 필터링
     * ShipMast → ShipTran JOIN으로 모든 제품별 출하 정보 조회
     * ShipOrder → OrderMast JOIN으로 주문번호 필터링 지원
     */
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName IS NULL OR st.shipTranDeta LIKE %:itemName%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFilters(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName") String itemName,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 AND, 규격 AND
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%)
        AND (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%)
        AND (:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%)
        AND (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersAndAnd(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 OR, 규격 AND
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND ((:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%) OR 
             (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%))
        AND (:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%)
        AND (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersOrAnd(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 AND, 규격 OR
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%)
        AND (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%)
        AND ((:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%) OR 
             (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%))
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersAndOr(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 OR, 규격 OR
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND ((:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%) OR 
             (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%))
        AND ((:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%) OR 
             (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%))
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersOrOr(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 하위호환성을 위한 default 메서드
    default Page<Object[]> findShipmentItemsByCustomerWithFilters(
            Integer custId, String shipDate, String startDate, String endDate,
            String shipNumber, String orderNumber, String itemName1, String itemName2,
            String spec1, String spec2, String itemNameOperator, String specOperator,
            String comName, Pageable pageable) {
        
        boolean itemNameIsOr = "OR".equalsIgnoreCase(itemNameOperator);
        boolean specIsOr = "OR".equalsIgnoreCase(specOperator);

        if (!itemNameIsOr && !specIsOr) {
            // AND, AND
            return findShipmentItemsByCustomerWithFiltersAndAnd(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        } else if (itemNameIsOr && !specIsOr) {
            // OR, AND
            return findShipmentItemsByCustomerWithFiltersOrAnd(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        } else if (!itemNameIsOr && specIsOr) {
            // AND, OR
            return findShipmentItemsByCustomerWithFiltersAndOr(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        } else {
            // OR, OR
            return findShipmentItemsByCustomerWithFiltersOrOr(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        }
    }
} 