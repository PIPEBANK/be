package com.weborder.ordersystem.domain.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.erp.entity.ShipMast;

@Repository
public interface ShipMastRepository extends JpaRepository<ShipMast, ShipMast.ShipMastId> {

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

    @Query("SELECT COALESCE(MAX(sm.shipMastAcno), 0) FROM ShipMast sm WHERE sm.shipMastDate = :date AND sm.shipMastSosok = :sosok AND sm.shipMastUjcd = :ujcd")
    Integer findMaxAcno(@Param("date") String date, @Param("sosok") Integer sosok, @Param("ujcd") String ujcd);

    @Query("SELECT sm FROM ShipMast sm WHERE sm.shipMastDate BETWEEN :startDate AND :endDate ORDER BY sm.shipMastDate DESC, sm.shipMastAcno DESC")
    List<ShipMast> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<ShipMast> findByShipMastDateOrderByShipMastAcnoDesc(String date);

    List<ShipMast> findByShipMastCustOrderByShipMastDateDescShipMastAcnoDesc(Integer cust);
}
