package com.pipebank.ordersystem.domain.web.temp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastResponse;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import com.pipebank.ordersystem.domain.web.temp.service.TempWebOrderMastService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/web/temp/order-mast")
@RequiredArgsConstructor
public class TempWebOrderMastController {

    private final TempWebOrderMastService tempWebOrderMastService;

    // 생성
    @PostMapping
    public ResponseEntity<TempWebOrderMastResponse> create(@RequestBody TempWebOrderMastCreateRequest request) {
        TempWebOrderMastResponse response = tempWebOrderMastService.create(request);
        return ResponseEntity.ok(response);
    }

    // 통합 생성 (Mast + Tran 한 번에 처리) - 새로 추가
    @PostMapping("/with-trans")
    public ResponseEntity<TempWebOrderMastResponse> createWithTrans(@RequestBody TempWebOrderMastCreateRequest request) {
        TempWebOrderMastResponse response = tempWebOrderMastService.createWithTrans(request);
        return ResponseEntity.ok(response);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<TempWebOrderMastResponse>> findAll() {
        List<TempWebOrderMastResponse> responses = tempWebOrderMastService.findAll();
        return ResponseEntity.ok(responses);
    }

    // ID로 조회
    @GetMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}")
    public ResponseEntity<TempWebOrderMastResponse> findById(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        return tempWebOrderMastService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 수정
    @PutMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}")
    public ResponseEntity<TempWebOrderMastResponse> update(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno,
            @RequestBody TempWebOrderMastCreateRequest request) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        return tempWebOrderMastService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 삭제
    @DeleteMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}")
    public ResponseEntity<Void> delete(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        boolean deleted = tempWebOrderMastService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // send 상태를 true로 변경하여 WebOrderMast로 변환
    @PatchMapping("/{orderMastDate}/{orderMastSosok}/{orderMastUjcd}/{orderMastAcno}/send")
    public ResponseEntity<?> markAsSent(
            @PathVariable String orderMastDate,
            @PathVariable Integer orderMastSosok,
            @PathVariable String orderMastUjcd,
            @PathVariable Integer orderMastAcno) {
        
        TempWebOrderMast.TempWebOrderMastId id = new TempWebOrderMast.TempWebOrderMastId(
                orderMastDate, orderMastSosok, orderMastUjcd, orderMastAcno);
        
        try {
            // 현재 임시저장 데이터 조회
            return tempWebOrderMastService.findById(id)
                    .map(tempOrder -> {
                        if (Boolean.TRUE.equals(tempOrder.getSend())) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "이미 전송된 주문입니다."));
                        }
                        
                        // send를 true로 변경하는 요청 생성
                        TempWebOrderMastCreateRequest updateRequest = TempWebOrderMastCreateRequest.builder()
                                .orderMastDate(tempOrder.getOrderMastDate())
                                .orderMastSosok(tempOrder.getOrderMastSosok())
                                .orderMastUjcd(tempOrder.getOrderMastUjcd())
                                // orderMastAcno는 자동생성이므로 제거
                                .orderMastCust(tempOrder.getOrderMastCust())
                                .orderMastScust(tempOrder.getOrderMastScust())
                                .orderMastSawon(tempOrder.getOrderMastSawon())
                                .orderMastSawonBuse(tempOrder.getOrderMastSawonBuse())
                                .orderMastOdate(tempOrder.getOrderMastOdate())
                                .orderMastProject(tempOrder.getOrderMastProject())
                                .orderMastRemark(tempOrder.getOrderMastRemark())
                                // 날짜/사용자 필드들은 자동생성되므로 제거
                                .orderMastComaddr1(tempOrder.getOrderMastComaddr1())
                                .orderMastComaddr2(tempOrder.getOrderMastComaddr2())
                                .orderMastComname(tempOrder.getOrderMastComname())
                                .orderMastComuname(tempOrder.getOrderMastComuname())
                                .orderMastComutel(tempOrder.getOrderMastComutel())
                                .orderMastReason(tempOrder.getOrderMastReason())
                                .orderMastTcomdiv(tempOrder.getOrderMastTcomdiv())
                                .orderMastCurrency(tempOrder.getOrderMastCurrency())
                                .orderMastCurrencyPer(tempOrder.getOrderMastCurrencyPer())
                                .orderMastSdiv(tempOrder.getOrderMastSdiv())
                                .orderMastDcust(tempOrder.getOrderMastDcust())
                                .orderMastIntype(tempOrder.getOrderMastIntype())
                                .orderMastOtime(tempOrder.getOrderMastOtime())
                                .send(true) // 핵심: send를 true로 변경
                                .build();
                        
                        // 업데이트 실행 (내부적으로 WebOrderMast 생성됨)
                        return tempWebOrderMastService.update(id, updateRequest)
                                .map(updatedOrder -> ResponseEntity.ok(Map.of(
                                        "message", "임시저장 주문이 정식 주문으로 변환되었습니다.",
                                        "orderKey", updatedOrder.getOrderKey(),
                                        "tempOrder", updatedOrder
                                )))
                                .orElse(ResponseEntity.notFound().build());
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "주문 변환 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 