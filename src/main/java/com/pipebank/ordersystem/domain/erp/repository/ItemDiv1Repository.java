package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.ItemDiv1;

@Repository
public interface ItemDiv1Repository extends JpaRepository<ItemDiv1, String> {
    
    // 코드로 조회
    Optional<ItemDiv1> findByItemDiv1Code(String itemDiv1Code);
    
    // 한글명으로 검색
    List<ItemDiv1> findByItemDiv1NameContaining(String itemDiv1Name);
    
    // 사용중인 항목만 조회
    List<ItemDiv1> findByItemDiv1Use(Integer itemDiv1Use);
    
    // 사용중인 항목 중 한글명 검색
    List<ItemDiv1> findByItemDiv1UseAndItemDiv1NameContaining(Integer itemDiv1Use, String itemDiv1Name);
} 