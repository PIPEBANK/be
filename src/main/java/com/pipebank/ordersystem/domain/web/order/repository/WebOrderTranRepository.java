package com.pipebank.ordersystem.domain.web.order.repository;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderTran;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebOrderTranRepository extends JpaRepository<WebOrderTran, WebOrderTran.WebOrderTranId> {

    // SEQ 자동 생성을 위한 메서드 - 해당 주문의 최대 SEQ 조회
    @Query("SELECT COALESCE(MAX(w.orderTranSeq), 0) FROM WebOrderTran w " +
           "WHERE w.orderTranDate = :date AND w.orderTranSosok = :sosok AND w.orderTranUjcd = :ujcd AND w.orderTranAcno = :acno")
    Integer findMaxSeqByDateAndSosokAndUjcdAndAcno(@Param("date") String date, 
                                                  @Param("sosok") Integer sosok, 
                                                  @Param("ujcd") String ujcd,
                                                  @Param("acno") Integer acno);

    // 기본 조회 - 페이징
    Page<WebOrderTran> findAll(Pageable pageable);

    // 특정 주문의 모든 상세 조회
    List<WebOrderTran> findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeq(
            String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno);

    // 특정 주문의 모든 상세 조회 (페이징)
    Page<WebOrderTran> findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
            String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno, Pageable pageable);

    // 품목코드로 조회
    Page<WebOrderTran> findByOrderTranItem(Integer orderTranItem, Pageable pageable);

    // 상태코드로 조회
    Page<WebOrderTran> findByOrderTranStau(String orderTranStau, Pageable pageable);

    // 복합키로 단건 조회
    Optional<WebOrderTran> findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoAndOrderTranSeq(
            String orderTranDate, Integer orderTranSosok, String orderTranUjcd, Integer orderTranAcno, Integer orderTranSeq);
} 