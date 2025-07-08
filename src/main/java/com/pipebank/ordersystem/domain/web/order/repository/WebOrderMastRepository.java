package com.pipebank.ordersystem.domain.web.order.repository;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderMast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebOrderMastRepository extends JpaRepository<WebOrderMast, WebOrderMast.WebOrderMastId> {

    // ACNO 자동 생성을 위한 메서드 - 해당 날짜, 소속, 업장의 최대 ACNO 조회
    @Query("SELECT COALESCE(MAX(w.orderMastAcno), 0) FROM WebOrderMast w " +
           "WHERE w.orderMastDate = :date AND w.orderMastSosok = :sosok AND w.orderMastUjcd = :ujcd")
    Integer findMaxAcnoByDateAndSosokAndUjcd(@Param("date") String date, 
                                           @Param("sosok") Integer sosok, 
                                           @Param("ujcd") String ujcd);

    // 기본 조회 - 페이징
    Page<WebOrderMast> findAll(Pageable pageable);

    // 특정 조건으로 조회
    Page<WebOrderMast> findByOrderMastDate(String orderMastDate, Pageable pageable);
    
    Page<WebOrderMast> findByOrderMastCust(Integer orderMastCust, Pageable pageable);
    
    // 복합키로 단건 조회
    Optional<WebOrderMast> findByOrderMastDateAndOrderMastSosokAndOrderMastUjcdAndOrderMastAcno(
            String orderMastDate, Integer orderMastSosok, String orderMastUjcd, Integer orderMastAcno);
} 