package com.weborder.ordersystem.domain.web.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.entity.MemberRole;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 기본 조회
    Optional<Member> findByMemberId(String memberId);
    
    boolean existsByMemberId(String memberId);
    
    // 활성화된 사용자만 조회
    Optional<Member> findByMemberIdAndUseYn(String memberId, Boolean useYn);
    
    // 회원명과 거래처코드로 조회 (회원 ID 찾기용)
    Optional<Member> findByMemberNameAndCustCode(String memberName, String custCode);
    
    // 거래처별 사용자 조회
    List<Member> findByCustCode(String custCode);
    
    List<Member> findByCustCodeAndUseYn(String custCode, Boolean useYn);
    
    // 권한별 사용자 조회
    List<Member> findByRole(MemberRole role);
    
    List<Member> findByRoleAndUseYnTrue(MemberRole role);
    
    List<Member> findByRoleInAndUseYnTrue(List<MemberRole> roles);
    
    Page<Member> findByRoleAndUseYn(MemberRole role, Boolean useYn, Pageable pageable);
    
    // 사용자 이름으로 검색
    @Query("SELECT m FROM Member m WHERE m.memberName LIKE %:memberName% AND m.useYn = :useYn")
    List<Member> findByMemberNameContainingAndUseYn(@Param("memberName") String memberName, 
                                                    @Param("useYn") Boolean useYn);
    
    // 복합 조건 검색 (기존)
    @Query("SELECT m FROM Member m WHERE " +
           "m.memberId != 'admin' AND " +
           "(:memberId IS NULL OR m.memberId LIKE %:memberId%) AND " +
           "(:memberName IS NULL OR m.memberName LIKE %:memberName%) AND " +
           "(:custCode IS NULL OR m.custCode = :custCode) AND " +
           "(:role IS NULL OR m.role = :role) AND " +
           "(:useYn IS NULL OR m.useYn = :useYn) AND " +
           "(:hbsb IS NULL OR m.hbsb = :hbsb)")
    Page<Member> findMembersWithConditions(@Param("memberId") String memberId,
                                          @Param("memberName") String memberName,
                                          @Param("custCode") String custCode,
                                          @Param("role") MemberRole role,
                                          @Param("useYn") Boolean useYn,
                                          @Param("hbsb") Integer hbsb,
                                          Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE " +
           "m.memberId != 'admin' AND " +
           "(:memberId IS NULL OR m.memberId LIKE %:memberId%) AND " +
           "(:memberName IS NULL OR m.memberName LIKE %:memberName%) AND " +
           "CAST(m.custCode AS integer) IN :custCodes AND " +
           "(:role IS NULL OR m.role = :role) AND " +
           "(:useYn IS NULL OR m.useYn = :useYn) AND " +
           "(:hbsb IS NULL OR m.hbsb = :hbsb)")
    Page<Member> findMembersWithCustCodes(@Param("memberId") String memberId,
                                         @Param("memberName") String memberName,
                                         @Param("custCodes") List<Integer> custCodes,
                                         @Param("role") MemberRole role,
                                         @Param("useYn") Boolean useYn,
                                         @Param("hbsb") Integer hbsb,
                                         Pageable pageable);
    
    // 통합 키워드 검색 (회원ID OR 회원명) + 필터
    @Query("SELECT m FROM Member m WHERE " +
           "m.memberId != 'admin' AND " +
           "(m.memberId LIKE %:keyword% OR m.memberName LIKE %:keyword%) AND " +
           "(:role IS NULL OR m.role = :role) AND " +
           "(:useYn IS NULL OR m.useYn = :useYn) AND " +
           "(:hbsb IS NULL OR m.hbsb = :hbsb)")
    Page<Member> findByKeyword(@Param("keyword") String keyword,
                               @Param("role") MemberRole role,
                               @Param("useYn") Boolean useYn,
                               @Param("hbsb") Integer hbsb,
                               Pageable pageable);

    // 통합 키워드 검색 + custCode 목록 필터 (거래처명 매칭용)
    @Query("SELECT m FROM Member m WHERE " +
           "m.memberId != 'admin' AND " +
           "(m.memberId LIKE %:keyword% OR m.memberName LIKE %:keyword% OR CAST(m.custCode AS integer) IN :custCodes) AND " +
           "(:role IS NULL OR m.role = :role) AND " +
           "(:useYn IS NULL OR m.useYn = :useYn) AND " +
           "(:hbsb IS NULL OR m.hbsb = :hbsb)")
    Page<Member> findByKeywordOrCustCodes(@Param("keyword") String keyword,
                                          @Param("custCodes") List<Integer> custCodes,
                                          @Param("role") MemberRole role,
                                          @Param("useYn") Boolean useYn,
                                          @Param("hbsb") Integer hbsb,
                                          Pageable pageable);

    // 통계용 쿼리
    long countByUseYn(Boolean useYn);
    
    long countByRoleAndUseYn(MemberRole role, Boolean useYn);
    
    @Query("SELECT COUNT(m) FROM Member m WHERE m.custCode = :custCode AND m.useYn = true")
    long countActiveMembersByCustCode(@Param("custCode") String custCode);
} 