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
} 