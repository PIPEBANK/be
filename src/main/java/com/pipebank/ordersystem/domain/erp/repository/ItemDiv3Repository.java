package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.ItemDiv3;

@Repository
public interface ItemDiv3Repository extends JpaRepository<ItemDiv3, ItemDiv3.ItemDiv3Id> {
    
    // 복합키로 조회
    Optional<ItemDiv3> findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3Code(String itemDiv3Div1, String itemDiv3Div2, String itemDiv3Code);
    
    // DIV1, DIV2로 하위 항목들 조회
    List<ItemDiv3> findByItemDiv3Div1AndItemDiv3Div2(String itemDiv3Div1, String itemDiv3Div2);
    
    // DIV1으로 조회
    List<ItemDiv3> findByItemDiv3Div1(String itemDiv3Div1);
    
    // DIV1, DIV2와 사용여부로 조회
    List<ItemDiv3> findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3Use(String itemDiv3Div1, String itemDiv3Div2, Integer itemDiv3Use);
    
    // 한글명으로 검색
    List<ItemDiv3> findByItemDiv3NameContaining(String itemDiv3Name);
    
    // DIV1, DIV2 내에서 한글명 검색
    List<ItemDiv3> findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3NameContaining(String itemDiv3Div1, String itemDiv3Div2, String itemDiv3Name);
    
    // 사용중인 항목만 조회
    List<ItemDiv3> findByItemDiv3Use(Integer itemDiv3Use);
    
    // DIV1, DIV2와 사용여부로 조회하여 코드순 정렬
    List<ItemDiv3> findByItemDiv3Div1AndItemDiv3Div2AndItemDiv3UseOrderByItemDiv3Code(String itemDiv3Div1, String itemDiv3Div2, Integer itemDiv3Use);
} 