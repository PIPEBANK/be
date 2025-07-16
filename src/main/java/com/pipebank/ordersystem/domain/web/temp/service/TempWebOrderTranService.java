package com.pipebank.ordersystem.domain.web.temp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderTran;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranResponse;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;
import com.pipebank.ordersystem.domain.web.temp.repository.TempWebOrderMastRepository;
import com.pipebank.ordersystem.domain.web.temp.repository.TempWebOrderTranRepository;
import com.pipebank.ordersystem.global.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TempWebOrderTranService {

    private final TempWebOrderTranRepository tempWebOrderTranRepository;
    private final TempWebOrderMastRepository tempWebOrderMastRepository;
    private final WebOrderTranRepository webOrderTranRepository;

    // 생성
    @Transactional
    public TempWebOrderTranResponse create(TempWebOrderTranCreateRequest request) {
        // 현재 로그인한 사용자 ID 자동 설정
        String currentUserId = SecurityUtils.getCurrentMemberId();
        LocalDateTime now = LocalDateTime.now();
        
        // SEQ 자동 생성 (해당 주문의 시퀀스)
        Integer nextSeq = generateNextSeq(request.getOrderTranDate(), 
                                        request.getOrderTranSosok(), 
                                        request.getOrderTranUjcd(), 
                                        request.getOrderTranAcno(),
                                        request.getTempOrderId()); // 🔥 TempOrderId 추가
        
        TempWebOrderTran entity = TempWebOrderTran.builder()
                .orderTranDate(request.getOrderTranDate())
                .orderTranSosok(request.getOrderTranSosok())
                .orderTranUjcd(request.getOrderTranUjcd())
                .orderTranAcno(request.getOrderTranAcno()) // 🔥 외부에서 받아온 ACNO 사용
                .orderTranSeq(nextSeq) // 🔥 자동생성된 SEQ 사용
                .tempOrderId(request.getTempOrderId()) // 🔥 TempOrderId 설정
                .orderTranItemVer(request.getOrderTranItemVer())
                .orderTranItem(request.getOrderTranItem())
                .orderTranDeta(request.getOrderTranDeta())
                .orderTranSpec(request.getOrderTranSpec())
                .orderTranUnit(request.getOrderTranUnit())
                .orderTranCalc(request.getOrderTranCalc())
                .orderTranVdiv(request.getOrderTranVdiv())
                .orderTranAdiv(request.getOrderTranAdiv())
                .orderTranRate(request.getOrderTranRate())
                .orderTranCnt(request.getOrderTranCnt())
                .orderTranConvertWeight(request.getOrderTranConvertWeight())
                .orderTranDcPer(request.getOrderTranDcPer())
                .orderTranDcAmt(request.getOrderTranDcAmt())
                .orderTranForiAmt(request.getOrderTranForiAmt())
                .orderTranAmt(request.getOrderTranAmt())
                .orderTranNet(request.getOrderTranNet())
                .orderTranVat(request.getOrderTranVat())
                .orderTranAdv(request.getOrderTranAdv())
                .orderTranTot(request.getOrderTranTot())
                .orderTranLrate(request.getOrderTranLrate())
                .orderTranPrice(request.getOrderTranPrice())
                .orderTranPrice2(request.getOrderTranPrice2())
                .orderTranLdiv(request.getOrderTranLdiv())
                .orderTranRemark(request.getOrderTranRemark())
                .orderTranStau(request.getOrderTranStau())
                .orderTranFdate(now) // 🔥 자동생성된 현재 시간
                .orderTranFuser(currentUserId) // 🔥 자동생성된 현재 사용자
                .orderTranLdate(now) // 🔥 자동생성된 현재 시간
                .orderTranLuser(currentUserId) // 🔥 자동생성된 현재 사용자
                .orderTranWamt(request.getOrderTranWamt())
                .userId(currentUserId) // 🔥 자동으로 현재 사용자 ID 설정
                .send(request.getSend())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TempWebOrderTran saved = tempWebOrderTranRepository.save(entity);
        
        // 생성 시에도 send가 true면 WebOrderTran 생성
        if (Boolean.TRUE.equals(request.getSend())) {
            convertToWebOrderTran(saved);
        }
        
        return TempWebOrderTranResponse.from(saved);
    }

    // 전체 조회
    public List<TempWebOrderTranResponse> findAll() {
        return tempWebOrderTranRepository.findAll()
                .stream()
                .map(TempWebOrderTranResponse::from)
                .collect(Collectors.toList());
    }

    // ID로 조회
    public Optional<TempWebOrderTranResponse> findById(TempWebOrderTran.TempWebOrderTranId id) {
        return tempWebOrderTranRepository.findById(id)
                .map(TempWebOrderTranResponse::from);
    }

    // 수정
    @Transactional
    public Optional<TempWebOrderTranResponse> update(TempWebOrderTran.TempWebOrderTranId id, TempWebOrderTranCreateRequest request) {
        return tempWebOrderTranRepository.findById(id)
                .map(entity -> {
                    boolean wasSentBefore = Boolean.TRUE.equals(entity.getSend());
                    
                    // 현재 로그인한 사용자 ID 자동 설정
                    String currentUserId = SecurityUtils.getCurrentMemberId();
                    LocalDateTime now = LocalDateTime.now();
                    
                    TempWebOrderTran updated = TempWebOrderTran.builder()
                            .orderTranDate(request.getOrderTranDate())
                            .orderTranSosok(request.getOrderTranSosok())
                            .orderTranUjcd(request.getOrderTranUjcd())
                            .orderTranAcno(entity.getOrderTranAcno()) // 🔥 기존 entity의 ACNO 사용
                            .orderTranSeq(entity.getOrderTranSeq()) // 🔥 기존 entity의 SEQ 사용
                            .orderTranItemVer(request.getOrderTranItemVer())
                            .orderTranItem(request.getOrderTranItem())
                            .orderTranDeta(request.getOrderTranDeta())
                            .orderTranSpec(request.getOrderTranSpec())
                            .orderTranUnit(request.getOrderTranUnit())
                            .orderTranCalc(request.getOrderTranCalc())
                            .orderTranVdiv(request.getOrderTranVdiv())
                            .orderTranAdiv(request.getOrderTranAdiv())
                            .orderTranRate(request.getOrderTranRate())
                            .orderTranCnt(request.getOrderTranCnt())
                            .orderTranConvertWeight(request.getOrderTranConvertWeight())
                            .orderTranDcPer(request.getOrderTranDcPer())
                            .orderTranDcAmt(request.getOrderTranDcAmt())
                            .orderTranForiAmt(request.getOrderTranForiAmt())
                            .orderTranAmt(request.getOrderTranAmt())
                            .orderTranNet(request.getOrderTranNet())
                            .orderTranVat(request.getOrderTranVat())
                            .orderTranAdv(request.getOrderTranAdv())
                            .orderTranTot(request.getOrderTranTot())
                            .orderTranLrate(request.getOrderTranLrate())
                            .orderTranPrice(request.getOrderTranPrice())
                            .orderTranPrice2(request.getOrderTranPrice2())
                            .orderTranLdiv(request.getOrderTranLdiv())
                            .orderTranRemark(request.getOrderTranRemark())
                            .orderTranStau(request.getOrderTranStau())
                            .orderTranFdate(entity.getOrderTranFdate()) // 🔥 기존 생성일시 유지
                            .orderTranFuser(entity.getOrderTranFuser()) // 🔥 기존 생성자 유지
                            .orderTranLdate(now) // 🔥 자동생성된 수정 시간
                            .orderTranLuser(currentUserId) // 🔥 자동생성된 수정자
                            .orderTranWamt(request.getOrderTranWamt())
                            .userId(currentUserId) // 🔥 자동으로 현재 사용자 ID 설정
                            .send(request.getSend())
                            .createdAt(entity.getCreatedAt()) // 기존 생성일시 유지
                            .updatedAt(LocalDateTime.now()) // 수정일시 업데이트
                            .build();
                    
                    TempWebOrderTran saved = tempWebOrderTranRepository.save(updated);
                    
                    // send가 false → true로 변경되면 WebOrderTran으로 복사
                    if (Boolean.TRUE.equals(request.getSend()) && !wasSentBefore) {
                        convertToWebOrderTran(saved);
                    }
                    
                    return TempWebOrderTranResponse.from(saved);
                });
    }

    // 삭제
    @Transactional
    public boolean delete(TempWebOrderTran.TempWebOrderTranId id) {
        if (tempWebOrderTranRepository.existsById(id)) {
            tempWebOrderTranRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // TempWebOrderTran를 WebOrderTran으로 변환하여 저장
    @Transactional
    public void convertToWebOrderTran(TempWebOrderTran tempEntity) {
        // 이미 WebOrderTran에 동일한 복합키가 있는지 확인
        WebOrderTran.WebOrderTranId webId = new WebOrderTran.WebOrderTranId(
                tempEntity.getOrderTranDate(),
                tempEntity.getOrderTranSosok(),
                tempEntity.getOrderTranUjcd(),
                tempEntity.getOrderTranAcno(),
                tempEntity.getOrderTranSeq()
        );
        
        if (webOrderTranRepository.existsById(webId)) {
            throw new IllegalStateException("이미 해당 주문상세가 실제 테이블에 존재합니다: " + tempEntity.getOrderTranKey());
        }

        // TempWebOrderTran의 데이터를 WebOrderTran으로 복사
        WebOrderTran webEntity = WebOrderTran.builder()
                .orderTranDate(tempEntity.getOrderTranDate())
                .orderTranSosok(tempEntity.getOrderTranSosok())
                .orderTranUjcd(tempEntity.getOrderTranUjcd())
                .orderTranAcno(tempEntity.getOrderTranAcno())
                .orderTranSeq(tempEntity.getOrderTranSeq())
                .orderTranItemVer(tempEntity.getOrderTranItemVer())
                .orderTranItem(tempEntity.getOrderTranItem())
                .orderTranDeta(tempEntity.getOrderTranDeta())
                .orderTranSpec(tempEntity.getOrderTranSpec())
                .orderTranUnit(tempEntity.getOrderTranUnit())
                .orderTranCalc(tempEntity.getOrderTranCalc())
                .orderTranVdiv(tempEntity.getOrderTranVdiv())
                .orderTranAdiv(tempEntity.getOrderTranAdiv())
                .orderTranRate(tempEntity.getOrderTranRate())
                .orderTranCnt(tempEntity.getOrderTranCnt())
                .orderTranConvertWeight(tempEntity.getOrderTranConvertWeight())
                .orderTranDcPer(tempEntity.getOrderTranDcPer())
                .orderTranDcAmt(tempEntity.getOrderTranDcAmt())
                .orderTranForiAmt(tempEntity.getOrderTranForiAmt())
                .orderTranAmt(tempEntity.getOrderTranAmt())
                .orderTranNet(tempEntity.getOrderTranNet())
                .orderTranVat(tempEntity.getOrderTranVat())
                .orderTranAdv(tempEntity.getOrderTranAdv())
                .orderTranTot(tempEntity.getOrderTranTot())
                .orderTranLrate(tempEntity.getOrderTranLrate())
                .orderTranPrice(tempEntity.getOrderTranPrice())
                .orderTranPrice2(tempEntity.getOrderTranPrice2())
                .orderTranLdiv(tempEntity.getOrderTranLdiv())
                .orderTranRemark(tempEntity.getOrderTranRemark())
                .orderTranStau(tempEntity.getOrderTranStau())
                .orderTranFdate(tempEntity.getOrderTranFdate())
                .orderTranFuser(tempEntity.getOrderTranFuser())
                .orderTranLdate(tempEntity.getOrderTranLdate())
                .orderTranLuser(tempEntity.getOrderTranLuser())
                .orderTranWamt(tempEntity.getOrderTranWamt())
                .build();

        webOrderTranRepository.save(webEntity);
        
        System.out.println("✅ TempWebOrderTran → WebOrderTran 변환 완료: " + tempEntity.getOrderTranKey() + " (사용자: " + tempEntity.getUserId() + ")");
    }
    
    /**
     * SEQ 자동 생성 - 해당 주문의 시퀀스 번호 (🔥 TempOrderId 기준)
     */
    private Integer generateNextSeq(String orderDate, Integer sosok, String ujcd, Integer acno, Integer tempOrderId) {
        Integer maxSeq = tempWebOrderTranRepository.findMaxSeqByOrderKeyAndTempOrderId(orderDate, sosok, ujcd, acno, tempOrderId);
        return maxSeq + 1;
    }
} 