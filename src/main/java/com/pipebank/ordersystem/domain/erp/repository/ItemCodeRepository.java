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
    
    // 제품명과 규격을 분리해서 검색 (2중 검색 지원) - AND/OR 연산자 지원
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND i.itemCodeOrder = 1 AND " +
           "(:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) AND " +
           "(:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%) AND " +
           "(:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) AND " +
           "(:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%)")
    Page<ItemCode> searchByNameAndSpecWithAndAnd(@Param("itemName1") String itemName1,
                                                 @Param("itemName2") String itemName2,
                                                 @Param("spec1") String spec1,
                                                 @Param("spec2") String spec2,
                                                 Pageable pageable);

    // 품목명 OR, 규격 AND
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND i.itemCodeOrder = 1 AND " +
           "((:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) OR " +
           " (:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%)) AND " +
           "(:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) AND " +
           "(:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%)")
    Page<ItemCode> searchByNameAndSpecWithOrAnd(@Param("itemName1") String itemName1,
                                                @Param("itemName2") String itemName2,
                                                @Param("spec1") String spec1,
                                                @Param("spec2") String spec2,
                                                Pageable pageable);

    // 품목명 AND, 규격 OR
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND i.itemCodeOrder = 1 AND " +
           "(:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) AND " +
           "(:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%) AND " +
           "((:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) OR " +
           " (:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%))")
    Page<ItemCode> searchByNameAndSpecWithAndOr(@Param("itemName1") String itemName1,
                                                @Param("itemName2") String itemName2,
                                                @Param("spec1") String spec1,
                                                @Param("spec2") String spec2,
                                                Pageable pageable);

    // 품목명 OR, 규격 OR
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND i.itemCodeOrder = 1 AND " +
           "((:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) OR " +
           " (:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%)) AND " +
           "((:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) OR " +
           " (:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%))")
    Page<ItemCode> searchByNameAndSpecWithOrOr(@Param("itemName1") String itemName1,
                                              @Param("itemName2") String itemName2,
                                              @Param("spec1") String spec1,
                                              @Param("spec2") String spec2,
                                              Pageable pageable);

    // 기존 메서드는 AND-AND 조건으로 유지 (하위호환)
    default Page<ItemCode> searchByNameAndSpec(String itemName1, String itemName2,
                                              String spec1, String spec2,
                                              Pageable pageable) {
        return searchByNameAndSpecWithAndAnd(itemName1, itemName2, spec1, spec2, pageable);
    }
} 