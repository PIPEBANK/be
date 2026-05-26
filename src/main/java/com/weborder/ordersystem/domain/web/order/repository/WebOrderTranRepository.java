package com.weborder.ordersystem.domain.web.order.repository;

import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WebOrderTranRepository extends JpaRepository<WebOrderTran, WebOrderTran.WebOrderTranId> {

    List<WebOrderTran> findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
            String date, Integer sosok, String ujcd, Integer acno);

    @Query("SELECT t FROM WebOrderTran t WHERE t.orderTranDate BETWEEN :startDate AND :endDate ORDER BY t.orderTranDate DESC, t.orderTranAcno ASC, t.orderTranSeq ASC")
    List<WebOrderTran> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT t FROM WebOrderTran t WHERE CONCAT(t.orderTranDate, '-', t.orderTranSosok, '-', t.orderTranUjcd, '-', t.orderTranAcno) IN :orderKeys")
    List<WebOrderTran> findByOrderKeys(@Param("orderKeys") List<String> orderKeys);

    default Integer findMaxSeq(String date, Integer sosok, String ujcd, Integer acno) {
        List<WebOrderTran> trans = findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(date, sosok, ujcd, acno);
        return trans.isEmpty() ? 0 : trans.get(trans.size() - 1).getOrderTranSeq();
    }
}
