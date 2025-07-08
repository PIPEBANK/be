package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.ShipOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipOrderRepository extends JpaRepository<ShipOrder, ShipOrder.ShipOrderId> {
    
    /**
     * 주문 정보로 매핑된 출하정보 조회
     */
    @Query("""
        SELECT so
        FROM ShipOrder so
        WHERE so.shipOrderOdate = :orderDate
        AND so.shipOrderSosok = :sosok
        AND so.shipOrderUjcd = :ujcd
        AND so.shipOrderOacno = :acno
        """)
    List<ShipOrder> findByOrderKey(
        @Param("orderDate") String orderDate,
        @Param("sosok") Integer sosok,
        @Param("ujcd") String ujcd,
        @Param("acno") Integer acno
    );

    /**
     * 출하 키와 순번으로 ShipOrder 조회
     */
    @Query("""
        SELECT so
        FROM ShipOrder so
        WHERE so.shipOrderDate = :shipDate
        AND so.shipOrderSosok = :sosok
        AND so.shipOrderUjcd = :ujcd
        AND so.shipOrderAcno = :acno
        AND so.shipOrderSeq = :seq
        """)
    List<ShipOrder> findByShipKeyAndSeq(
        @Param("shipDate") String shipDate,
        @Param("sosok") Integer sosok,
        @Param("ujcd") String ujcd,
        @Param("acno") Integer acno,
        @Param("seq") Integer seq
    );
} 