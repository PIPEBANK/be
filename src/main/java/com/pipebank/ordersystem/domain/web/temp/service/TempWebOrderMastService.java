package com.pipebank.ordersystem.domain.web.temp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.web.order.entity.WebOrderMast;
import com.pipebank.ordersystem.domain.web.order.entity.WebOrderTran;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.pipebank.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastListResponse;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderMastResponse;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranCreateRequest;
import com.pipebank.ordersystem.domain.web.temp.dto.TempWebOrderTranResponse;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderMast;
import com.pipebank.ordersystem.domain.web.temp.entity.TempWebOrderTran;
import com.pipebank.ordersystem.domain.web.temp.repository.TempWebOrderMastRepository;
import com.pipebank.ordersystem.domain.web.temp.repository.TempWebOrderTranRepository;
import com.pipebank.ordersystem.global.security.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TempWebOrderMastService {

    private final TempWebOrderMastRepository tempWebOrderMastRepository;
    private final WebOrderMastRepository webOrderMastRepository;
    private final TempWebOrderTranService tempWebOrderTranService;
    private final TempWebOrderTranRepository tempWebOrderTranRepository;
    private final WebOrderTranRepository webOrderTranRepository;
    // ERP DB 저장을 위한 서비스 추가
    private final com.pipebank.ordersystem.domain.erp.service.OrderMastService erpOrderMastService;
    private final com.pipebank.ordersystem.domain.erp.service.OrderTranService erpOrderTranService;
    // ERP ItemCode 조회를 위한 서비스 추가
    private final com.pipebank.ordersystem.domain.erp.service.ItemCodeService itemCodeService;
    private final com.pipebank.ordersystem.domain.erp.repository.ItemCodeRepository itemCodeRepository;

    // 통합 생성 (Mast + Tran 한 번에 처리) - 새로 추가
    @Transactional
    public TempWebOrderMastResponse createWithTrans(TempWebOrderMastCreateRequest request) {
        // 1. 먼저 TempWebOrderMast 생성 (ACNO 자동 생성됨) - send 변환은 하지 않음
        TempWebOrderMastResponse mastResponse = createWithoutConversion(request);
        
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
        
        // 3. 🔥 모든 TempWebOrderTran 저장이 완료된 후, send=true이면 변환 실행
        if (Boolean.TRUE.equals(request.getSend())) {
            TempWebOrderMast.TempWebOrderMastId tempId = new TempWebOrderMast.TempWebOrderMastId(
                    mastResponse.getOrderMastDate(),
                    mastResponse.getOrderMastSosok(),
                    mastResponse.getOrderMastUjcd(),
                    mastResponse.getOrderMastAcno()
            );
            
            tempWebOrderMastRepository.findById(tempId).ifPresent(this::convertToWebOrderMast);
        }
        
        return mastResponse;
    }

    // 🔥 변환 없이 생성만 하는 메서드 (새로 추가)
    @Transactional
    public TempWebOrderMastResponse createWithoutConversion(TempWebOrderMastCreateRequest request) {
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
        
        // 🔥 변환은 하지 않고 저장만 함
        return TempWebOrderMastResponse.from(saved);
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

    /**
     * 주문번호로 조회 (OrderTran 포함 통합 조회)
     * @param orderNumber 주문번호 (형식: "YYYYMMDD-숫자", 예: "20250710-1")
     * @return TempWebOrderMastResponse (OrderTran 리스트 포함)
     */
    public Optional<TempWebOrderMastResponse> findByOrderNumber(String orderNumber) {
        // orderNumber를 DATE와 ACNO로 분리
        String[] parts = orderNumber.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. 올바른 형식: YYYYMMDD-숫자 (예: 20250710-1)");
        }
        
        String orderDate = parts[0];
        Integer acno;
        try {
            acno = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. ACNO는 숫자여야 합니다.");
        }
        
        // 1. OrderMast 조회
        return tempWebOrderMastRepository.findByOrderNumber(orderDate, acno)
                .map(orderMast -> {
                    // 2. 관련된 OrderTran들 조회 (모든 소속/업장에서 검색)
                    List<TempWebOrderTran> orderTrans = tempWebOrderTranRepository
                            .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                                    orderMast.getOrderMastDate(),
                                    orderMast.getOrderMastSosok(),
                                    orderMast.getOrderMastUjcd(),
                                    orderMast.getOrderMastAcno()
                            );
                    
                    // 3. OrderTran을 DTO로 변환 (ItemCode 정보 포함)
                    List<TempWebOrderTranResponse> orderTranResponses = orderTrans.stream()
                            .map(orderTran -> {
                                // ERP DB에서 ItemCode 정보 조회
                                String itemCodeNum = null;
                                if (orderTran.getOrderTranItem() != null) {
                                    itemCodeNum = itemCodeRepository.findById(orderTran.getOrderTranItem())
                                            .map(itemCode -> itemCode.getItemCodeNum())
                                            .orElse(null);
                                }
                                
                                // ItemCode 정보와 함께 Response 생성
                                return TempWebOrderTranResponse.fromWithItemCode(orderTran, itemCodeNum);
                            })
                            .collect(Collectors.toList());
                    
                    // 4. OrderMast + OrderTran 통합 응답 생성
                    return TempWebOrderMastResponse.fromWithOrderTrans(orderMast, orderTranResponses);
                });
    }

    /**
     * 거래처별 임시저장 주문 목록 조회 (페이징 + 필터링)
     * - send = false인 것만 조회
     * - 주문번호, 작성자, 현장명, 주문일자만 응답
     */
    public Page<TempWebOrderMastListResponse> getTempOrdersByCustomerWithFilters(
            Integer custId, String orderDate, String startDate, String endDate,
            String orderNumber, String userId, String comName, Pageable pageable) {
        
        Page<TempWebOrderMast> tempOrders = tempWebOrderMastRepository.findByCustomerWithFilters(
                custId, orderDate, startDate, endDate, orderNumber, userId, comName, pageable);
        
        return tempOrders.map(TempWebOrderMastListResponse::from);
    }

    /**
     * 거래처별 임시저장 주문 기본 목록 조회 (페이징만)
     * - send = false인 것만 조회
     */
    public Page<TempWebOrderMastListResponse> getTempOrdersByCustomer(Integer custId, Pageable pageable) {
        Page<TempWebOrderMast> tempOrders = tempWebOrderMastRepository.findByOrderMastCustAndSendFalse(custId, pageable);
        return tempOrders.map(TempWebOrderMastListResponse::from);
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

    /**
     * 주문번호로 통합 수정 (OrderMast + OrderTran 한 번에 처리)
     * @param orderNumber 주문번호 (형식: "YYYYMMDD-숫자", 예: "20250710-1")
     * @param request 수정 요청 데이터
     * @return 수정된 TempWebOrderMastResponse
     */
    @Transactional
    public Optional<TempWebOrderMastResponse> updateWithTransByOrderNumber(String orderNumber, TempWebOrderMastCreateRequest request) {
        // orderNumber를 DATE와 ACNO로 분리
        String[] parts = orderNumber.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. 올바른 형식: YYYYMMDD-숫자 (예: 20250710-1)");
        }
        
        String orderDate = parts[0];
        Integer acno;
        try {
            acno = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 주문번호 형식입니다. ACNO는 숫자여야 합니다.");
        }
        
        // 1. 기존 OrderMast 조회
        return tempWebOrderMastRepository.findByOrderNumber(orderDate, acno)
                .map(existingOrderMast -> {
                    String existingDate = existingOrderMast.getOrderMastDate();
                    String requestDate = request.getOrderMastDate();
                    
                    // 2. 날짜 비교
                    if (!requestDate.equals(existingDate)) {
                        // 🔥 날짜가 다르면: 기존 데이터 삭제 후 새로 생성
                        log.info("주문 날짜 변경 감지: {} → {}, 기존 데이터 삭제 후 새로 생성", existingDate, requestDate);
                        
                        // 2-1. 기존 데이터 완전 삭제
                        List<TempWebOrderTran> existingOrderTrans = tempWebOrderTranRepository
                                .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                                        existingOrderMast.getOrderMastDate(),
                                        existingOrderMast.getOrderMastSosok(),
                                        existingOrderMast.getOrderMastUjcd(),
                                        existingOrderMast.getOrderMastAcno()
                                );
                        
                        tempWebOrderTranRepository.deleteAll(existingOrderTrans);
                        tempWebOrderMastRepository.delete(existingOrderMast);
                        
                        // 2-2. 새로운 날짜로 새로 생성
                        TempWebOrderMastResponse newMastResponse = createWithoutConversion(request);
                        
                        // 2-3. 새로운 OrderTran들 생성
                        List<TempWebOrderTranResponse> newOrderTrans = new ArrayList<>();
                        if (request.getOrderTrans() != null && !request.getOrderTrans().isEmpty()) {
                            for (TempWebOrderTranCreateRequest tranRequest : request.getOrderTrans()) {
                                // 새로운 Mast의 키 정보를 Tran에 자동 설정
                                tranRequest.setOrderTranDate(newMastResponse.getOrderMastDate());
                                tranRequest.setOrderTranSosok(newMastResponse.getOrderMastSosok());
                                tranRequest.setOrderTranUjcd(newMastResponse.getOrderMastUjcd());
                                tranRequest.setOrderTranAcno(newMastResponse.getOrderMastAcno());
                                tranRequest.setSend(request.getSend()); // Mast와 동일한 send 상태
                                
                                // Tran 생성
                                TempWebOrderTranResponse tranResponse = tempWebOrderTranService.create(tranRequest);
                                newOrderTrans.add(tranResponse);
                            }
                        }
                        
                        // 2-4. 🔥 send=true이면 변환 실행 (새로운 데이터로)
                        log.info("🔍 PUT API(날짜변경) - Send 값 확인: {}", request.getSend());
                        if (Boolean.TRUE.equals(request.getSend())) {
                            log.info("🔍 PUT API(날짜변경) - convertToWebOrderMast 실행 시작");
                            TempWebOrderMast.TempWebOrderMastId newId = new TempWebOrderMast.TempWebOrderMastId(
                                    newMastResponse.getOrderMastDate(),
                                    newMastResponse.getOrderMastSosok(),
                                    newMastResponse.getOrderMastUjcd(),
                                    newMastResponse.getOrderMastAcno()
                            );
                            
                            log.info("🔍 PUT API(날짜변경) - TempWebOrderMast 조회 시도: {}", newId);
                            Optional<TempWebOrderMast> foundEntity = tempWebOrderMastRepository.findById(newId);
                            if (foundEntity.isPresent()) {
                                log.info("🔍 PUT API(날짜변경) - TempWebOrderMast 조회 성공, convertToWebOrderMast 호출");
                                convertToWebOrderMast(foundEntity.get());
                                log.info("✅ PUT API(날짜변경) - convertToWebOrderMast 완료");
                            } else {
                                log.error("❌ PUT API(날짜변경) - TempWebOrderMast 조회 실패: {}", newId);
                            }
                        } else {
                            log.info("ℹ️ PUT API(날짜변경) - send=false이므로 변환 생략");
                        }
                        
                        // 2-5. 통합 응답 생성 (새로운 데이터로)
                        TempWebOrderMast.TempWebOrderMastId newId = new TempWebOrderMast.TempWebOrderMastId(
                                newMastResponse.getOrderMastDate(),
                                newMastResponse.getOrderMastSosok(),
                                newMastResponse.getOrderMastUjcd(),
                                newMastResponse.getOrderMastAcno()
                        );
                        
                        return TempWebOrderMastResponse.fromWithOrderTrans(
                                tempWebOrderMastRepository.findById(newId).orElseThrow(),
                                newOrderTrans
                        );
                        
                    } else {
                        // 🔥 날짜가 같으면: 기존 수정 로직 사용
                        log.info("주문 날짜 동일: {}, 기존 데이터 수정", existingDate);
                        
                        // 3-1. 기존 OrderTran들 삭제
                        List<TempWebOrderTran> existingOrderTrans = tempWebOrderTranRepository
                                .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                                        existingOrderMast.getOrderMastDate(),
                                        existingOrderMast.getOrderMastSosok(),
                                        existingOrderMast.getOrderMastUjcd(),
                                        existingOrderMast.getOrderMastAcno()
                                );
                        
                        // 기존 OrderTran 삭제
                        tempWebOrderTranRepository.deleteAll(existingOrderTrans);
                        
                        // 3-2. OrderMast 수정 (기존 ID 사용)
                        TempWebOrderMast.TempWebOrderMastId existingId = new TempWebOrderMast.TempWebOrderMastId(
                                existingOrderMast.getOrderMastDate(),
                                existingOrderMast.getOrderMastSosok(),
                                existingOrderMast.getOrderMastUjcd(),
                                existingOrderMast.getOrderMastAcno()
                        );
                        
                        // 🔥 send 값을 임시로 false로 설정하여 update에서 변환 방지
                        TempWebOrderMastCreateRequest tempRequest = new TempWebOrderMastCreateRequest();
                        // 모든 필드 복사
                        tempRequest.setOrderMastDate(request.getOrderMastDate());
                        tempRequest.setOrderMastSosok(request.getOrderMastSosok());
                        tempRequest.setOrderMastUjcd(request.getOrderMastUjcd());
                        tempRequest.setOrderMastCust(request.getOrderMastCust());
                        tempRequest.setOrderMastScust(request.getOrderMastScust());
                        tempRequest.setOrderMastSawon(request.getOrderMastSawon());
                        tempRequest.setOrderMastSawonBuse(request.getOrderMastSawonBuse());
                        tempRequest.setOrderMastOdate(request.getOrderMastOdate());
                        tempRequest.setOrderMastProject(request.getOrderMastProject());
                        tempRequest.setOrderMastRemark(request.getOrderMastRemark());
                        tempRequest.setOrderMastComaddr1(request.getOrderMastComaddr1());
                        tempRequest.setOrderMastComaddr2(request.getOrderMastComaddr2());
                        tempRequest.setOrderMastComname(request.getOrderMastComname());
                        tempRequest.setOrderMastComuname(request.getOrderMastComuname());
                        tempRequest.setOrderMastComutel(request.getOrderMastComutel());
                        tempRequest.setOrderMastReason(request.getOrderMastReason());
                        tempRequest.setOrderMastTcomdiv(request.getOrderMastTcomdiv());
                        tempRequest.setOrderMastCurrency(request.getOrderMastCurrency());
                        tempRequest.setOrderMastCurrencyPer(request.getOrderMastCurrencyPer());
                        tempRequest.setOrderMastSdiv(request.getOrderMastSdiv());
                        tempRequest.setOrderMastDcust(request.getOrderMastDcust());
                        tempRequest.setOrderMastIntype(request.getOrderMastIntype());
                        tempRequest.setOrderMastOtime(request.getOrderMastOtime());
                        tempRequest.setSend(false); // 🔥 임시로 false 설정
                        
                        Optional<TempWebOrderMastResponse> updatedMast = update(existingId, tempRequest);
                        
                        if (updatedMast.isEmpty()) {
                            throw new IllegalStateException("OrderMast 수정에 실패했습니다.");
                        }
                        
                        TempWebOrderMastResponse mastResponse = updatedMast.get();
                        
                        // 3-3. 새로운 OrderTran들 생성
                        List<TempWebOrderTranResponse> newOrderTrans = new ArrayList<>();
                        log.info("🔍 PUT API - OrderTran 생성 시작: {}개 요청", request.getOrderTrans() != null ? request.getOrderTrans().size() : 0);
                        if (request.getOrderTrans() != null && !request.getOrderTrans().isEmpty()) {
                            for (int i = 0; i < request.getOrderTrans().size(); i++) {
                                TempWebOrderTranCreateRequest tranRequest = request.getOrderTrans().get(i);
                                log.info("🔍 PUT API - [{}] OrderTran 생성 시도: ITEM={}, DETA={}", i+1, tranRequest.getOrderTranItem(), tranRequest.getOrderTranDeta());
                                
                                // Mast의 키 정보를 Tran에 자동 설정
                                tranRequest.setOrderTranDate(mastResponse.getOrderMastDate());
                                tranRequest.setOrderTranSosok(mastResponse.getOrderMastSosok());
                                tranRequest.setOrderTranUjcd(mastResponse.getOrderMastUjcd());
                                tranRequest.setOrderTranAcno(mastResponse.getOrderMastAcno());
                                tranRequest.setSend(request.getSend()); // Mast와 동일한 send 상태
                                
                                log.info("🔍 PUT API - [{}] OrderTran 키 설정: {}-{}-{}-{}", i+1, 
                                        tranRequest.getOrderTranDate(), tranRequest.getOrderTranSosok(), 
                                        tranRequest.getOrderTranUjcd(), tranRequest.getOrderTranAcno());
                                
                                // Tran 생성
                                TempWebOrderTranResponse tranResponse = tempWebOrderTranService.create(tranRequest);
                                newOrderTrans.add(tranResponse);
                                log.info("✅ PUT API - [{}] OrderTran 생성 완료: {}", i+1, tranResponse.getOrderTranKey());
                            }
                        }
                        log.info("✅ PUT API - OrderTran 생성 완료: 총 {}개", newOrderTrans.size());
                        
                        // 3-4. 🔥 모든 TempWebOrderTran 생성 완료 후, send=true이면 변환 실행
                        log.info("🔍 PUT API - Send 값 확인: {}", request.getSend());
                        if (Boolean.TRUE.equals(request.getSend())) {
                            log.info("🔥 PUT API - 모든 TempWebOrderTran 생성 완료 후 convertToWebOrderMast 실행");
                            TempWebOrderMast.TempWebOrderMastId tempId = new TempWebOrderMast.TempWebOrderMastId(
                                    mastResponse.getOrderMastDate(),
                                    mastResponse.getOrderMastSosok(),
                                    mastResponse.getOrderMastUjcd(),
                                    mastResponse.getOrderMastAcno()
                            );
                            
                            log.info("🔍 PUT API - TempWebOrderMast 조회 시도: {}", tempId);
                            Optional<TempWebOrderMast> foundEntity = tempWebOrderMastRepository.findById(tempId);
                            if (foundEntity.isPresent()) {
                                // 🔥 먼저 TempWebOrderMast의 send 값을 true로 업데이트
                                log.info("🔍 PUT API - TempWebOrderMast send 값을 true로 업데이트");
                                TempWebOrderMast entityToUpdate = foundEntity.get();
                                TempWebOrderMast updatedEntity = TempWebOrderMast.builder()
                                        .orderMastDate(entityToUpdate.getOrderMastDate())
                                        .orderMastSosok(entityToUpdate.getOrderMastSosok())
                                        .orderMastUjcd(entityToUpdate.getOrderMastUjcd())
                                        .orderMastAcno(entityToUpdate.getOrderMastAcno())
                                        .orderMastCust(entityToUpdate.getOrderMastCust())
                                        .orderMastScust(entityToUpdate.getOrderMastScust())
                                        .orderMastSawon(entityToUpdate.getOrderMastSawon())
                                        .orderMastSawonBuse(entityToUpdate.getOrderMastSawonBuse())
                                        .orderMastOdate(entityToUpdate.getOrderMastOdate())
                                        .orderMastProject(entityToUpdate.getOrderMastProject())
                                        .orderMastRemark(entityToUpdate.getOrderMastRemark())
                                        .orderMastFdate(entityToUpdate.getOrderMastFdate())
                                        .orderMastFuser(entityToUpdate.getOrderMastFuser())
                                        .orderMastLdate(entityToUpdate.getOrderMastLdate())
                                        .orderMastLuser(entityToUpdate.getOrderMastLuser())
                                        .orderMastComaddr1(entityToUpdate.getOrderMastComaddr1())
                                        .orderMastComaddr2(entityToUpdate.getOrderMastComaddr2())
                                        .orderMastComname(entityToUpdate.getOrderMastComname())
                                        .orderMastComuname(entityToUpdate.getOrderMastComuname())
                                        .orderMastComutel(entityToUpdate.getOrderMastComutel())
                                        .orderMastReason(entityToUpdate.getOrderMastReason())
                                        .orderMastTcomdiv(entityToUpdate.getOrderMastTcomdiv())
                                        .orderMastCurrency(entityToUpdate.getOrderMastCurrency())
                                        .orderMastCurrencyPer(entityToUpdate.getOrderMastCurrencyPer())
                                        .orderMastSdiv(entityToUpdate.getOrderMastSdiv())
                                        .orderMastDcust(entityToUpdate.getOrderMastDcust())
                                        .orderMastIntype(entityToUpdate.getOrderMastIntype())
                                        .orderMastOtime(entityToUpdate.getOrderMastOtime())
                                        .userId(entityToUpdate.getUserId())
                                        .send(true) // 🔥 send 값을 true로 설정
                                        .createdAt(entityToUpdate.getCreatedAt())
                                        .updatedAt(LocalDateTime.now())
                                        .build();
                                
                                TempWebOrderMast finalEntity = tempWebOrderMastRepository.save(updatedEntity);
                                log.info("✅ PUT API - TempWebOrderMast send 값 업데이트 완료: {}", finalEntity.getSend());
                                
                                // 🔥 TempWebOrderTran의 send 값도 true로 업데이트
                                log.info("🔍 PUT API - TempWebOrderTran send 값을 true로 업데이트");
                                List<TempWebOrderTran> tempTrans = tempWebOrderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                                        finalEntity.getOrderMastDate(),
                                        finalEntity.getOrderMastSosok(),
                                        finalEntity.getOrderMastUjcd(),
                                        finalEntity.getOrderMastAcno()
                                );
                                
                                for (TempWebOrderTran tempTran : tempTrans) {
                                    TempWebOrderTran updatedTran = TempWebOrderTran.builder()
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
                                            .userId(tempTran.getUserId())
                                            .send(true) // 🔥 send 값을 true로 설정
                                            .createdAt(tempTran.getCreatedAt())
                                            .updatedAt(LocalDateTime.now())
                                            .build();
                                    
                                    tempWebOrderTranRepository.save(updatedTran);
                                }
                                log.info("✅ PUT API - TempWebOrderTran send 값 업데이트 완료: {}개", tempTrans.size());
                                
                                // 🔥 그 다음 convertToWebOrderMast 호출
                                log.info("🔍 PUT API - TempWebOrderMast 조회 성공, convertToWebOrderMast 호출");
                                convertToWebOrderMast(finalEntity);
                                log.info("✅ PUT API - convertToWebOrderMast 완료");
                            } else {
                                log.error("❌ PUT API - TempWebOrderMast 조회 실패: {}", tempId);
                            }
                        } else {
                            log.info("ℹ️ PUT API - send=false이므로 변환 생략");
                        }
                        
                        // 3-5. 통합 응답 생성
                        return TempWebOrderMastResponse.fromWithOrderTrans(
                                tempWebOrderMastRepository.findById(existingId).orElseThrow(),
                                newOrderTrans
                        );
                    }
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
        
        // 🔥 이미 WebOrderMast에 있으면 업데이트, 없으면 신규 생성
        boolean webOrderMastExists = webOrderMastRepository.existsById(webId);
        log.info("WebOrderMast 존재 여부 확인: {} - {}", tempEntity.getOrderKey(), webOrderMastExists);
        
        if (webOrderMastExists) {
            log.info("기존 WebOrderMast 업데이트 모드: {}", tempEntity.getOrderKey());
            
            // 기존 WebOrderTran들 삭제 (전체 교체)
            List<WebOrderTran> existingWebTrans = webOrderTranRepository.findAll().stream()
                    .filter(wt -> wt.getOrderTranDate().equals(tempEntity.getOrderMastDate()) &&
                                 wt.getOrderTranSosok().equals(tempEntity.getOrderMastSosok()) &&
                                 wt.getOrderTranUjcd().equals(tempEntity.getOrderMastUjcd()) &&
                                 wt.getOrderTranAcno().equals(tempEntity.getOrderMastAcno()))
                    .collect(Collectors.toList());
            
            webOrderTranRepository.deleteAll(existingWebTrans);
            log.info("기존 WebOrderTran 삭제 완료: {}개", existingWebTrans.size());
        } else {
            log.info("신규 WebOrderMast 생성 모드: {}", tempEntity.getOrderKey());
        }

        // 1. TempWebOrderMast의 데이터를 WebOrderMast로 복사 (업데이트 또는 신규 생성)
        WebOrderMast webEntity;
        if (webOrderMastExists) {
            // 기존 데이터 업데이트
            webEntity = webOrderMastRepository.findById(webId).orElseThrow();
            
            // 필드 업데이트
            webEntity = WebOrderMast.builder()
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
        } else {
            // 신규 생성
            webEntity = WebOrderMast.builder()
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
        }

        WebOrderMast savedWebEntity = webOrderMastRepository.save(webEntity);
        
        // 2. 관련된 TempWebOrderTran들을 WebOrderTran으로 복사
        System.out.println("🔍 TempWebOrderTran 조회 시작:");
        System.out.println("   - DATE: " + tempEntity.getOrderMastDate());
        System.out.println("   - SOSOK: " + tempEntity.getOrderMastSosok());
        System.out.println("   - UJCD: " + tempEntity.getOrderMastUjcd());
        System.out.println("   - ACNO: " + tempEntity.getOrderMastAcno());
        
        List<TempWebOrderTran> tempTrans = tempWebOrderTranRepository.findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcno(
                tempEntity.getOrderMastDate(),
                tempEntity.getOrderMastSosok(),
                tempEntity.getOrderMastUjcd(),
                tempEntity.getOrderMastAcno()
        );
        
        System.out.println("🔍 TempWebOrderTran 조회 결과: " + tempTrans.size() + "개 발견");
        for (int i = 0; i < tempTrans.size(); i++) {
            TempWebOrderTran tempTran = tempTrans.get(i);
            System.out.println("   [" + (i+1) + "] " + tempTran.getOrderTranKey() + " - " + tempTran.getOrderTranDeta());
        }
        
        List<WebOrderTran> savedWebTrans = new ArrayList<>();
        for (int i = 0; i < tempTrans.size(); i++) {
            TempWebOrderTran tempTran = tempTrans.get(i);
            System.out.println("🔍 [" + (i+1) + "/" + tempTrans.size() + "] WebOrderTran 변환 시작: " + tempTran.getOrderTranKey());
            
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
            
            WebOrderTran savedWebTran = webOrderTranRepository.save(webTran);
            savedWebTrans.add(savedWebTran);
            System.out.println("✅ [" + (i+1) + "/" + tempTrans.size() + "] WebOrderTran 저장 완료: " + savedWebTran.getOrderTranKey());
        }
        
        System.out.println("✅ TempWebOrderMast → WebOrderMast 변환 완료: " + tempEntity.getOrderKey() + " (사용자: " + tempEntity.getUserId() + ")");
        System.out.println("✅ TempWebOrderTran → WebOrderTran 변환 완료: " + tempTrans.size() + "개 항목");
        
        // 3. 🔥 Web DB → ERP DB 저장 (새로 추가!)
        try {
            System.out.println("🔍 ERP DB 저장 시작: " + savedWebEntity.getOrderKey());
            
            // 3-1. ERP OrderMast 중복 확인
            System.out.println("🔍 ERP OrderMast 존재 여부 확인 중...");
            boolean erpOrderMastExists = erpOrderMastService.existsOrderMast(
                    savedWebEntity.getOrderMastDate(),
                    savedWebEntity.getOrderMastSosok(),
                    savedWebEntity.getOrderMastUjcd(),
                    savedWebEntity.getOrderMastAcno()
            );
            
            System.out.println("🔍 ERP OrderMast 존재 여부 확인 결과: " + erpOrderMastExists);
            
            if (!erpOrderMastExists) {
                System.out.println("🔍 ERP OrderMast 신규 저장 시작...");
                // 3-2. ERP OrderMast 저장
                erpOrderMastService.saveOrderMastFromWeb(savedWebEntity);
                System.out.println("✅ ERP OrderMast 저장 완료");
                
                // 3-3. ERP OrderTran 저장
                System.out.println("🔍 ERP OrderTran 저장 시작 - 총 " + savedWebTrans.size() + "개 OrderTran 처리 예정");
                for (int i = 0; i < savedWebTrans.size(); i++) {
                    WebOrderTran webTran = savedWebTrans.get(i);
                    try {
                        System.out.println("🔍 [" + (i+1) + "/" + savedWebTrans.size() + "] ERP OrderTran 저장 시도: " + webTran.getOrderTranKey());
                        
                        boolean erpOrderTranExists = erpOrderTranService.existsOrderTran(
                                webTran.getOrderTranDate(),
                                webTran.getOrderTranSosok(),
                                webTran.getOrderTranUjcd(),
                                webTran.getOrderTranAcno(),
                                webTran.getOrderTranSeq()
                        );
                        
                        System.out.println("🔍 [" + (i+1) + "/" + savedWebTrans.size() + "] ERP OrderTran 존재 여부 확인 완료: " + erpOrderTranExists);
                        
                        if (!erpOrderTranExists) {
                            System.out.println("🔍 [" + (i+1) + "/" + savedWebTrans.size() + "] ERP OrderTran 저장 호출 시작...");
                            erpOrderTranService.saveOrderTranFromWeb(webTran);
                            System.out.println("✅ [" + (i+1) + "/" + savedWebTrans.size() + "] ERP OrderTran 저장 성공: " + webTran.getOrderTranKey());
                        } else {
                            System.out.println("⚠️ [" + (i+1) + "/" + savedWebTrans.size() + "] ERP OrderTran 이미 존재: " + webTran.getOrderTranKey());
                        }
                    } catch (Exception e) {
                        System.err.println("❌ [" + (i+1) + "/" + savedWebTrans.size() + "] ERP OrderTran 저장 실패: " + webTran.getOrderTranKey() + " - " + e.getMessage());
                        System.err.println("   OrderTran 상세정보:");
                        System.err.println("   - DATE: " + webTran.getOrderTranDate());
                        System.err.println("   - SOSOK: " + webTran.getOrderTranSosok());
                        System.err.println("   - UJCD: " + webTran.getOrderTranUjcd());
                        System.err.println("   - ACNO: " + webTran.getOrderTranAcno());
                        System.err.println("   - SEQ: " + webTran.getOrderTranSeq());
                        System.err.println("   - ITEM: " + webTran.getOrderTranItem());
                        System.err.println("   - STAU: " + webTran.getOrderTranStau());
                        e.printStackTrace();
                    }
                }
                
                System.out.println("🎉 ERP DB 저장 완료: " + savedWebEntity.getOrderKey() + " (OrderMast + " + savedWebTrans.size() + "개 OrderTran)");
            } else {
                System.out.println("⚠️ ERP OrderMast 이미 존재하므로 ERP OrderTran 저장을 생략합니다: " + savedWebEntity.getOrderKey());
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERP DB 저장 실패: " + savedWebEntity.getOrderKey() + " - " + e.getMessage());
            // ERP 저장 실패해도 Web DB 저장은 유지 (비즈니스 연속성)
            e.printStackTrace();
        }
    }
    
    /**
     * ACNO 자동 생성 - 같은 날짜, 소속, 업장에 대한 시퀀스 번호
     */
    private Integer generateNextAcno(String orderDate, Integer sosok, String ujcd) {
        Integer maxAcno = tempWebOrderMastRepository.findMaxAcnoByDateAndSosokAndUjcd(orderDate, sosok, ujcd);
        return maxAcno + 1;
    }
} 