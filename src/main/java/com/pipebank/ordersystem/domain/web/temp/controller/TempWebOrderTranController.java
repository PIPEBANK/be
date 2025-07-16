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

    // ID로 조회 (🔥 Deprecated - tempOrderId 없이는 정확한 식별 불가능, by-order-number API 사용 권장)
    @GetMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}")
    public ResponseEntity<TempWebOrderTranResponse> findById(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq) {
        
        // 🔥 tempOrderId 없이는 정확한 조회 불가능
        return ResponseEntity.badRequest()
                .body(null); // 사용 불가 응답
    }

    // 수정 (🔥 Deprecated - tempOrderId 없이는 정확한 식별 불가능, by-order-number API 사용 권장)
    @PutMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}")
    public ResponseEntity<TempWebOrderTranResponse> update(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq,
            @RequestBody TempWebOrderTranCreateRequest request) {
        
        // 🔥 tempOrderId 없이는 정확한 수정 불가능
        return ResponseEntity.badRequest()
                .body(null); // 사용 불가 응답
    }

    // 삭제 (🔥 Deprecated - tempOrderId 없이는 정확한 식별 불가능, by-order-number API 사용 권장)
    @DeleteMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}")
    public ResponseEntity<Void> delete(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq) {
        
        // 🔥 tempOrderId 없이는 정확한 삭제 불가능
        return ResponseEntity.badRequest().build(); // 사용 불가 응답
    }

    // send 상태를 true로 변경하여 WebOrderTran으로 변환 (🔥 Deprecated - by-order-number API 사용 권장)
    @PatchMapping("/{orderTranDate}/{orderTranSosok}/{orderTranUjcd}/{orderTranAcno}/{orderTranSeq}/send")
    public ResponseEntity<?> markAsSent(
            @PathVariable String orderTranDate,
            @PathVariable Integer orderTranSosok,
            @PathVariable String orderTranUjcd,
            @PathVariable Integer orderTranAcno,
            @PathVariable Integer orderTranSeq) {
        
        // 🔥 tempOrderId 없이는 정확한 전송 처리 불가능
        return ResponseEntity.badRequest()
                .body(Map.of("error", "tempOrderId 없이는 정확한 식별이 불가능합니다. by-order-number API를 사용하세요."));
    }
} 