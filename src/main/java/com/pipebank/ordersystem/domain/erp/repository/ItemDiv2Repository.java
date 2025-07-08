package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.ItemDiv2;

@Repository
public interface ItemDiv2Repository extends JpaRepository<ItemDiv2, ItemDiv2.ItemDiv2Id> {
    
    // 복합키로 조회
    Optional<ItemDiv2> findByItemDiv2Div1AndItemDiv2Code(String itemDiv2Div1, String itemDiv2Code);
    
    // DIV1으로 하위 항목들 조회
    List<ItemDiv2> findByItemDiv2Div1(String itemDiv2Div1);
    
    // DIV1과 사용여부로 조회
    List<ItemDiv2> findByItemDiv2Div1AndItemDiv2Use(String itemDiv2Div1, Integer itemDiv2Use);
    
    // 한글명으로 검색
    List<ItemDiv2> findByItemDiv2NameContaining(String itemDiv2Name);
    
    // DIV1 내에서 한글명 검색
    List<ItemDiv2> findByItemDiv2Div1AndItemDiv2NameContaining(String itemDiv2Div1, String itemDiv2Name);
    
    // 사용중인 항목만 조회
    List<ItemDiv2> findByItemDiv2Use(Integer itemDiv2Use);
    
    // DIV1과 사용여부로 조회하여 코드순 정렬
    List<ItemDiv2> findByItemDiv2Div1AndItemDiv2UseOrderByItemDiv2Code(String itemDiv2Div1, Integer itemDiv2Use);
} 