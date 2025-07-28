package com.pipebank.ordersystem.domain.erp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pipebank.ordersystem.domain.erp.entity.ShipMast;

@Repository
public interface ShipMastRepository extends JpaRepository<ShipMast, ShipMast.ShipMastId> {

    /**
     * 거래처별 출하 마스터 조회 (페이징 + 필터링)
     * ShipOrder를 통해 OrderMast와 조인하여 출고형태 정보도 함께 조회
     */
    @Query("""
        SELECT sm, om
        FROM ShipMast sm
        JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
        JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR sm.shipMastDate = :shipDate)
        AND (:startDate IS NULL OR sm.shipMastDate >= :startDate)
        AND (:endDate IS NULL OR sm.shipMastDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:sdiv IS NULL OR om.orderMastSdiv = :sdiv)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY sm.shipMastDate DESC, sm.shipMastAcno DESC
        """)
    Page<Object[]> findShipMastWithOrderMastByCustomerWithFilters(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("sdiv") String sdiv,
        @Param("comName") String comName,
        Pageable pageable
    );

    /**
     * 출하일자와 출하번호로 ShipMast 조회
     */
    @Query("""
        SELECT sm
        FROM ShipMast sm
        WHERE sm.shipMastDate = :shipDate
        AND sm.shipMastAcno = :shipAcno
        """)
    List<ShipMast> findByShipMastDateAndShipMastAcno(
        @Param("shipDate") String shipDate,
        @Param("shipAcno") Integer shipAcno
    );

    /**
     * 거래처별 출고전표 목록 조회 (페이징 + 필터링)
     * ShipOrder를 통해 OrderMast와 조인하여 주문번호 정보도 함께 조회
     */
    @Query("""
        SELECT sm, om
        FROM ShipMast sm
        JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
        JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR sm.shipMastDate = :shipDate)
        AND (:startDate IS NULL OR sm.shipMastDate >= :startDate)
        AND (:endDate IS NULL OR sm.shipMastDate <= :endDate)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        GROUP BY sm.shipMastDate, sm.shipMastSosok, sm.shipMastUjcd, sm.shipMastAcno,
                 om.orderMastDate, om.orderMastSosok, om.orderMastUjcd, om.orderMastAcno
        ORDER BY sm.shipMastDate DESC, sm.shipMastAcno DESC
        """)
    List<Object[]> findShipSlipListByCustomerWithFiltersNative(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orderNumber") String orderNumber,
        @Param("shipNumber") String shipNumber,
        @Param("comName") String comName
    );

    /**
     * 거래처별 현장별 출하조회 (ShipTran 단위) - 페이징 + 필터링
     * ShipMast → ShipTran JOIN으로 모든 제품별 출하 정보 조회
     * ShipOrder → OrderMast JOIN으로 주문번호 필터링 지원
     */
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName IS NULL OR st.shipTranDeta LIKE %:itemName%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFilters(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName") String itemName,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 AND, 규격 AND
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%)
        AND (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%)
        AND (:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%)
        AND (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersAndAnd(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 OR, 규격 AND
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND ((:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%) OR 
             (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%))
        AND (:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%)
        AND (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%)
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersOrAnd(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 AND, 규격 OR
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%)
        AND (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%)
        AND ((:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%) OR 
             (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%))
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersAndOr(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 제품명 OR, 규격 OR
    @Query("""
        SELECT sm, st, so, om
        FROM ShipMast sm
        JOIN ShipTran st ON sm.shipMastDate = st.shipTranDate 
            AND sm.shipMastSosok = st.shipTranSosok 
            AND sm.shipMastUjcd = st.shipTranUjcd 
            AND sm.shipMastAcno = st.shipTranAcno
        LEFT JOIN ShipOrder so ON sm.shipMastDate = so.shipOrderDate 
            AND sm.shipMastSosok = so.shipOrderSosok 
            AND sm.shipMastUjcd = so.shipOrderUjcd 
            AND sm.shipMastAcno = so.shipOrderAcno
            AND st.shipTranSeq = so.shipOrderSeq
        LEFT JOIN OrderMast om ON so.shipOrderOdate = om.orderMastDate 
            AND so.shipOrderSosok = om.orderMastSosok 
            AND so.shipOrderUjcd = om.orderMastUjcd 
            AND so.shipOrderOacno = om.orderMastAcno
        WHERE sm.shipMastCust = :custId
        AND (:shipDate IS NULL OR st.shipTranDate = :shipDate)
        AND (:startDate IS NULL OR st.shipTranDate >= :startDate)
        AND (:endDate IS NULL OR st.shipTranDate <= :endDate)
        AND (:shipNumber IS NULL OR CONCAT(sm.shipMastDate, '-', sm.shipMastAcno) LIKE %:shipNumber%)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND ((:itemName1 IS NULL OR :itemName1 = '' OR st.shipTranDeta LIKE %:itemName1%) OR 
             (:itemName2 IS NULL OR :itemName2 = '' OR st.shipTranDeta LIKE %:itemName2%))
        AND ((:spec1 IS NULL OR :spec1 = '' OR st.shipTranSpec LIKE %:spec1%) OR 
             (:spec2 IS NULL OR :spec2 = '' OR st.shipTranSpec LIKE %:spec2%))
        AND (:comName IS NULL OR sm.shipMastComname LIKE %:comName%)
        ORDER BY st.shipTranDate DESC, sm.shipMastDate DESC, sm.shipMastAcno DESC, st.shipTranSeq ASC
        """)
    Page<Object[]> findShipmentItemsByCustomerWithFiltersOrOr(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("shipNumber") String shipNumber,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("comName") String comName,
        Pageable pageable
    );

    // 🔥 하위호환성을 위한 default 메서드
    default Page<Object[]> findShipmentItemsByCustomerWithFilters(
            Integer custId, String shipDate, String startDate, String endDate,
            String shipNumber, String orderNumber, String itemName1, String itemName2,
            String spec1, String spec2, String itemNameOperator, String specOperator,
            String comName, Pageable pageable) {
        
        boolean itemNameIsOr = "OR".equalsIgnoreCase(itemNameOperator);
        boolean specIsOr = "OR".equalsIgnoreCase(specOperator);

        if (!itemNameIsOr && !specIsOr) {
            // AND, AND
            return findShipmentItemsByCustomerWithFiltersAndAnd(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        } else if (itemNameIsOr && !specIsOr) {
            // OR, AND
            return findShipmentItemsByCustomerWithFiltersOrAnd(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        } else if (!itemNameIsOr && specIsOr) {
            // AND, OR
            return findShipmentItemsByCustomerWithFiltersAndOr(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        } else {
            // OR, OR
            return findShipmentItemsByCustomerWithFiltersOrOr(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                itemName1, itemName2, spec1, spec2, comName, pageable);
        }
    }

    /**
     * 🔥 주문-출하 통합 상세 조회 (페이징 + 2중 필터링)
     * OrderMast + OrderTran + ItemCode + ShipTran 통합 조회
     * 
     * @param custId 거래처ID (ORDER_MAST_CUST 기준)
     * @param shipDate 출하일자 (정확 일치)
     * @param startDate 시작일자 (범위 조회)
     * @param endDate 종료일자 (범위 조회)
     * @param orderNumber 주문번호 (부분 검색)
     * @param itemName1 품명1 (부분 검색)
     * @param itemName2 품명2 (부분 검색)
     * @param spec1 규격1 (부분 검색)
     * @param spec2 규격2 (부분 검색)
     * @param itemNameOperator 품명 검색 연산자 (AND/OR)
     * @param specOperator 규격 검색 연산자 (AND/OR)
     * @param siteName 현장명 (부분 검색)
     * @param excludeCompleted 완료 내역 제외 여부
     * @param statusFilter 특정 상태만 조회
     * @param pageable 페이징 정보
     * @return 통합 조회 결과 (17개 필드)
     */
    default Page<Object[]> findOrderShipmentDetailByCustomer(
            Integer custId, String shipDate, String startDate, String endDate, String orderNumber,
            String itemName1, String itemName2, String spec1, String spec2,
            String itemNameOperator, String specOperator, String siteName,
            boolean excludeCompleted, String statusFilter, Pageable pageable) {
        
        // 품명 연산자에 따른 분기
        boolean itemNameAnd = "AND".equalsIgnoreCase(itemNameOperator);
        boolean specAnd = "AND".equalsIgnoreCase(specOperator);
        
        if (itemNameAnd && specAnd) {
            // AND, AND
            return findOrderShipmentDetailByCustomerAndAnd(
                custId, shipDate, startDate, endDate, orderNumber,
                itemName1, itemName2, spec1, spec2, siteName, excludeCompleted, statusFilter, pageable);
        } else if (!itemNameAnd && specAnd) {
            // OR, AND
            return findOrderShipmentDetailByCustomerOrAnd(
                custId, shipDate, startDate, endDate, orderNumber,
                itemName1, itemName2, spec1, spec2, siteName, excludeCompleted, statusFilter, pageable);
        } else if (itemNameAnd && !specAnd) {
            // AND, OR
            return findOrderShipmentDetailByCustomerAndOr(
                custId, shipDate, startDate, endDate, orderNumber,
                itemName1, itemName2, spec1, spec2, siteName, excludeCompleted, statusFilter, pageable);
        } else {
            // OR, OR
            return findOrderShipmentDetailByCustomerOrOr(
                custId, shipDate, startDate, endDate, orderNumber,
                itemName1, itemName2, spec1, spec2, siteName, excludeCompleted, statusFilter, pageable);
        }
    }

    // 🔥 품명 AND, 규격 AND
    @Query("""
        SELECT 
            om.orderMastDate,
            om.orderMastAcno,
            om.orderMastOdate,
            ot.orderTranStau,
            ic.itemCodeNum,
            ot.orderTranDeta,
            ot.orderTranSpec,
            ot.orderTranUnit,
            om.orderMastComname,
            om.orderMastDcust,
            ot.orderTranCnt,
            ot.orderTranAmt,
            ot.orderTranDcPer,
            ot.orderTranTot,
            COALESCE(SUM(st.shipTranCnt), 0),
            cc3.commCod3Hnam
        FROM OrderMast om
        INNER JOIN OrderTran ot ON om.orderMastDate = ot.orderTranDate 
            AND om.orderMastSosok = ot.orderTranSosok 
            AND om.orderMastUjcd = ot.orderTranUjcd 
            AND om.orderMastAcno = ot.orderTranAcno
        LEFT JOIN ItemCode ic ON ot.orderTranItem = ic.itemCodeCode
        LEFT JOIN ShipOrder so ON ot.orderTranDate = so.shipOrderOdate 
            AND ot.orderTranSosok = so.shipOrderSosok 
            AND ot.orderTranUjcd = so.shipOrderUjcd 
            AND ot.orderTranAcno = so.shipOrderOacno 
            AND ot.orderTranSeq = so.shipOrderOseq
        LEFT JOIN ShipTran st ON so.shipOrderDate = st.shipTranDate 
            AND so.shipOrderSosok = st.shipTranSosok 
            AND so.shipOrderUjcd = st.shipTranUjcd 
            AND so.shipOrderAcno = st.shipTranAcno 
            AND so.shipOrderSeq = st.shipTranSeq
        LEFT JOIN CommonCode3 cc3 ON ot.orderTranStau = cc3.commCod3Code
        WHERE om.orderMastCust = :custId
        AND (:shipDate IS NULL OR om.orderMastDate = :shipDate)
        AND (:startDate IS NULL OR om.orderMastDate >= :startDate)
        AND (:endDate IS NULL OR om.orderMastDate <= :endDate)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName1 IS NULL OR ot.orderTranDeta LIKE %:itemName1%)
        AND (:itemName2 IS NULL OR ot.orderTranDeta LIKE %:itemName2%)
        AND (:spec1 IS NULL OR ot.orderTranSpec LIKE %:spec1%)
        AND (:spec2 IS NULL OR ot.orderTranSpec LIKE %:spec2%)
        AND (:siteName IS NULL OR om.orderMastComname LIKE %:siteName%)
        AND (:excludeCompleted = false OR ot.orderTranStau != '4010030001')
        AND (:statusFilter IS NULL OR ot.orderTranStau = :statusFilter)
        GROUP BY om.orderMastDate, om.orderMastAcno, ot.orderTranSeq,
                 om.orderMastOdate, ot.orderTranStau, ic.itemCodeNum, 
                 ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
                 om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, 
                 ot.orderTranAmt, ot.orderTranDcPer, ot.orderTranTot, cc3.commCod3Hnam
        """)
    Page<Object[]> findOrderShipmentDetailByCustomerAndAnd(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("siteName") String siteName,
        @Param("excludeCompleted") boolean excludeCompleted,
        @Param("statusFilter") String statusFilter,
        Pageable pageable
    );

    // 🔥 품명 OR, 규격 AND
    @Query("""
        SELECT 
            om.orderMastDate, om.orderMastAcno, om.orderMastOdate, ot.orderTranStau,
            ic.itemCodeNum, ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
            om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, ot.orderTranAmt, 
            ot.orderTranDcPer, ot.orderTranTot, COALESCE(SUM(st.shipTranCnt), 0), cc3.commCod3Hnam
        FROM OrderMast om
        INNER JOIN OrderTran ot ON om.orderMastDate = ot.orderTranDate 
            AND om.orderMastSosok = ot.orderTranSosok 
            AND om.orderMastUjcd = ot.orderTranUjcd 
            AND om.orderMastAcno = ot.orderTranAcno
        LEFT JOIN ItemCode ic ON ot.orderTranItem = ic.itemCodeCode
        LEFT JOIN ShipOrder so ON ot.orderTranDate = so.shipOrderOdate 
            AND ot.orderTranSosok = so.shipOrderSosok 
            AND ot.orderTranUjcd = so.shipOrderUjcd 
            AND ot.orderTranAcno = so.shipOrderOacno 
            AND ot.orderTranSeq = so.shipOrderOseq
        LEFT JOIN ShipTran st ON so.shipOrderDate = st.shipTranDate 
            AND so.shipOrderSosok = st.shipTranSosok 
            AND so.shipOrderUjcd = st.shipTranUjcd 
            AND so.shipOrderAcno = st.shipTranAcno 
            AND so.shipOrderSeq = st.shipTranSeq
        LEFT JOIN CommonCode3 cc3 ON ot.orderTranStau = cc3.commCod3Code
        WHERE om.orderMastCust = :custId
        AND (:shipDate IS NULL OR om.orderMastDate = :shipDate)
        AND (:startDate IS NULL OR om.orderMastDate >= :startDate)
        AND (:endDate IS NULL OR om.orderMastDate <= :endDate)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND ((:itemName1 IS NULL OR ot.orderTranDeta LIKE %:itemName1%) 
             OR (:itemName2 IS NULL OR ot.orderTranDeta LIKE %:itemName2%))
        AND (:spec1 IS NULL OR ot.orderTranSpec LIKE %:spec1%)
        AND (:spec2 IS NULL OR ot.orderTranSpec LIKE %:spec2%)
        AND (:siteName IS NULL OR om.orderMastComname LIKE %:siteName%)
        AND (:excludeCompleted = false OR ot.orderTranStau != '4010030001')
        AND (:statusFilter IS NULL OR ot.orderTranStau = :statusFilter)
        GROUP BY om.orderMastDate, om.orderMastAcno, ot.orderTranSeq,
                 om.orderMastOdate, ot.orderTranStau, ic.itemCodeNum, 
                 ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
                 om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, 
                 ot.orderTranAmt, ot.orderTranDcPer, ot.orderTranTot, cc3.commCod3Hnam
        """)
    Page<Object[]> findOrderShipmentDetailByCustomerOrAnd(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("siteName") String siteName,
        @Param("excludeCompleted") boolean excludeCompleted,
        @Param("statusFilter") String statusFilter,
        Pageable pageable
    );

    // 🔥 품명 AND, 규격 OR
    @Query("""
        SELECT 
            om.orderMastDate, om.orderMastAcno, om.orderMastOdate, ot.orderTranStau,
            ic.itemCodeNum, ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
            om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, ot.orderTranAmt, 
            ot.orderTranDcPer, ot.orderTranTot, COALESCE(SUM(st.shipTranCnt), 0), cc3.commCod3Hnam
        FROM OrderMast om
        INNER JOIN OrderTran ot ON om.orderMastDate = ot.orderTranDate 
            AND om.orderMastSosok = ot.orderTranSosok 
            AND om.orderMastUjcd = ot.orderTranUjcd 
            AND om.orderMastAcno = ot.orderTranAcno
        LEFT JOIN ItemCode ic ON ot.orderTranItem = ic.itemCodeCode
        LEFT JOIN ShipOrder so ON ot.orderTranDate = so.shipOrderOdate 
            AND ot.orderTranSosok = so.shipOrderSosok 
            AND ot.orderTranUjcd = so.shipOrderUjcd 
            AND ot.orderTranAcno = so.shipOrderOacno 
            AND ot.orderTranSeq = so.shipOrderOseq
        LEFT JOIN ShipTran st ON so.shipOrderDate = st.shipTranDate 
            AND so.shipOrderSosok = st.shipTranSosok 
            AND so.shipOrderUjcd = st.shipTranUjcd 
            AND so.shipOrderAcno = st.shipTranAcno 
            AND so.shipOrderSeq = st.shipTranSeq
        LEFT JOIN CommonCode3 cc3 ON ot.orderTranStau = cc3.commCod3Code
        WHERE om.orderMastCust = :custId
        AND (:shipDate IS NULL OR om.orderMastDate = :shipDate)
        AND (:startDate IS NULL OR om.orderMastDate >= :startDate)
        AND (:endDate IS NULL OR om.orderMastDate <= :endDate)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND (:itemName1 IS NULL OR ot.orderTranDeta LIKE %:itemName1%)
        AND (:itemName2 IS NULL OR ot.orderTranDeta LIKE %:itemName2%)
        AND ((:spec1 IS NULL OR ot.orderTranSpec LIKE %:spec1%) 
             OR (:spec2 IS NULL OR ot.orderTranSpec LIKE %:spec2%))
        AND (:siteName IS NULL OR om.orderMastComname LIKE %:siteName%)
        AND (:excludeCompleted = false OR ot.orderTranStau != '4010030001')
        AND (:statusFilter IS NULL OR ot.orderTranStau = :statusFilter)
        GROUP BY om.orderMastDate, om.orderMastAcno, ot.orderTranSeq,
                 om.orderMastOdate, ot.orderTranStau, ic.itemCodeNum, 
                 ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
                 om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, 
                 ot.orderTranAmt, ot.orderTranDcPer, ot.orderTranTot, cc3.commCod3Hnam
        """)
    Page<Object[]> findOrderShipmentDetailByCustomerAndOr(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("siteName") String siteName,
        @Param("excludeCompleted") boolean excludeCompleted,
        @Param("statusFilter") String statusFilter,
        Pageable pageable
    );

    // 🔥 품명 OR, 규격 OR
    @Query("""
        SELECT 
            om.orderMastDate, om.orderMastAcno, om.orderMastOdate, ot.orderTranStau,
            ic.itemCodeNum, ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
            om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, ot.orderTranAmt, 
            ot.orderTranDcPer, ot.orderTranTot, COALESCE(SUM(st.shipTranCnt), 0), cc3.commCod3Hnam
        FROM OrderMast om
        INNER JOIN OrderTran ot ON om.orderMastDate = ot.orderTranDate 
            AND om.orderMastSosok = ot.orderTranSosok 
            AND om.orderMastUjcd = ot.orderTranUjcd 
            AND om.orderMastAcno = ot.orderTranAcno
        LEFT JOIN ItemCode ic ON ot.orderTranItem = ic.itemCodeCode
        LEFT JOIN ShipOrder so ON ot.orderTranDate = so.shipOrderOdate 
            AND ot.orderTranSosok = so.shipOrderSosok 
            AND ot.orderTranUjcd = so.shipOrderUjcd 
            AND ot.orderTranAcno = so.shipOrderOacno 
            AND ot.orderTranSeq = so.shipOrderOseq
        LEFT JOIN ShipTran st ON so.shipOrderDate = st.shipTranDate 
            AND so.shipOrderSosok = st.shipTranSosok 
            AND so.shipOrderUjcd = st.shipTranUjcd 
            AND so.shipOrderAcno = st.shipTranAcno 
            AND so.shipOrderSeq = st.shipTranSeq
        LEFT JOIN CommonCode3 cc3 ON ot.orderTranStau = cc3.commCod3Code
        WHERE om.orderMastCust = :custId
        AND (:shipDate IS NULL OR om.orderMastDate = :shipDate)
        AND (:startDate IS NULL OR om.orderMastDate >= :startDate)
        AND (:endDate IS NULL OR om.orderMastDate <= :endDate)
        AND (:orderNumber IS NULL OR CONCAT(om.orderMastDate, '-', om.orderMastAcno) LIKE %:orderNumber%)
        AND ((:itemName1 IS NULL OR ot.orderTranDeta LIKE %:itemName1%) 
             OR (:itemName2 IS NULL OR ot.orderTranDeta LIKE %:itemName2%))
        AND ((:spec1 IS NULL OR ot.orderTranSpec LIKE %:spec1%) 
             OR (:spec2 IS NULL OR ot.orderTranSpec LIKE %:spec2%))
        AND (:siteName IS NULL OR om.orderMastComname LIKE %:siteName%)
        AND (:excludeCompleted = false OR ot.orderTranStau != '4010030001')
        AND (:statusFilter IS NULL OR ot.orderTranStau = :statusFilter)
        GROUP BY om.orderMastDate, om.orderMastAcno, ot.orderTranSeq,
                 om.orderMastOdate, ot.orderTranStau, ic.itemCodeNum, 
                 ot.orderTranDeta, ot.orderTranSpec, ot.orderTranUnit,
                 om.orderMastComname, om.orderMastDcust, ot.orderTranCnt, 
                 ot.orderTranAmt, ot.orderTranDcPer, ot.orderTranTot, cc3.commCod3Hnam
        """)
    Page<Object[]> findOrderShipmentDetailByCustomerOrOr(
        @Param("custId") Integer custId,
        @Param("shipDate") String shipDate,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orderNumber") String orderNumber,
        @Param("itemName1") String itemName1,
        @Param("itemName2") String itemName2,
        @Param("spec1") String spec1,
        @Param("spec2") String spec2,
        @Param("siteName") String siteName,
        @Param("excludeCompleted") boolean excludeCompleted,
        @Param("statusFilter") String statusFilter,
        Pageable pageable
    );
} 