package com.weborder.ordersystem.domain.erp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.erp.entity.ItemCode;

@Repository
public interface ItemCodeRepository extends JpaRepository<ItemCode, Integer> {
    
    List<ItemCode> findByItemCodeCodeBetween(Integer startCode, Integer endCode);
    
    Page<ItemCode> findByItemCodeUse(Integer itemCodeUse, Pageable pageable);
    
    Page<ItemCode> findByItemCodeDiv1AndItemCodeDiv2AndItemCodeDiv3AndItemCodeUse(
        String itemCodeDiv1, String itemCodeDiv2, String itemCodeDiv3,
        Integer itemCodeUse, Pageable pageable);
    
    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND " +
           "(:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) AND " +
           "(:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%) AND " +
           "(:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) AND " +
           "(:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%) AND " +
           "(:itemNum IS NULL OR :itemNum = '' OR i.itemCodeNum LIKE %:itemNum%)")
    Page<ItemCode> searchByNameAndSpecWithAndAnd(@Param("itemName1") String itemName1,
                                                 @Param("itemName2") String itemName2,
                                                 @Param("spec1") String spec1,
                                                 @Param("spec2") String spec2,
                                                 @Param("itemNum") String itemNum,
                                                 Pageable pageable);

    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND " +
           "((:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) OR " +
           " (:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%)) AND " +
           "(:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) AND " +
           "(:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%) AND " +
           "(:itemNum IS NULL OR :itemNum = '' OR i.itemCodeNum LIKE %:itemNum%)")
    Page<ItemCode> searchByNameAndSpecWithOrAnd(@Param("itemName1") String itemName1,
                                                @Param("itemName2") String itemName2,
                                                @Param("spec1") String spec1,
                                                @Param("spec2") String spec2,
                                                @Param("itemNum") String itemNum,
                                                Pageable pageable);

    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND " +
           "(:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) AND " +
           "(:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%) AND " +
           "((:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) OR " +
           " (:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%)) AND " +
           "(:itemNum IS NULL OR :itemNum = '' OR i.itemCodeNum LIKE %:itemNum%)")
    Page<ItemCode> searchByNameAndSpecWithAndOr(@Param("itemName1") String itemName1,
                                                @Param("itemName2") String itemName2,
                                                @Param("spec1") String spec1,
                                                @Param("spec2") String spec2,
                                                @Param("itemNum") String itemNum,
                                                Pageable pageable);

    @Query("SELECT i FROM ItemCode i WHERE " +
           "i.itemCodeUse = 1 AND " +
           "((:itemName1 IS NULL OR :itemName1 = '' OR i.itemCodeHnam LIKE %:itemName1%) OR " +
           " (:itemName2 IS NULL OR :itemName2 = '' OR i.itemCodeHnam LIKE %:itemName2%)) AND " +
           "((:spec1 IS NULL OR :spec1 = '' OR i.itemCodeSpec LIKE %:spec1%) OR " +
           " (:spec2 IS NULL OR :spec2 = '' OR i.itemCodeSpec LIKE %:spec2%)) AND " +
           "(:itemNum IS NULL OR :itemNum = '' OR i.itemCodeNum LIKE %:itemNum%)")
    Page<ItemCode> searchByNameAndSpecWithOrOr(@Param("itemName1") String itemName1,
                                              @Param("itemName2") String itemName2,
                                              @Param("spec1") String spec1,
                                              @Param("spec2") String spec2,
                                              @Param("itemNum") String itemNum,
                                              Pageable pageable);

    // 최대 품목코드 조회 (신규 등록용)
    @Query("SELECT COALESCE(MAX(i.itemCodeCode), 0) FROM ItemCode i")
    Integer findMaxItemCode();

    @Query("SELECT i FROM ItemCode i WHERE " +
           "(:useStatus IS NULL OR i.itemCodeUse = :useStatus) AND " +
           "(:scodList IS NULL OR i.itemCodeScod IN :scodList) AND " +
           "(:keyword IS NULL OR :keyword = '' OR i.itemCodeHnam LIKE %:keyword% OR i.itemCodeNum LIKE %:keyword% OR i.itemCodeSpec LIKE %:keyword% OR i.itemCodeTags LIKE %:keyword%)")
    Page<ItemCode> findAllWithFilter(@Param("useStatus") Integer useStatus, @Param("keyword") String keyword, @Param("scodList") java.util.List<String> scodList, Pageable pageable);

    default Page<ItemCode> searchByNameAndSpec(String itemName1, String itemName2,
                                              String spec1, String spec2,
                                              Pageable pageable) {
        return searchByNameAndSpecWithAndAnd(itemName1, itemName2, spec1, spec2, null, pageable);
    }
}
