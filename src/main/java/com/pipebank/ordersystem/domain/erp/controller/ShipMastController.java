package com.pipebank.ordersystem.domain.erp.controller;

import com.pipebank.ordersystem.domain.erp.dto.ShipMastListResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipmentDetailResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipSummaryResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipListResponse;
import com.pipebank.ordersystem.domain.erp.service.ShipMastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/erp/shipments")
@RequiredArgsConstructor
@Slf4j
public class ShipMastController {

    private final ShipMastService shipMastService;

    /**
     * 거래처별 출하 목록 조회 (페이징 + 필터링) - 출하번호 기준
     * GET /api/erp/shipments/customer/{custId}
     * 
     * 필터링 파라미터:
     * - shipDate: 출하일자 (정확히 일치)
     * - startDate: 시작 출하일자 (범위 조회)
     * - endDate: 종료 출하일자 (범위 조회)
     * - shipNumber: 출하번호 (부분 검색)
     * - sdiv: 출고형태 (ORDER_MAST_SDIV)
     * - comName: 현장명 (부분 검색)
     * 
     * 예시: 
     * - 특정 날짜: GET /api/erp/shipments/customer/9?shipDate=20240101
     * - 날짜 범위: GET /api/erp/shipments/customer/9?startDate=20240101&endDate=20240131
     * - 복합 필터: GET /api/erp/shipments/customer/9?startDate=20240101&endDate=20240131&sdiv=1&comName=현장명
     */
    @GetMapping("/customer/{custId}")
    public ResponseEntity<Page<ShipMastListResponse>> getShipmentsByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String shipDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String shipNumber,
            @RequestParam(required = false) String sdiv,
            @RequestParam(required = false) String comName,
            @PageableDefault(size = 20, sort = "shipMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("거래처별 출하 조회 API 호출 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, shipNumber={}, sdiv={}, comName={}", 
                custId, shipDate, startDate, endDate, shipNumber, sdiv, comName);
        
        Page<ShipMastListResponse> response = shipMastService.getShipMastsByCustomerWithFilters(
                custId, shipDate, startDate, endDate, shipNumber, sdiv, comName, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 출하번호별 출고현황 조회
     * GET /api/erp/shipments/detail/{shipNumber}
     * 
     * 출하번호로 해당 출하의 상세 출고현황을 조회합니다.
     * 응답 정보:
     * - 제품코드 (ItemCode.itemCodeNum)
     * - 규격 (ShipTran.shipTranSpec)
     * - 단위 (ShipTran.shipTranUnit)
     * - 주문량 (OrderTran.orderTranCnt)
     * - 출고량 (ShipTran.shipTranCnt)
     * - 주문잔량 (주문량 - 출고량)
     * 
     * 예시: GET /api/erp/shipments/detail/20240101-123
     */
    @GetMapping("/detail/{shipNumber}")
    public ResponseEntity<List<ShipmentDetailResponse>> getShipmentDetail(
            @PathVariable String shipNumber) {
        
        log.info("출고현황 조회 API 호출 - 출하번호: {}", shipNumber);
        
        List<ShipmentDetailResponse> response = shipMastService.getShipmentDetail(shipNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 전표번호별 출고전표현황 조회 (합계 포함)
     * GET /api/erp/shipments/slip/{slipNumber}
     * 
     * 전표번호로 해당 전표의 출고전표현황을 조회합니다.
     * 응답 정보:
     * - 상세 내역들 (각 ShipTran 정보)
     * - 수량 합계
     * - 단가 합계  
     * - 출고금액 합계
     * 
     * 예시: GET /api/erp/shipments/slip/20240101-123
     */
    @GetMapping("/slip/{slipNumber}")
    public ResponseEntity<ShipSlipSummaryResponse> getShipSlipDetail(
            @PathVariable String slipNumber) {
        
        log.info("출고전표현황 조회 API 호출 - 전표번호: {}", slipNumber);
        
        ShipSlipSummaryResponse response = shipMastService.getShipSlipDetail(slipNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처별 출고전표 목록 조회 (페이징 + 필터링)
     * GET /api/erp/shipments/slips/customer/{custId}
     * 
     * 거래처코드로 출고전표 목록을 조회합니다.
     * 필터링 파라미터:
     * - shipDate: 출고일자 (정확히 일치)
     * - startDate: 시작 출고일자 (범위 조회)
     * - endDate: 종료 출고일자 (범위 조회)
     * - searchKeyword: 주문번호 또는 출하번호로 검색 (부분 검색)
     * 
     * 응답 정보:
     * - 주문번호, 출하번호, 현장명, 출고일자, 출고금액
     * 
     * 예시: 
     * - GET /api/erp/shipments/slips/customer/9?startDate=20240101&endDate=20240131
     * - GET /api/erp/shipments/slips/customer/9?searchKeyword=20240315-123
     */
    @GetMapping("/slips/customer/{custId}")
    public ResponseEntity<Page<ShipSlipListResponse>> getShipSlipListByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String shipDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(size = 20, sort = "shipMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("거래처별 출고전표 목록 조회 API 호출 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, searchKeyword={}", 
                custId, shipDate, startDate, endDate, searchKeyword);
        
        Page<ShipSlipListResponse> response = shipMastService.getShipSlipListByCustomer(
                custId, shipDate, startDate, endDate, searchKeyword, pageable);
        return ResponseEntity.ok(response);
    }
} 