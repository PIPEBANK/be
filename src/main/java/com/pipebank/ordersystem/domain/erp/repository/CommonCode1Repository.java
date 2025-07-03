package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.CommonCode1;

@Repository
public interface CommonCode1Repository extends JpaRepository<CommonCode1, String> {

    // 사용중인 코드만 조회
    @Query("SELECT c FROM CommonCode1 c WHERE c.commCod1Use = 1 ORDER BY c.commCod1Sort")
    List<CommonCode1> findActiveCodesOrderBySort();
    
    // 조회 가능한 코드만 조회
    @Query("SELECT c FROM CommonCode1 c WHERE c.commCod1Use = 1 AND c.commCod1View = 1 ORDER BY c.commCod1Sort")
    List<CommonCode1> findVisibleCodesOrderBySort();
    
    // 코드명으로 검색
    List<CommonCode1> findByCommCod1NameContainingAndCommCod1UseOrderByCommCod1Sort(String name, Integer use);
    
    // 사용중인 코드 개수
    long countByCommCod1Use(Integer use);
} 