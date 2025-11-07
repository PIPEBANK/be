package com.pipebank.ordersystem.domain.erp.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.StockDate;

@Repository
public interface StockDateRepository extends JpaRepository<StockDate, StockDate.StockDateId> {
    
    // 특정 품목의 부서7 재고수량 조회
    @Query("SELECT s.stockDateCnt FROM StockDate s WHERE s.stockDateItem = :itemCode AND s.stockDateBuse = 7")
    Optional<BigDecimal> findStockQuantityByItemCodeAndBuse7(@Param("itemCode") Integer itemCode);
    
    // 특정 품목의 부서7 재고 전체 정보 조회
    Optional<StockDate> findByStockDateItemAndStockDateBuse(Integer stockDateItem, Integer stockDateBuse);
    
    // 여러 품목의 부서7 재고수량 일괄 조회
    @Query("SELECT s FROM StockDate s WHERE s.stockDateItem IN :itemCodes AND s.stockDateBuse = 7")
    List<StockDate> findStockByItemCodesAndBuse7(@Param("itemCodes") List<Integer> itemCodes);
    
    /**
     * 가용재고 조회 (기존 ERP ExpectJegoSearch 로직)
     * = (현재재고) + (주문수량 - 출하수량) + 제작예정수량 + 가공예정수량
     * 
     * @param sosok 소속
     * @param itemCode 품목코드
     * @return 가용재고 수량
     */
    @Query(value = 
        "SELECT COALESCE(SUM(EXPECT_JEGO_CNT), 0) AS EXPECT_JEGO_CNT " +
        "FROM ( " +
        "    SELECT " +
        "        (SUM(ITEM_ORDER_CNT) - SUM(ITEM_SHIP_CNT)) * -1 AS EXPECT_JEGO_CNT " +
        "    FROM ( " +
        "        SELECT " +
        "            SUM(ORDER_TRAN_CNT) AS ITEM_ORDER_CNT, " +
        "            0 AS ITEM_SHIP_CNT " +
        "        FROM sa_order_tran " +
        "        WHERE ORDER_TRAN_SOSOK = :sosok " +
        "          AND ORDER_TRAN_ITEM = :itemCode " +
        "          AND ORDER_TRAN_STAU NOT LIKE '401003%' " +
        "        UNION ALL " +
        "        SELECT " +
        "            0 AS ITEM_ORDER_CNT, " +
        "            SUM(st.SHIP_TRAN_CNT) AS ITEM_SHIP_CNT " +
        "        FROM sa_ship_order so " +
        "        INNER JOIN sa_ship_tran st " +
        "            ON  so.SHIP_ORDER_DATE = st.SHIP_TRAN_DATE " +
        "            AND so.SHIP_ORDER_SOSOK = st.SHIP_TRAN_SOSOK " +
        "            AND so.SHIP_ORDER_UJCD = st.SHIP_TRAN_UJCD " +
        "            AND so.SHIP_ORDER_ACNO = st.SHIP_TRAN_ACNO " +
        "            AND so.SHIP_ORDER_SEQ = st.SHIP_TRAN_SEQ " +
        "            AND st.SHIP_TRAN_STAU IN ('5380010002', '5380030001', '5380030002') " +
        "        INNER JOIN sa_order_tran ot " +
        "            ON so.SHIP_ORDER_SOSOK = ot.ORDER_TRAN_SOSOK " +
        "            AND so.SHIP_ORDER_UJCD = ot.ORDER_TRAN_UJCD " +
        "            AND so.SHIP_ORDER_ODATE = ot.ORDER_TRAN_DATE " +
        "            AND so.SHIP_ORDER_OACNO = ot.ORDER_TRAN_ACNO " +
        "            AND so.SHIP_ORDER_OSEQ = ot.ORDER_TRAN_SEQ " +
        "            AND ot.ORDER_TRAN_STAU NOT LIKE '401003%' " +
        "        WHERE st.SHIP_TRAN_SOSOK = :sosok " +
        "          AND st.SHIP_TRAN_ITEM = :itemCode " +
        "    ) AA " +
        "    UNION ALL " +
        "    SELECT MAKE_TRAN_CNT AS EXPECT_JEGO_CNT " +
        "    FROM sa_make_tran " +
        "    WHERE MAKE_TRAN_SOSOK = :sosok " +
        "      AND MAKE_TRAN_ITEM = :itemCode " +
        "      AND MAKE_TRAN_STAU <> '4150030001' " +
        "    UNION ALL " +
        "    SELECT PROC_TRAN_CNT AS EXPECT_JEGO_CNT " +
        "    FROM sa_proc_tran " +
        "    WHERE PROC_TRAN_SOSOK = :sosok " +
        "      AND PROC_TRAN_ITEM = :itemCode " +
        "      AND PROC_TRAN_STAU <> '4160030001' " +
        ") BB", 
        nativeQuery = true)
    BigDecimal findAvailableStockQuantity(@Param("sosok") Integer sosok, @Param("itemCode") Integer itemCode);
} 