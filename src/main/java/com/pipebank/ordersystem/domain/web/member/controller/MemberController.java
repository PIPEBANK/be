package com.pipebank.ordersystem.domain.web.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pipebank.ordersystem.domain.web.member.dto.MemberCreateRequest;
import com.pipebank.ordersystem.domain.web.member.dto.MemberResponse;
import com.pipebank.ordersystem.domain.web.member.dto.MemberUpdateRequest;
import com.pipebank.ordersystem.domain.web.member.dto.PasswordChangeRequest;
import com.pipebank.ordersystem.domain.web.member.entity.MemberRole;
import com.pipebank.ordersystem.domain.web.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 생성 (관리자만)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberCreateRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        request.setCreateBy(userDetails.getUsername());
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 조회 (ID)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @memberService.getMember(#id).memberId == authentication.name")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        MemberResponse response = memberService.getMember(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 조회 (회원 ID)
     */
    @GetMapping("/by-member-id/{memberId}")
    @PreAuthorize("hasRole('ADMIN') or #memberId == authentication.name")
    public ResponseEntity<MemberResponse> getMemberByMemberId(@PathVariable String memberId) {
        MemberResponse response = memberService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        MemberResponse response = memberService.getActiveMember(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 목록 조회 (관리자만)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberResponse>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<MemberResponse> response = memberService.getMembers(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 검색 (관리자만)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberResponse>> searchMembers(
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String custCode,
            @RequestParam(required = false) MemberRole role,
            @RequestParam(required = false) Boolean useYn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<MemberResponse> response = memberService.searchMembers(
            memberId, memberName, custCode, role, useYn, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래처별 회원 조회
     */
    @GetMapping("/by-custcode/{custCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberResponse>> getMembersByCustCode(
            @PathVariable String custCode,
            @RequestParam(required = false) Boolean useYn) {
        List<MemberResponse> response = memberService.getMembersByCustCode(custCode, useYn);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 정보 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @memberService.getMember(#id).memberId == authentication.name")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id,
                                                      @Valid @RequestBody MemberUpdateRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        // 일반 사용자는 자신의 role, useYn 변경 불가
        if (!userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            request.setRole(null);
            request.setUseYn(null);
        }
        
        request.setUpdateBy(userDetails.getUsername());
        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @memberService.getMember(#id).memberId == authentication.name")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable Long id,
                                                             @Valid @RequestBody PasswordChangeRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        request.setUpdateBy(userDetails.getUsername());
        memberService.changePassword(id, request);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "비밀번호가 성공적으로 변경되었습니다");
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 비활성화 (관리자만)
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateMember(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        memberService.deactivateMember(id, userDetails.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원이 성공적으로 비활성화되었습니다");
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 완전 삭제 (관리자만)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원이 성공적으로 삭제되었습니다");
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 ID 중복 체크
     */
    @GetMapping("/check-duplicate/{memberId}")
    public ResponseEntity<Map<String, Boolean>> checkDuplicateMemberId(@PathVariable String memberId) {
        boolean exists = memberService.isExistsMemberId(memberId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 통계 (관리자만)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getMemberStatistics(
            @RequestParam(required = false) String custCode) {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalActiveMembers", memberService.getActiveMemberCount());
        statistics.put("totalAdmins", memberService.getAdminCount());
        
        if (custCode != null) {
            statistics.put("activeMembersByCustCode", memberService.getActiveMemberCountByCustCode(custCode));
        }
        
        return ResponseEntity.ok(statistics);
    }
} 