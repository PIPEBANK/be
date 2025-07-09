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

import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranResponse;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;
import com.pipebank.ordersystem.domain.web.temp.service.TempWebOrderTranService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/web/temp/order-tran")
@RequiredArgsConstructor
public class TempWebOrderTranController {

    private final TempWebOrderTranService tempWebOrderTranService;

    // 생성
    @PostMapping
    public ResponseEntity<TempWebOrderTranResponse> create(@RequestBody TempWebOrderTranCreateRequest request) {
        TempWebOrderTranResponse response = tempWebOrderTranService.create(request);
        return ResponseEntity.ok(response);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<TempWebOrderTranResponse>> findAll() {
        List<TempWebOrderTranResponse> responses = tempWebOrderTranService.findAll();
        return ResponseEntity.ok(responses);
    }

    // ID로 조회
    @GetMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}")
    public ResponseEntity<TempWebOrderTranResponse> findById(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq) {
        
        TempWebOrderTran.TempWebOrderTranId id = new TempWebOrderTran.TempWebOrderTranId(
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, orderTranSeq);
        
        return tempWebOrderTranService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 수정
    @PutMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}")
    public ResponseEntity<TempWebOrderTranResponse> update(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq,
            @RequestBody TempWebOrderTranCreateRequest request) {
        
        TempWebOrderTran.TempWebOrderTranId id = new TempWebOrderTran.TempWebOrderTranId(
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, orderTranSeq);
        
        return tempWebOrderTranService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 삭제
    @DeleteMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}")
    public ResponseEntity<Void> delete(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq) {
        
        TempWebOrderTran.TempWebOrderTranId id = new TempWebOrderTran.TempWebOrderTranId(
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, orderTranSeq);
        
        boolean deleted = tempWebOrderTranService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // send 상태를 true로 변경하여 WebOrderTran으로 변환
    @PatchMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}/send")
    public ResponseEntity<?> markAsSent(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq) {
        
        TempWebOrderTran.TempWebOrderTranId id = new TempWebOrderTran.TempWebOrderTranId(
                orderTranDate, orderTranSosok, orderTranUjcd, orderTranAcno, orderTranSeq);
        
        try {
            // 현재 임시저장 데이터 조회
            return tempWebOrderTranService.findById(id)
                    .map(tempOrderTran -> {
                        if (Boolean.TRUE.equals(tempOrderTran.getSend())) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "이미 전송된 주문상세입니다."));
                        }
                        
                        // send를 true로 변경하는 요청 생성
                        TempWebOrderTranCreateRequest updateRequest = TempWebOrderTranCreateRequest.builder()
                                .orderTranDate(tempOrderTran.getOrderTranDate())
                                .orderTranSosok(tempOrderTran.getOrderTranSosok())
                                .orderTranUjcd(tempOrderTran.getOrderTranUjcd())
                                .orderTranAcno(tempOrderTran.getOrderTranAcno()) // 기존 ACNO 사용
                                // orderTranSeq는 자동생성이므로 제거
                                .orderTranItemVer(tempOrderTran.getOrderTranItemVer())
                                .orderTranItem(tempOrderTran.getOrderTranItem())
                                .orderTranDeta(tempOrderTran.getOrderTranDeta())
                                .orderTranSpec(tempOrderTran.getOrderTranSpec())
                                .orderTranUnit(tempOrderTran.getOrderTranUnit())
                                .orderTranCalc(tempOrderTran.getOrderTranCalc())
                                .orderTranVdiv(tempOrderTran.getOrderTranVdiv())
                                .orderTranAdiv(tempOrderTran.getOrderTranAdiv())
                                .orderTranRate(tempOrderTran.getOrderTranRate())
                                .orderTranCnt(tempOrderTran.getOrderTranCnt())
                                .orderTranConvertWeight(tempOrderTran.getOrderTranConvertWeight())
                                .orderTranDcPer(tempOrderTran.getOrderTranDcPer())
                                .orderTranDcAmt(tempOrderTran.getOrderTranDcAmt())
                                .orderTranForiAmt(tempOrderTran.getOrderTranForiAmt())
                                .orderTranAmt(tempOrderTran.getOrderTranAmt())
                                .orderTranNet(tempOrderTran.getOrderTranNet())
                                .orderTranVat(tempOrderTran.getOrderTranVat())
                                .orderTranAdv(tempOrderTran.getOrderTranAdv())
                                .orderTranTot(tempOrderTran.getOrderTranTot())
                                .orderTranLrate(tempOrderTran.getOrderTranLrate())
                                .orderTranPrice(tempOrderTran.getOrderTranPrice())
                                .orderTranPrice2(tempOrderTran.getOrderTranPrice2())
                                .orderTranLdiv(tempOrderTran.getOrderTranLdiv())
                                .orderTranRemark(tempOrderTran.getOrderTranRemark())
                                .orderTranStau(tempOrderTran.getOrderTranStau())
                                // orderTranFdate, orderTranFuser, orderTranLdate, orderTranLuser는 자동생성
                                .orderTranWamt(tempOrderTran.getOrderTranWamt())
                                .send(true) // 핵심: send를 true로 변경
                                .build();
                        
                        // 업데이트 실행 (내부적으로 WebOrderTran 생성됨)
                        return tempWebOrderTranService.update(id, updateRequest)
                                .map(updatedOrderTran -> ResponseEntity.ok(Map.of(
                                        "message", "임시저장 주문상세가 정식 주문상세로 변환되었습니다.",
                                        "orderTranKey", updatedOrderTran.getOrderTranKey(),
                                        "tempOrderTran", updatedOrderTran
                                )))
                                .orElse(ResponseEntity.notFound().build());
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "주문상세 변환 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 