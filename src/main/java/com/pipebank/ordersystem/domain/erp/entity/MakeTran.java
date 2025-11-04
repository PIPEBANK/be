package com.pipebank.ordersystem.domain.erp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "sa_make_tran")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(MakeTran.MakeTranId.class)
public class MakeTran {

    @Id
    @Column(name = "MAKE_TRAN_DATE", columnDefinition = "char(8)", nullable = false)
    private String makeTranDate; // 제작일자

    @Id
    @Column(name = "MAKE_TRAN_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer makeTranSosok; // 소속

    @Id
    @Column(name = "MAKE_TRAN_UJCD", columnDefinition = "char(10)", nullable = false)
    private String makeTranUjcd; // 업장

    @Id
    @Column(name = "MAKE_TRAN_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer makeTranAcno; // 제작번호

    @Id
    @Column(name = "MAKE_TRAN_SEQ", columnDefinition = "int(10)", nullable = false)
    private Integer makeTranSeq; // 순번

    @Column(name = "MAKE_TRAN_PCOD", columnDefinition = "char(10)", nullable = false)
    private String makeTranPcod; // 공정코드

    @Column(name = "MAKE_TRAN_ITEM", columnDefinition = "int(10)", nullable = false)
    private Integer makeTranItem; // 품목코드

    @Column(name = "MAKE_TRAN_DETA", length = 50, nullable = false)
    private String makeTranDeta; // 품목명

    @Column(name = "MAKE_TRAN_ITEM_VER", length = 10, nullable = false)
    private String makeTranItemVer; // 품목버전

    @Column(name = "MAKE_TRAN_CARIDNUM", length = 50, nullable = false)
    private String makeTranCaridnum; // 카드번호

    @Column(name = "MAKE_TRAN_SPEC", length = 100, nullable = false)
    private String makeTranSpec; // 규격

    @Column(name = "MAKE_TRAN_UNIT", length = 50, nullable = false)
    private String makeTranUnit; // 단위

    @Column(name = "MAKE_TRAN_CNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal makeTranCnt; // 수량

    @Column(name = "MAKE_TRAN_CONVERT_WEIGHT", precision = 18, scale = 3, nullable = false)
    private BigDecimal makeTranConvertWeight; // 환산중량

    @Column(name = "MAKE_TRAN_LCNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal makeTranLcnt; // L수량

    @Column(name = "MAKE_TRAN_LINE", columnDefinition = "char(10)", nullable = false)
    private String makeTranLine; // 라인

    @Column(name = "MAKE_TRAN_TERM", precision = 18, scale = 0, nullable = false)
    private BigDecimal makeTranTerm; // 기간

    @Column(name = "MAKE_TRAN_LOTNO", length = 50, nullable = false)
    private String makeTranLotno; // LOT번호

    @Column(name = "MAKE_TRAN_STAU", columnDefinition = "char(10)", nullable = false)
    private String makeTranStau; // 상태

    @Column(name = "MAKE_TRAN_REMARK", length = 200, nullable = false)
    private String makeTranRemark; // 비고

    @Column(name = "MAKE_TRAN_FDATE", nullable = false)
    private LocalDateTime makeTranFdate; // 최초등록일

    @Column(name = "MAKE_TRAN_FUSER", length = 20, nullable = false)
    private String makeTranFuser; // 최초등록자

    @Column(name = "MAKE_TRAN_LDATE", nullable = false)
    private LocalDateTime makeTranLdate; // 최종수정일

    @Column(name = "MAKE_TRAN_LUSER", length = 20, nullable = false)
    private String makeTranLuser; // 최종수정자

    @Column(name = "MAKE_TRAN_WEIGHT", precision = 18, scale = 0, nullable = false)
    private BigDecimal makeTranWeight; // 중량

    @Builder
    public MakeTran(String makeTranDate, Integer makeTranSosok, String makeTranUjcd, Integer makeTranAcno,
                    Integer makeTranSeq, String makeTranPcod, Integer makeTranItem, String makeTranDeta,
                    String makeTranItemVer, String makeTranCaridnum, String makeTranSpec, String makeTranUnit,
                    BigDecimal makeTranCnt, BigDecimal makeTranConvertWeight, BigDecimal makeTranLcnt,
                    String makeTranLine, BigDecimal makeTranTerm, String makeTranLotno, String makeTranStau,
                    String makeTranRemark, LocalDateTime makeTranFdate, String makeTranFuser,
                    LocalDateTime makeTranLdate, String makeTranLuser, BigDecimal makeTranWeight) {
        this.makeTranDate = makeTranDate;
        this.makeTranSosok = makeTranSosok;
        this.makeTranUjcd = makeTranUjcd;
        this.makeTranAcno = makeTranAcno;
        this.makeTranSeq = makeTranSeq;
        this.makeTranPcod = makeTranPcod;
        this.makeTranItem = makeTranItem;
        this.makeTranDeta = makeTranDeta;
        this.makeTranItemVer = makeTranItemVer;
        this.makeTranCaridnum = makeTranCaridnum;
        this.makeTranSpec = makeTranSpec;
        this.makeTranUnit = makeTranUnit;
        this.makeTranCnt = makeTranCnt;
        this.makeTranConvertWeight = makeTranConvertWeight;
        this.makeTranLcnt = makeTranLcnt;
        this.makeTranLine = makeTranLine;
        this.makeTranTerm = makeTranTerm;
        this.makeTranLotno = makeTranLotno;
        this.makeTranStau = makeTranStau;
        this.makeTranRemark = makeTranRemark;
        this.makeTranFdate = makeTranFdate;
        this.makeTranFuser = makeTranFuser;
        this.makeTranLdate = makeTranLdate;
        this.makeTranLuser = makeTranLuser;
        this.makeTranWeight = makeTranWeight;
    }

    // 비즈니스 메서드
    public String getMakeTranKey() {
        return makeTranDate + "-" + makeTranSosok + "-" + makeTranUjcd + "-" + makeTranAcno + "-" + makeTranSeq;
    }

    // 완료 여부 확인
    public boolean isCompleted() {
        return "4150030001".equals(makeTranStau);
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class MakeTranId implements Serializable {
        private String makeTranDate;
        private Integer makeTranSosok;
        private String makeTranUjcd;
        private Integer makeTranAcno;
        private Integer makeTranSeq;

        public MakeTranId(String makeTranDate, Integer makeTranSosok, String makeTranUjcd, 
                         Integer makeTranAcno, Integer makeTranSeq) {
            this.makeTranDate = makeTranDate;
            this.makeTranSosok = makeTranSosok;
            this.makeTranUjcd = makeTranUjcd;
            this.makeTranAcno = makeTranAcno;
            this.makeTranSeq = makeTranSeq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MakeTranId that = (MakeTranId) o;

            if (!makeTranDate.equals(that.makeTranDate)) return false;
            if (!makeTranSosok.equals(that.makeTranSosok)) return false;
            if (!makeTranUjcd.equals(that.makeTranUjcd)) return false;
            if (!makeTranAcno.equals(that.makeTranAcno)) return false;
            return makeTranSeq.equals(that.makeTranSeq);
        }

        @Override
        public int hashCode() {
            int result = makeTranDate.hashCode();
            result = 31 * result + makeTranSosok.hashCode();
            result = 31 * result + makeTranUjcd.hashCode();
            result = 31 * result + makeTranAcno.hashCode();
            result = 31 * result + makeTranSeq.hashCode();
            return result;
        }
    }
}

