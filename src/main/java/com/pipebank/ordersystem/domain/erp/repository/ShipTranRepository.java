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
     * 출하번호별 특정 품목의 ShipTran 조회 (출하량 계산용)
     */
    @Query("""
        SELECT st
        FROM ShipTran st
        WHERE st.shipTranDate = :shipDate
        AND st.shipTranSosok = :sosok
        AND st.shipTranUjcd = :ujcd
        AND st.shipTranAcno = :acno
        AND st.shipTranItem = :itemCode
        ORDER BY st.shipTranSeq ASC
        """)
    List<ShipTran> findByShipKeyAndItem(
        @Param("shipDate") String shipDate,
        @Param("sosok") Integer sosok,
        @Param("ujcd") String ujcd,
        @Param("acno") Integer acno,
        @Param("itemCode") Integer itemCode
    );

    /**
     * 주문키로 ShipTran 조회 (주문상세에서 출하정보 조회용)
     * ShipOrder를 거치지 않고 직접 ShipTran에서 조회
     */
    @Query("""
        SELECT st
        FROM ShipTran st
        JOIN ShipOrder so ON st.shipTranDate = so.shipOrderDate 
                         AND st.shipTranSosok = so.shipOrderSosok
                         AND st.shipTranUjcd = so.shipOrderUjcd
                         AND st.shipTranAcno = so.shipOrderAcno
        WHERE so.shipOrderOdate = :orderDate
        AND so.shipOrderSosok = :sosok
        AND so.shipOrderUjcd = :ujcd
        AND so.shipOrderOacno = :acno
        AND st.shipTranItem = :itemCode
        ORDER BY st.shipTranDate ASC, st.shipTranAcno ASC, st.shipTranSeq ASC
        """)
    List<ShipTran> findByOrderKeyAndItem(
        @Param("orderDate") String orderDate,
        @Param("sosok") Integer sosok,
        @Param("ujcd") String ujcd,
        @Param("acno") Integer acno,
        @Param("itemCode") Integer itemCode
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