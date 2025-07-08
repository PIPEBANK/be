package com.pipebank.ordersystem.domain.erp.repository;

import com.pipebank.ordersystem.domain.erp.entity.ShipTran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipTranRepository extends JpaRepository<ShipTran, ShipTran.ShipTranId> {

    /**
     * 출하번호별 ShipTran 상태값 조회 (상태 계산용)
     */
    @Query("""
        SELECT st.shipTranStau
        FROM ShipTran st
        WHERE st.shipTranDate = :shipDate
        AND st.shipTranSosok = :sosok
        AND st.shipTranUjcd = :ujcd
        AND st.shipTranAcno = :acno
        """)
    List<String> findShipTranStatusByShipKey(
        @Param("shipDate") String shipDate,
        @Param("sosok") Integer sosok,
        @Param("ujcd") String ujcd,
        @Param("acno") Integer acno
    );

    /**
     * 출하번호별 ShipTran 전체 조회 (출고현황용)
     */
    @Query("""
        SELECT st
        FROM ShipTran st
        WHERE st.shipTranDate = :shipDate
        AND st.shipTranSosok = :sosok
        AND st.shipTranUjcd = :ujcd
        AND st.shipTranAcno = :acno
        ORDER BY st.shipTranSeq ASC
        """)
    List<ShipTran> findByShipKey(
        @Param("shipDate") String shipDate,
        @Param("sosok") Integer sosok,
        @Param("ujcd") String ujcd,
        @Param("acno") Integer acno
    );

    /**
     * 전표번호별 ShipTran 조회 (출고전표현황용)
     */
    @Query("""
        SELECT st
        FROM ShipTran st
        WHERE st.shipTranDate = :slipDate
        AND st.shipTranAcno = :slipAcno
        ORDER BY st.shipTranSeq ASC
        """)
    List<ShipTran> findBySlipNumber(
        @Param("slipDate") String slipDate,
        @Param("slipAcno") Integer slipAcno
    );
} 