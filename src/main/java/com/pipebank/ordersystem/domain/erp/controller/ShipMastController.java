package com.pipebank.ordersystem.domain.erp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.erp.dto.OrderShipmentDetailResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipMastListResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipListResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipSlipSummaryResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipmentDetailResponse;
import com.pipebank.ordersystem.domain.erp.dto.ShipmentItemResponse;
import com.pipebank.ordersystem.domain.erp.service.ShipMastService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * - orderNumber: 주문번호 (부분 검색)
     * - shipNumber: 출하번호 (부분 검색)
     * - comName: 현장명 (부분 검색)
     * 
     * 응답 정보:
     * - 주문번호, 출하번호, 현장명, 출고일자, 출고금액
     * 
     * 예시: 
     * - GET /api/erp/shipments/slips/customer/9?startDate=20240101&endDate=20240131
     * - GET /api/erp/shipments/slips/customer/9?orderNumber=20240315-123&comName=대화도시가스
     */
    @GetMapping("/slips/customer/{custId}")
    public ResponseEntity<Page<ShipSlipListResponse>> getShipSlipListByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String shipDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String shipNumber,
            @RequestParam(required = false) String comName,
            @PageableDefault(size = 20, sort = "shipMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("거래처별 출고전표 목록 조회 API 호출 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, orderNumber={}, shipNumber={}, comName={}", 
                custId, shipDate, startDate, endDate, orderNumber, shipNumber, comName);
        
        Page<ShipSlipListResponse> response = shipMastService.getShipSlipListByCustomer(
                custId, shipDate, startDate, endDate, orderNumber, shipNumber, comName, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처별 현장별 출하조회 (ShipTran 단위, 페이징 + 필터링)
     * GET /api/erp/shipments/items/customer/{custId}
     * 
     * 거래처코드로 현장별 출하 정보를 ShipTran 단위로 조회합니다.
     * 모든 제품별 출하 정보가 표시되며 중복 제거하지 않습니다.
     * 
     * 필터링 파라미터:
     * - shipDate: 출고일자 (정확히 일치)
     * - startDate: 시작 출고일자 (범위 조회)
     * - endDate: 종료 출고일자 (범위 조회)
     * - shipNumber: 출하번호 (부분 검색)
     * - orderNumber: 주문번호 (부분 검색)
     * - itemName1: 제품명1 (부분 검색) 🆕
     * - itemName2: 제품명2 (부분 검색) 🆕
     * - spec1: 규격1 (부분 검색) 🆕
     * - spec2: 규격2 (부분 검색) 🆕
     * - itemNameOperator: 제품명 검색 연산자 (AND/OR, 기본값: AND) 🆕
     * - specOperator: 규격 검색 연산자 (AND/OR, 기본값: AND) 🆕
     * - itemName: 제품명 (하위호환성용, itemName1로 매핑됨)
     * - comName: 현장명 (부분 검색)
     * 
     * 응답 정보:
     * - 현장명, 출하번호, 주문번호, 제품명, 규격, 단위, 출고일자, 수량, 단가
     * - 차량번호, 운송기사명, 운송회사전화, 차량톤수코드, 차량톤수명
     * 
     * 예시: 
     * - GET /api/erp/shipments/items/customer/9?startDate=20240101&endDate=20240131
     * - GET /api/erp/shipments/items/customer/9?shipNumber=20240315-123&itemName=파이프
     * - GET /api/erp/shipments/items/customer/9?orderNumber=20240731-6
     * - GET /api/erp/shipments/items/customer/9?itemName1=가스관&itemName2=파이프&itemNameOperator=OR 🆕
     * - GET /api/erp/shipments/items/customer/9?spec1=63&spec2=75&specOperator=OR 🆕
     */
    @GetMapping("/items/customer/{custId}")
    public ResponseEntity<Page<ShipmentItemResponse>> getShipmentItemsByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String shipDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String shipNumber,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String itemName1,
            @RequestParam(required = false) String itemName2,
            @RequestParam(required = false) String spec1,
            @RequestParam(required = false) String spec2,
            // 🔥 하위호환성을 위한 기존 파라미터명 지원
            @RequestParam(required = false) String itemName,
            @RequestParam(defaultValue = "AND") String itemNameOperator,  // AND 또는 OR
            @RequestParam(defaultValue = "AND") String specOperator,      // AND 또는 OR
            @RequestParam(required = false) String comName,
            @PageableDefault(size = 20, sort = "shipMastDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        // 🔥 하위호환성 처리: 기존 itemName 파라미터가 넘어오면 itemName1로 매핑
        String finalItemName1 = itemName1 != null ? itemName1 : itemName;
        
        log.info("거래처별 현장별 출하조회 API 호출 - 거래처ID: {}, 필터: shipDate={}, startDate={}, endDate={}, shipNumber={}, orderNumber={}, itemName1={}, itemName2={}, spec1={}, spec2={}, itemNameOp={}, specOp={}, comName={}", 
                custId, shipDate, startDate, endDate, shipNumber, orderNumber, finalItemName1, itemName2, spec1, spec2, itemNameOperator, specOperator, comName);
        
        Page<ShipmentItemResponse> response = shipMastService.getShipmentItemsByCustomer(
                custId, shipDate, startDate, endDate, shipNumber, orderNumber,
                finalItemName1, itemName2, spec1, spec2, itemNameOperator, specOperator,
                comName, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 🔥 주문-출하 통합 상세 조회 (페이징 + 2중 필터링)
     * GET /api/erp/shipments/order-shipment-detail/customer/{custId}
     * 
     * OrderMast + OrderTran + ItemCode + ShipTran 완전 통합 조회
     * 17개 필드 모든 정보 제공 (주문정보 + 품목정보 + 현장정보 + 수주정보 + 출하정보)
     * 
     * 필터링 파라미터:
     * - shipDate: 출하일자 (정확 일치)
     * - startDate: 시작일자 (범위 조회)
     * - endDate: 종료일자 (범위 조회)
     * - orderNumber: 주문번호 (부분 검색)
     * - itemName1: 품명1 (부분 검색) 🆕
     * - itemName2: 품명2 (부분 검색) 🆕
     * - spec1: 규격1 (부분 검색) 🆕
     * - spec2: 규격2 (부분 검색) 🆕
     * - itemNumber: 품번 (부분 검색) 🆕
     * - itemNameOperator: 품명 검색 연산자 (AND/OR, 기본값: AND) 🆕
     * - specOperator: 규격 검색 연산자 (AND/OR, 기본값: AND) 🆕
     * - siteName: 현장명 (부분 검색) 🆕
     * - excludeCompleted: 완료 내역 제외 여부 (true/false, 기본값: false) 🆕
     * - statusFilter: 특정 상태만 조회 (선택적) 🆕
     * 
     * 응답 정보 (17개 필드):
     * - 주문정보: 주문일자, 주문번호, 납기일자, 상태, 상태명
     * - 품목정보: 품번, 품명, 규격, 단위  
     * - 현장정보: 납품현장명, 수요처
     * - 수주정보: 수주수량, 판매단가, 할인율, 주문금액
     * - 출하정보: 출하수량, 미출하수량, 미출하금액
     * 
     * 정렬: 주문번호(주문일자+ACNO) 최신순
     * 
     * 예시: 
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?startDate=20240101&endDate=20240131
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?orderNumber=20240731-6
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?itemName1=가스관&itemName2=파이프&itemNameOperator=OR 🆕
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?spec1=63&spec2=75&specOperator=OR 🆕
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?siteName=대화도시가스 🆕
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?excludeCompleted=true 🆕 (완료 제외)
     * - GET /api/erp/shipments/order-shipment-detail/customer/9?statusFilter=4010020001 🆕 (수주진행만)
     */
    @GetMapping("/order-shipment-detail/customer/{custId}")
    public ResponseEntity<Page<OrderShipmentDetailResponse>> getOrderShipmentDetailByCustomer(
            @PathVariable Integer custId,
            @RequestParam(required = false) String shipDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String itemName1,
            @RequestParam(required = false) String itemName2,
            @RequestParam(required = false) String spec1,
            @RequestParam(required = false) String spec2,
            @RequestParam(required = false) String itemNumber,
            @RequestParam(defaultValue = "AND") String itemNameOperator,  // AND 또는 OR
            @RequestParam(defaultValue = "AND") String specOperator,      // AND 또는 OR
            @RequestParam(required = false) String siteName,
            @RequestParam(defaultValue = "false") boolean excludeCompleted,  // 완료 제외
            @RequestParam(required = false) String statusFilter,             // 특정 상태
            @PageableDefault(size = 20, sort = {"orderMastDate", "orderMastAcno"}, direction = Sort.Direction.DESC) Pageable pageable) {
        
        // 로그 생략 (파라미터 다수)
        
        Page<OrderShipmentDetailResponse> response = shipMastService.getOrderShipmentDetailByCustomer(
                custId, shipDate, startDate, endDate, orderNumber,
                itemName1, itemName2, spec1, spec2, itemNumber, itemNameOperator, specOperator, siteName, 
                excludeCompleted, statusFilter, pageable);
        return ResponseEntity.ok(response);
    }
} 