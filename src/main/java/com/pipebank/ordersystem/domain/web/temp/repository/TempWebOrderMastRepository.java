package com.pipebank.ordersystem.domain.web.temp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;

@Repository
public interface TempWebOrderMastRepository extends JpaRepository<TempWebOrderMast, TempWebOrderMast.TempWebOrderMastId> {
    
    // ACNO 자동 생성을 위한 메서드 - 해당 날짜, 소속, 업장의 최대 ACNO 조회
    @Query("SELECT COALESCE(MAX(t.orderMastAcno), 0) FROM TempWebOrderMast t " +
           "WHERE t.orderMastDate = :date AND t.orderMastSosok = :sosok AND t.orderMastUjcd = :ujcd")
    Integer findMaxAcnoByDateAndSosokAndUjcd(@Param("date") String date, 
                                           @Param("sosok") Integer sosok, 
                                           @Param("ujcd") String ujcd);
} 