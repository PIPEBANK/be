package com.pipebank.ordersystem.domain.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.MakeTran;

/**
 * MakeTran Repository (조회 전용)
 * ERP DB 연동이므로 조회만 가능
 */
@Repository
public interface MakeTranRepository extends JpaRepository<MakeTran, MakeTran.MakeTranId> {
    
    // 기본 JpaRepository 메서드만 사용 (조회 전용)
    // save, delete 등의 메서드는 사용하지 않음
}

