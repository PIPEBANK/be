package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.ItemDiv4;

@Repository
public interface ItemDiv4Repository extends JpaRepository<ItemDiv4, ItemDiv4.ItemDiv4Id> {
    
    // 복합키로 조회
    Optional<ItemDiv4> findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3AndItemDiv4Code(String itemDiv4Div1, String itemDiv4Div2, String itemDiv4Div3, String itemDiv4Code);
    
    // DIV1, DIV2, DIV3으로 하위 항목들 조회
    List<ItemDiv4> findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3(String itemDiv4Div1, String itemDiv4Div2, String itemDiv4Div3);
    
    // DIV1, DIV2로 조회
    List<ItemDiv4> findByItemDiv4Div1AndItemDiv4Div2(String itemDiv4Div1, String itemDiv4Div2);
    
    // DIV1으로 조회
    List<ItemDiv4> findByItemDiv4Div1(String itemDiv4Div1);
    
    // DIV1, DIV2, DIV3과 사용여부로 조회
    List<ItemDiv4> findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3AndItemDiv4Use(String itemDiv4Div1, String itemDiv4Div2, String itemDiv4Div3, Integer itemDiv4Use);
    
    // 한글명으로 검색
    List<ItemDiv4> findByItemDiv4NameContaining(String itemDiv4Name);
    
    // DIV1, DIV2, DIV3 내에서 한글명 검색
    List<ItemDiv4> findByItemDiv4Div1AndItemDiv4Div2AndItemDiv4Div3AndItemDiv4NameContaining(String itemDiv4Div1, String itemDiv4Div2, String itemDiv4Div3, String itemDiv4Name);
    
    // 사용중인 항목만 조회
    List<ItemDiv4> findByItemDiv4Use(Integer itemDiv4Use);
} 