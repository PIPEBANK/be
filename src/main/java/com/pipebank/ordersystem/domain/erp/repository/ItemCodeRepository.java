package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.ItemCode;

@Repository
public interface ItemCodeRepository extends JpaRepository<ItemCode, Integer> {
    
    // 품목 코드 범위 조회
    List<ItemCode> findByItemCodeCodeBetween(Integer startCode, Integer endCode);
    
    // ===== 페이징 메서드들 =====
    
    // 사용중인 품목 페이징 조회
    Page<ItemCode> findByItemCodeUse(Integer itemCodeUse, Pageable pageable);
    
    // ===== 계층형 품목 선택용 메서드들 =====
    
    // 정확한 분류 조합으로 주문가능한 품목 조회 (페이징)
    Page<ItemCode> findByItemCodeDiv1AndItemCodeDiv2AndItemCodeDiv3AndItemCodeDiv4AndItemCodeUseAndItemCodeOrder(
        String itemCodeDiv1, String itemCodeDiv2, String itemCodeDiv3, String itemCodeDiv4, 
        Integer itemCodeUse, Integer itemCodeOrder, Pageable pageable);
    
    // 제품명과 규격을 분리해서 검색
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND i.itemCodeOrder = 1 AND " +
           "(:itemName IS NULL OR :itemName = '' OR i.itemCodeHnam LIKE %:itemName%) AND " +
           "(:spec IS NULL OR :spec = '' OR i.itemCodeSpec LIKE %:spec%)")
    Page<ItemCode> searchByNameAndSpec(@Param("itemName") String itemName, 
                                      @Param("spec") String spec, 
                                      Pageable pageable);
} 