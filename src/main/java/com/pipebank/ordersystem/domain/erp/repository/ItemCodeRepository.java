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
    
    // ===== 기본 조회 메서드들 (호환성 유지) =====
    
    // 품목번호로 검색
    Optional<ItemCode> findByItemCodeNum(String itemCodeNum);
    
    // 품목명으로 검색 (한글)
    List<ItemCode> findByItemCodeHnamContaining(String itemCodeHnam);
    
    // 품목명으로 검색 (영문)
    List<ItemCode> findByItemCodeEnamContaining(String itemCodeEnam);
    
    // 사용중인 품목만 조회
    List<ItemCode> findByItemCodeUse(Integer itemCodeUse);
    
    // 브랜드별 품목 조회
    List<ItemCode> findByItemCodeBrand(String itemCodeBrand);
    
    // 제품종류별 품목 조회
    List<ItemCode> findByItemCodeDiv1(String itemCodeDiv1);
    
    // 제품군별 품목 조회
    List<ItemCode> findByItemCodeDiv2(String itemCodeDiv2);
    
    // 제품용도별 품목 조회
    List<ItemCode> findByItemCodeDiv3(String itemCodeDiv3);
    
    // 제품기능별 품목 조회
    List<ItemCode> findByItemCodeDiv4(String itemCodeDiv4);
    
    // 매입처별 품목 조회
    List<ItemCode> findByItemCodePcust(Integer itemCodePcust);
    
    // 매출처별 품목 조회
    List<ItemCode> findByItemCodeScust(Integer itemCodeScust);
    
    // 품목 통합 검색 (품목번호, 품목명, 규격으로 검색)
    @Query("SELECT i FROM ItemCode i WHERE " +
           "(:keyword IS NULL OR " +
           "i.itemCodeNum LIKE %:keyword% OR " +
           "i.itemCodeHnam LIKE %:keyword% OR " +
           "i.itemCodeEnam LIKE %:keyword% OR " +
           "i.itemCodeSpec LIKE %:keyword%)")
    List<ItemCode> searchByKeyword(@Param("keyword") String keyword);
    
    // 사용중인 품목 통합 검색
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND " +
           "(:keyword IS NULL OR " +
           "i.itemCodeNum LIKE %:keyword% OR " +
           "i.itemCodeHnam LIKE %:keyword% OR " +
           "i.itemCodeEnam LIKE %:keyword% OR " +
           "i.itemCodeSpec LIKE %:keyword%)")
    List<ItemCode> searchActiveByKeyword(@Param("keyword") String keyword);
    
    // 제품 분류별 품목 조회 (계층적 검색)
    @Query("SELECT i FROM ItemCode i WHERE " +
           "(:div1 IS NULL OR i.itemCodeDiv1 = :div1) AND " +
           "(:div2 IS NULL OR i.itemCodeDiv2 = :div2) AND " +
           "(:div3 IS NULL OR i.itemCodeDiv3 = :div3) AND " +
           "(:div4 IS NULL OR i.itemCodeDiv4 = :div4)")
    List<ItemCode> findByProductDivision(@Param("div1") String div1, 
                                        @Param("div2") String div2, 
                                        @Param("div3") String div3, 
                                        @Param("div4") String div4);
    
    // 오더센터 사용 품목 조회
    @Query("SELECT i FROM ItemCode i WHERE i.itemCodeOrder = 1")
    List<ItemCode> findOrderableItems();
    
    // 재고 관리 품목 조회
    @Query("SELECT i FROM ItemCode i WHERE i.itemCodeNstock = 0")
    List<ItemCode> findStockManagedItems();
    
    // 품목 코드 범위 조회
    List<ItemCode> findByItemCodeCodeBetween(Integer startCode, Integer endCode);
    
    // ===== 페이징 메서드들 =====
    
    // 전체 품목 페이징 조회 (JpaRepository에서 상속받음)
    // Page<ItemCode> findAll(Pageable pageable);
    
    // 사용중인 품목 페이징 조회
    Page<ItemCode> findByItemCodeUse(Integer itemCodeUse, Pageable pageable);
    
    // 키워드 검색 페이징
    @Query("SELECT i FROM ItemCode i WHERE " +
           "(:keyword IS NULL OR " +
           "i.itemCodeNum LIKE %:keyword% OR " +
           "i.itemCodeHnam LIKE %:keyword% OR " +
           "i.itemCodeEnam LIKE %:keyword% OR " +
           "i.itemCodeSpec LIKE %:keyword%)")
    Page<ItemCode> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 사용중인 품목 키워드 검색 페이징
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND " +
           "(:keyword IS NULL OR " +
           "i.itemCodeNum LIKE %:keyword% OR " +
           "i.itemCodeHnam LIKE %:keyword% OR " +
           "i.itemCodeEnam LIKE %:keyword% OR " +
           "i.itemCodeSpec LIKE %:keyword%)")
    Page<ItemCode> searchActiveByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 제품 분류별 품목 페이징 조회
    @Query("SELECT i FROM ItemCode i WHERE " +
           "(:div1 IS NULL OR i.itemCodeDiv1 = :div1) AND " +
           "(:div2 IS NULL OR i.itemCodeDiv2 = :div2) AND " +
           "(:div3 IS NULL OR i.itemCodeDiv3 = :div3) AND " +
           "(:div4 IS NULL OR i.itemCodeDiv4 = :div4)")
    Page<ItemCode> findByProductDivision(@Param("div1") String div1, 
                                        @Param("div2") String div2, 
                                        @Param("div3") String div3, 
                                        @Param("div4") String div4,
                                        Pageable pageable);
    
    // 브랜드별 품목 페이징 조회
    Page<ItemCode> findByItemCodeBrand(String itemCodeBrand, Pageable pageable);
    
    // 매입처별 품목 페이징 조회
    Page<ItemCode> findByItemCodePcust(Integer itemCodePcust, Pageable pageable);
    
    // 매출처별 품목 페이징 조회
    Page<ItemCode> findByItemCodeScust(Integer itemCodeScust, Pageable pageable);
    
    // 오더센터 사용 품목 페이징 조회
    @Query("SELECT i FROM ItemCode i WHERE i.itemCodeOrder = 1")
    Page<ItemCode> findOrderableItems(Pageable pageable);
    
    // 재고 관리 품목 페이징 조회
    @Query("SELECT i FROM ItemCode i WHERE i.itemCodeNstock = 0")
    Page<ItemCode> findStockManagedItems(Pageable pageable);
} 