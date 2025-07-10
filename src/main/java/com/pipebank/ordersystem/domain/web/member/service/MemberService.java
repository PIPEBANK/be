package com.pipebank.ordersystem.domain.web.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pipebank.ordersystem.domain.erp.repository.CustomerRepository;
import com.pipebank.ordersystem.domain.erp.service.CustomerService;
import com.pipebank.ordersystem.domain.web.member.dto.MemberCreateRequest;
import com.pipebank.ordersystem.domain.web.member.dto.MemberResponse;
import com.pipebank.ordersystem.domain.web.member.dto.MemberUpdateRequest;
import com.pipebank.ordersystem.domain.web.member.dto.PasswordChangeRequest;
import com.pipebank.ordersystem.domain.web.member.entity.Member;
import com.pipebank.ordersystem.domain.web.member.entity.MemberRole;
import com.pipebank.ordersystem.domain.web.member.repository.MemberRepository;
import com.pipebank.ordersystem.global.auth.dto.FindMemberIdResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 생성
     */
    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        log.info("회원 생성 요청 - ID: {}, 이름: {}", request.getMemberId(), request.getMemberName());

        // 중복 체크
        if (memberRepository.existsByMemberId(request.getMemberId())) {
            throw new IllegalArgumentException("이미 존재하는 회원 ID입니다: " + request.getMemberId());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getMemberPw());

        Member member = Member.builder()
                .memberId(request.getMemberId())
                .memberPw(encodedPassword)
                .memberName(request.getMemberName())
                .custCode(request.getCustCode())
                .useYn(request.getUseYn())
                .role(request.getRole())
                .createBy(request.getCreateBy())
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("회원 생성 완료 - ID: {}", savedMember.getMemberId());

        return MemberResponse.from(savedMember);
    }

    /**
     * 회원 조회 (ID)
     */
    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        return convertToResponseWithCustName(member);
    }

    /**
     * 회원 조회 (회원 ID)
     */
    public MemberResponse getMemberByMemberId(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID입니다: " + memberId));
        return convertToResponseWithCustName(member);
    }

    /**
     * 활성 회원 조회 (로그인용)
     */
    public MemberResponse getActiveMember(String memberId) {
        Member member = memberRepository.findByMemberIdAndUseYn(memberId, true)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 회원을 찾을 수 없습니다: " + memberId));
        return convertToResponseWithCustName(member);
    }

    /**
     * 회원 ID 찾기 (회원명 + 사업자번호)
     */
    public FindMemberIdResponse findMemberId(String memberName, String custCodeSano) {
        log.info("회원 ID 찾기 요청 - 회원명: {}, 사업자번호: {}", memberName, custCodeSano);
        
        // 1. 사업자번호로 거래처 코드 조회
        String custCode = customerService.getCustCodeBySano(custCodeSano);
        if (custCode == null) {
            log.warn("회원 ID 찾기 실패 - 존재하지 않는 사업자번호: {}", custCodeSano);
            throw new IllegalArgumentException("존재하지 않는 사업자번호입니다: " + custCodeSano);
        }
        
        // 2. 회원명과 거래처코드로 회원 조회
        Member member = memberRepository.findByMemberNameAndCustCode(memberName, custCode)
                .orElseThrow(() -> {
                    log.warn("회원 ID 찾기 실패 - 회원명: {}, 거래처코드: {}", memberName, custCode);
                    return new IllegalArgumentException("해당 조건에 맞는 회원을 찾을 수 없습니다.");
                });
        
        // 3. 비활성화된 회원 체크
        if (!member.isActive()) {
            log.warn("회원 ID 찾기 실패 - 비활성화된 회원: {}", member.getMemberId());
            throw new IllegalArgumentException("비활성화된 회원입니다.");
        }
        
        log.info("회원 ID 찾기 성공 - 회원명: {}, 회원ID: {}", memberName, member.getMemberId());
        return FindMemberIdResponse.of(member.getMemberId(), member.getMemberName());
    }

    /**
     * 회원 목록 조회 (페이징)
     */
    public Page<MemberResponse> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(this::convertToResponseWithCustName);
    }

    /**
     * 조건별 회원 검색 (거래처명 검색 최적화 - 애플리케이션 레벨)
     */
    public Page<MemberResponse> searchMembers(String memberId, String memberName, String custCodeName,
                                             MemberRole role, Boolean useYn, Pageable pageable) {
        
        // 거래처명 검색이 없으면 기본 DB 페이징 사용 (최적화)
        if (custCodeName == null || custCodeName.trim().isEmpty()) {
            Page<Member> memberPage = memberRepository.findMembersWithConditions(
                memberId, memberName, null, role, useYn, pageable);
            return memberPage.map(this::convertToResponseWithCustName);
        }
        
        // 거래처명 검색이 있는 경우: 효율적인 방식으로 처리
        // 1. 먼저 거래처명으로 custCode 목록을 조회 (ERP DB)
        List<Integer> matchingCustCodes = customerRepository.findAll().stream()
                .filter(customer -> customer.getCustCodeName() != null && 
                       customer.getCustCodeName().toLowerCase().contains(custCodeName.toLowerCase()))
                .map(customer -> customer.getCustCodeCode())
                .collect(Collectors.toList());
        
        if (matchingCustCodes.isEmpty()) {
            // 일치하는 거래처가 없으면 빈 페이지 반환
            return Page.empty(pageable);
        }
        
        // 2. custCode 목록으로 회원 검색 (Web DB) - 효율적인 IN 쿼리 사용
        Page<Member> memberPage = memberRepository.findMembersWithCustCodes(
            memberId, memberName, matchingCustCodes, role, useYn, pageable);
        
        return memberPage.map(this::convertToResponseWithCustName);
    }

    /**
     * 거래처별 회원 조회
     */
    public List<MemberResponse> getMembersByCustCode(String custCode, Boolean useYn) {
        List<Member> members = useYn != null 
            ? memberRepository.findByCustCodeAndUseYn(custCode, useYn)
            : memberRepository.findByCustCode(custCode);
        
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        member.updateMemberInfo(request.getMemberName(), request.getCustCode(), request.getUpdateBy());
        
        if (request.getRole() != null) {
            member.updateRole(request.getRole(), request.getUpdateBy());
        }
        
        if (request.getUseYn() != null) {
            member.updateUseYn(request.getUseYn(), request.getUpdateBy());
        }

        log.info("회원 정보 수정 완료 - ID: {}", member.getMemberId());
        return convertToResponseWithCustName(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long memberId, PasswordChangeRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        // 새 비밀번호 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getMemberPw())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다");
        }

        // 새 비밀번호 암호화 및 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedNewPassword, request.getUpdateBy());

        log.info("비밀번호 변경 완료 - 회원 ID: {}", member.getMemberId());
    }

    /**
     * 비밀번호 초기화 (관리자만) - 초기 비밀번호 '12345678'로 설정
     */
    @Transactional
    public void resetPassword(Long memberId, String updateBy) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        // 초기 비밀번호 '12345678'로 암호화하여 설정
        String defaultPassword = "12345678";
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        member.updatePassword(encodedPassword, updateBy);

        log.info("비밀번호 초기화 완료 - 회원 ID: {} (초기화자: {})", member.getMemberId(), updateBy);
    }

    /**
     * 회원 삭제 (비활성화)
     */
    @Transactional
    public void deactivateMember(Long memberId, String updateBy) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        member.updateUseYn(false, updateBy);
        log.info("회원 비활성화 완료 - ID: {}", member.getMemberId());
    }

    /**
     * 회원 완전 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        memberRepository.delete(member);
        log.info("회원 완전 삭제 완료 - ID: {}", member.getMemberId());
    }

    /**
     * 회원 ID 중복 체크
     */
    public boolean isExistsMemberId(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    /**
     * 통계 정보
     */
    public long getActiveMemberCount() {
        return memberRepository.countByUseYn(true);
    }

    public long getAdminCount() {
        return memberRepository.countByRoleAndUseYn(MemberRole.ADMIN, true);
    }

    public long getActiveMemberCountByCustCode(String custCode) {
        return memberRepository.countActiveMembersByCustCode(custCode);
    }

    /**
     * Member를 MemberResponse로 변환하면서 거래처 정보 매핑
     */
    private MemberResponse convertToResponseWithCustName(Member member) {
        MemberResponse response = MemberResponse.from(member);
        
        // custCode가 숫자인 경우 Customer 테이블에서 거래처 정보 조회
        if (member.getCustCode() != null && !member.getCustCode().trim().isEmpty()) {
            try {
                Integer custCodeInt = Integer.parseInt(member.getCustCode());
                customerRepository.findById(custCodeInt)
                        .ifPresentOrElse(
                            customer -> {
                                response.setCustCodeName(customer.getCustCodeName());
                                response.setCustCodeSano(customer.getCustCodeSano());
                                response.setCustCodeUname1(customer.getCustCodeUname1());
                                response.setCustCodeUtel1(customer.getCustCodeUtel1());
                                response.setCustCodeAddr(customer.getCustCodeAddr());
                                response.setCustCodeEmail(customer.getCustCodeEmail());
                                response.setCustCodeSawon(customer.getCustCodeSawon()); // 담당 사원번호 추가
                                response.setCustCodeBuse(customer.getCustCodeBuse());   // 담당 부서번호 추가
                            },
                            () -> response.setCustCodeName("거래처 정보 없음")
                        );
            } catch (NumberFormatException e) {
                log.warn("custCode가 숫자가 아닙니다: {}", member.getCustCode());
                response.setCustCodeName("잘못된 거래처 코드");
            }
        }
        
        return response;
    }
} 