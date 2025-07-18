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
     */
    @Query("""
        SELECT sm, st
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
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
        @Param("itemName") String itemName,
        @Param("comName") String comName,
        Pageable pageable
    );
} 