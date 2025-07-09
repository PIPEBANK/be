package com.pipebank.ordersystem.domain.web.temp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;

@Repository
public interface TempWebOrderTranRepository extends JpaRepository<TempWebOrderTran, TempWebOrderTran.TempWebOrderTranId> {
    
    // SEQ 자동 생성을 위한 메서드 - 해당 주문의 최대 SEQ 조회
    @Query("SELECT COALESCE(MAX(t.orderTranSeq), 0) FROM TempWebOrderTran t " +
           "WHERE t.orderTranDate = :date AND t.orderTranSosok = :sosok AND t.orderTranUjcd = :ujcd AND t.orderTranAcno = :acno")
    Integer findMaxSeqByOrderKey(@Param("date") String date, 
                                @Param("sosok") Integer sosok, 
                                @Param("ujcd") String ujcd,
                                @Param("acno") Integer acno);
    
    // 특정 주문의 모든 TempWebOrderTran 조회
    List<TempWebOrderTran> findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
            String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno);
} 