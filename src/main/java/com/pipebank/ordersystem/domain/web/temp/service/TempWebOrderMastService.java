package com.pipebank.ordersystem.domain.web.temp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderMast;
import com.pipebank.ordersystem.domain.web.order.entity.WebOrderTran;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastResponse;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;
import com.pipebank.ordersystem.domain.web.temp.repository.TempWebOrderMastRepository;
import com.pipebank.ordersystem.domain.web.temp.repository.TempWebOrderTranRepository;
import com.pipebank.ordersystem.domain.web.temp.service.TempWebOrderTranService;
import com.pipebank.ordersystem.global.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TempWebOrderMastService {

    private final TempWebOrderMastRepository tempWebOrderMastRepository;
    private final WebOrderMastRepository webOrderMastRepository;
    private final TempWebOrderTranService tempWebOrderTranService;
    private final TempWebOrderTranRepository tempWebOrderTranRepository;
    private final WebOrderTranRepository webOrderTranRepository;

    // 통합 생성 (Mast + Tran 한 번에 처리) - 새로 추가
    @Transactional
    public TempWebOrderMastResponse createWithTrans(TempWebOrderMastCreateRequest request) {
        // 1. 먼저 TempWebOrderMast 생성 (ACNO 자동 생성됨)
        TempWebOrderMastResponse mastResponse = create(request);
        
        // 2. orderTrans가 있으면 각각 생성
        if (request.getOrderTrans() != null && !request.getOrderTrans().isEmpty()) {
            for (TempWebOrderTranCreateRequest tranRequest : request.getOrderTrans()) {
                // Mast의 키 정보를 Tran에 자동 설정
                tranRequest.setOrderTranDate(mastResponse.getOrderMastDate());
                tranRequest.setOrderTranSosok(mastResponse.getOrderMastSosok());
                tranRequest.setOrderTranUjcd(mastResponse.getOrderMastUjcd());
                tranRequest.setOrderTranAcno(mastResponse.getOrderMastAcno());
                tranRequest.setSend(request.getSend()); // Mast와 동일한 send 상태
                
                // Tran 생성
                tempWebOrderTranService.create(tranRequest);
            }
        }
        
        return mastResponse;
    }

    // 생성
    @Transactional
    public TempWebOrderMastResponse create(TempWebOrderMastCreateRequest request) {
        // 현재 로그인한 사용자 ID 자동 설정
        String currentUserId = SecurityUtils.getCurrentMemberId();
        LocalDateTime now = LocalDateTime.now();
        
        // ACNO 자동 생성 (같은 날짜, 소속, 업장에 대한 시퀀스)
        Integer nextAcno = generateNextAcno(request.getOrderMastDate(), 
                                          request.getOrderMastSosok(), 
                                          request.getOrderMastUjcd());
        
        TempWebOrderMast entity = TempWebOrderMast.builder()
                .orderMastDate(request.getOrderMastDate())
                .orderMastSosok(request.getOrderMastSosok())
                .orderMastUjcd(request.getOrderMastUjcd())
                .orderMastAcno(nextAcno) // 🔥 자동생성된 ACNO 사용
                .orderMastCust(request.getOrderMastCust())
                .orderMastScust(request.getOrderMastScust())
                .orderMastSawon(request.getOrderMastSawon())
                .orderMastSawonBuse(request.getOrderMastSawonBuse())
                .orderMastOdate(request.getOrderMastOdate())
                .orderMastProject(request.getOrderMastProject())
                .orderMastRemark(request.getOrderMastRemark())
                .orderMastFdate(now) // 🔥 자동생성된 현재 시간
                .orderMastFuser(currentUserId) // 🔥 자동생성된 현재 사용자
                .orderMastLdate(now) // 🔥 자동생성된 현재 시간
                .orderMastLuser(currentUserId) // 🔥 자동생성된 현재 사용자
                .orderMastComaddr1(request.getOrderMastComaddr1())
                .orderMastComaddr2(request.getOrderMastComaddr2())
                .orderMastComname(request.getOrderMastComname())
                .orderMastComuname(request.getOrderMastComuname())
                .orderMastComutel(request.getOrderMastComutel())
                .orderMastReason(request.getOrderMastReason())
                .orderMastTcomdiv(request.getOrderMastTcomdiv())
                .orderMastCurrency(request.getOrderMastCurrency())
                .orderMastCurrencyPer(request.getOrderMastCurrencyPer())
                .orderMastSdiv(request.getOrderMastSdiv())
                .orderMastDcust(request.getOrderMastDcust())
                .orderMastIntype(request.getOrderMastIntype())
                .orderMastOtime(request.getOrderMastOtime())
                .userId(currentUserId) // 🔥 자동으로 현재 사용자 ID 설정
                .send(request.getSend())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TempWebOrderMast saved = tempWebOrderMastRepository.save(entity);
        
        // 생성 시에도 send가 true면 WebOrderMast 생성
        if (Boolean.TRUE.equals(request.getSend())) {
            convertToWebOrderMast(saved);
        }
        
        return TempWebOrderMastResponse.from(saved);
    }

    // 전체 조회
    public List<TempWebOrderMastResponse> findAll() {
        return tempWebOrderMastRepository.findAll()
                .stream()
                .map(TempWebOrderMastResponse::from)
                .collect(Collectors.toList());
    }

    // ID로 조회
    public Optional<TempWebOrderMastResponse> findById(TempWebOrderMast.TempWebOrderMastId id) {
        return tempWebOrderMastRepository.findById(id)
                .map(TempWebOrderMastResponse::from);
    }

    // 수정
    @Transactional
    public Optional<TempWebOrderMastResponse> update(TempWebOrderMast.TempWebOrderMastId id, TempWebOrderMastCreateRequest request) {
        return tempWebOrderMastRepository.findById(id)
                .map(entity -> {
                    boolean wasSentBefore = Boolean.TRUE.equals(entity.getSend());
                    
                    // 현재 로그인한 사용자 ID 자동 설정
                    String currentUserId = SecurityUtils.getCurrentMemberId();
                    LocalDateTime now = LocalDateTime.now();
                    
                    // 기존 entity의 모든 필드 업데이트
                    TempWebOrderMast updated = TempWebOrderMast.builder()
                            .orderMastDate(request.getOrderMastDate())
                            .orderMastSosok(request.getOrderMastSosok())
                            .orderMastUjcd(request.getOrderMastUjcd())
                            .orderMastAcno(entity.getOrderMastAcno()) // 🔥 기존 entity의 ACNO 사용 (자동생성이므로 변경 불가)
                            .orderMastCust(request.getOrderMastCust())
                            .orderMastScust(request.getOrderMastScust())
                            .orderMastSawon(request.getOrderMastSawon())
                            .orderMastSawonBuse(request.getOrderMastSawonBuse())
                            .orderMastOdate(request.getOrderMastOdate())
                            .orderMastProject(request.getOrderMastProject())
                            .orderMastRemark(request.getOrderMastRemark())
                            .orderMastFdate(entity.getOrderMastFdate()) // 🔥 기존 생성일시 유지
                            .orderMastFuser(entity.getOrderMastFuser()) // 🔥 기존 생성자 유지
                            .orderMastLdate(now) // 🔥 자동생성된 수정 시간
                            .orderMastLuser(currentUserId) // 🔥 자동생성된 수정자
                            .orderMastComaddr1(request.getOrderMastComaddr1())
                            .orderMastComaddr2(request.getOrderMastComaddr2())
                            .orderMastComname(request.getOrderMastComname())
                            .orderMastComuname(request.getOrderMastComuname())
                            .orderMastComutel(request.getOrderMastComutel())
                            .orderMastReason(request.getOrderMastReason())
                            .orderMastTcomdiv(request.getOrderMastTcomdiv())
                            .orderMastCurrency(request.getOrderMastCurrency())
                            .orderMastCurrencyPer(request.getOrderMastCurrencyPer())
                            .orderMastSdiv(request.getOrderMastSdiv())
                            .orderMastDcust(request.getOrderMastDcust())
                            .orderMastIntype(request.getOrderMastIntype())
                            .orderMastOtime(request.getOrderMastOtime())
                            .userId(currentUserId) // 🔥 자동으로 현재 사용자 ID 설정
                            .send(request.getSend())
                            .createdAt(entity.getCreatedAt()) // 기존 생성일시 유지
                            .updatedAt(LocalDateTime.now()) // 수정일시 업데이트
                            .build();
                    
                    TempWebOrderMast saved = tempWebOrderMastRepository.save(updated);
                    
                    // send가 false → true로 변경되면 WebOrderMast로 복사
                    if (Boolean.TRUE.equals(request.getSend()) && !wasSentBefore) {
                        convertToWebOrderMast(saved);
                    }
                    
                    return TempWebOrderMastResponse.from(saved);
                });
    }

    // 삭제
    @Transactional
    public boolean delete(TempWebOrderMast.TempWebOrderMastId id) {
        if (tempWebOrderMastRepository.existsById(id)) {
            tempWebOrderMastRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // TempWebOrderMast를 WebOrderMast로 변환하여 저장
    @Transactional
    public void convertToWebOrderMast(TempWebOrderMast tempEntity) {
        // 이미 WebOrderMast에 동일한 복합키가 있는지 확인
        WebOrderMast.WebOrderMastId webId = new WebOrderMast.WebOrderMastId(
                tempEntity.getOrderMastDate(),
                tempEntity.getOrderMastSosok(),
                tempEntity.getOrderMastUjcd(),
                tempEntity.getOrderMastAcno()
        );
        
        if (webOrderMastRepository.existsById(webId)) {
            throw new IllegalStateException("이미 해당 주문이 실제 테이블에 존재합니다: " + tempEntity.getOrderKey());
        }

        // 1. TempWebOrderMast의 데이터를 WebOrderMast로 복사
        WebOrderMast webEntity = WebOrderMast.builder()
                .orderMastDate(tempEntity.getOrderMastDate())
                .orderMastSosok(tempEntity.getOrderMastSosok())
                .orderMastUjcd(tempEntity.getOrderMastUjcd())
                .orderMastAcno(tempEntity.getOrderMastAcno())
                .orderMastCust(tempEntity.getOrderMastCust())
                .orderMastScust(tempEntity.getOrderMastScust())
                .orderMastSawon(tempEntity.getOrderMastSawon())
                .orderMastSawonBuse(tempEntity.getOrderMastSawonBuse())
                .orderMastOdate(tempEntity.getOrderMastOdate())
                .orderMastProject(tempEntity.getOrderMastProject())
                .orderMastRemark(tempEntity.getOrderMastRemark())
                .orderMastFdate(tempEntity.getOrderMastFdate())
                .orderMastFuser(tempEntity.getOrderMastFuser())
                .orderMastLdate(tempEntity.getOrderMastLdate())
                .orderMastLuser(tempEntity.getOrderMastLuser())
                .orderMastComaddr1(tempEntity.getOrderMastComaddr1())
                .orderMastComaddr2(tempEntity.getOrderMastComaddr2())
                .orderMastComname(tempEntity.getOrderMastComname())
                .orderMastComuname(tempEntity.getOrderMastComuname())
                .orderMastComutel(tempEntity.getOrderMastComutel())
                .orderMastReason(tempEntity.getOrderMastReason())
                .orderMastTcomdiv(tempEntity.getOrderMastTcomdiv())
                .orderMastCurrency(tempEntity.getOrderMastCurrency())
                .orderMastCurrencyPer(tempEntity.getOrderMastCurrencyPer())
                .orderMastSdiv(tempEntity.getOrderMastSdiv())
                .orderMastDcust(tempEntity.getOrderMastDcust())
                .orderMastIntype(tempEntity.getOrderMastIntype())
                .orderMastOtime(tempEntity.getOrderMastOtime())
                .build();

        webOrderMastRepository.save(webEntity);
        
        // 2. 관련된 TempWebOrderTran들을 WebOrderTran으로 복사
        List<TempWebOrderTran> tempTrans = tempWebOrderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                tempEntity.getOrderMastDate(),
                tempEntity.getOrderMastSosok(),
                tempEntity.getOrderMastUjcd(),
                tempEntity.getOrderMastAcno()
        );
        
        for (TempWebOrderTran tempTran : tempTrans) {
            WebOrderTran webTran = WebOrderTran.builder()
                    .orderTranDate(tempTran.getOrderTranDate())
                    .orderTranSosok(tempTran.getOrderTranSosok())
                    .orderTranUjcd(tempTran.getOrderTranUjcd())
                    .orderTranAcno(tempTran.getOrderTranAcno())
                    .orderTranSeq(tempTran.getOrderTranSeq())
                    .orderTranItemVer(tempTran.getOrderTranItemVer())
                    .orderTranItem(tempTran.getOrderTranItem())
                    .orderTranDeta(tempTran.getOrderTranDeta())
                    .orderTranSpec(tempTran.getOrderTranSpec())
                    .orderTranUnit(tempTran.getOrderTranUnit())
                    .orderTranCalc(tempTran.getOrderTranCalc())
                    .orderTranVdiv(tempTran.getOrderTranVdiv())
                    .orderTranAdiv(tempTran.getOrderTranAdiv())
                    .orderTranRate(tempTran.getOrderTranRate())
                    .orderTranCnt(tempTran.getOrderTranCnt())
                    .orderTranConvertWeight(tempTran.getOrderTranConvertWeight())
                    .orderTranDcPer(tempTran.getOrderTranDcPer())
                    .orderTranDcAmt(tempTran.getOrderTranDcAmt())
                    .orderTranForiAmt(tempTran.getOrderTranForiAmt())
                    .orderTranAmt(tempTran.getOrderTranAmt())
                    .orderTranNet(tempTran.getOrderTranNet())
                    .orderTranVat(tempTran.getOrderTranVat())
                    .orderTranAdv(tempTran.getOrderTranAdv())
                    .orderTranTot(tempTran.getOrderTranTot())
                    .orderTranLrate(tempTran.getOrderTranLrate())
                    .orderTranPrice(tempTran.getOrderTranPrice())
                    .orderTranPrice2(tempTran.getOrderTranPrice2())
                    .orderTranLdiv(tempTran.getOrderTranLdiv())
                    .orderTranRemark(tempTran.getOrderTranRemark())
                    .orderTranStau(tempTran.getOrderTranStau())
                    .orderTranFdate(tempTran.getOrderTranFdate())
                    .orderTranFuser(tempTran.getOrderTranFuser())
                    .orderTranLdate(tempTran.getOrderTranLdate())
                    .orderTranLuser(tempTran.getOrderTranLuser())
                    .orderTranWamt(tempTran.getOrderTranWamt())
                    .build();
            
            webOrderTranRepository.save(webTran);
        }
        
        System.out.println("✅ TempWebOrderMast → WebOrderMast 변환 완료: " + tempEntity.getOrderKey() + " (사용자: " + tempEntity.getUserId() + ")");
        System.out.println("✅ TempWebOrderTran → WebOrderTran 변환 완료: " + tempTrans.size() + "개 항목");
    }
    
    /**
     * ACNO 자동 생성 - 같은 날짜, 소속, 업장에 대한 시퀀스 번호
     */
    private Integer generateNextAcno(String orderDate, Integer sosok, String ujcd) {
        Integer maxAcno = tempWebOrderMastRepository.findMaxAcnoByDateAndSosokAndUjcd(orderDate, sosok, ujcd);
        return maxAcno + 1;
    }
} 