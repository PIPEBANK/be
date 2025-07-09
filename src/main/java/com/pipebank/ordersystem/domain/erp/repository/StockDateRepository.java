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
} 