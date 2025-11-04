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
@Table(name = "sa_proc_tran")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@IdClass(ProcTran.ProcTranId.class)
public class ProcTran {

    @Id
    @Column(name = "PROC_TRAN_DATE", columnDefinition = "char(8)", nullable = false)
    private String procTranDate; // 가공일자

    @Id
    @Column(name = "PROC_TRAN_SOSOK", columnDefinition = "int(10)", nullable = false)
    private Integer procTranSosok; // 소속

    @Id
    @Column(name = "PROC_TRAN_UJCD", columnDefinition = "char(10)", nullable = false)
    private String procTranUjcd; // 업장

    @Id
    @Column(name = "PROC_TRAN_ACNO", columnDefinition = "int(10)", nullable = false)
    private Integer procTranAcno; // 가공번호

    @Id
    @Column(name = "PROC_TRAN_SEQ", columnDefinition = "int(10)", nullable = false)
    private Integer procTranSeq; // 순번

    @Column(name = "PROC_TRAN_PCOD", columnDefinition = "char(10)", nullable = false)
    private String procTranPcod; // 공정코드

    @Column(name = "PROC_TRAN_ITEM", columnDefinition = "int(10)", nullable = false)
    private Integer procTranItem; // 품목코드

    @Column(name = "PROC_TRAN_DETA", length = 50, nullable = false)
    private String procTranDeta; // 품목명

    @Column(name = "PROC_TRAN_ITEM_VER", length = 10, nullable = false)
    private String procTranItemVer; // 품목버전

    @Column(name = "PROC_TRAN_CARIDNUM", length = 50, nullable = false)
    private String procTranCaridnum; // 카드번호

    @Column(name = "PROC_TRAN_SPEC", length = 100, nullable = false)
    private String procTranSpec; // 규격

    @Column(name = "PROC_TRAN_UNIT", length = 50, nullable = false)
    private String procTranUnit; // 단위

    @Column(name = "PROC_TRAN_CNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal procTranCnt; // 수량

    @Column(name = "PROC_TRAN_CONVERT_WEIGHT", precision = 18, scale = 3, nullable = false)
    private BigDecimal procTranConvertWeight; // 환산중량

    @Column(name = "PROC_TRAN_LCNT", precision = 18, scale = 2, nullable = false)
    private BigDecimal procTranLcnt; // L수량

    @Column(name = "PROC_TRAN_LINE", columnDefinition = "char(10)", nullable = false)
    private String procTranLine; // 라인

    @Column(name = "PROC_TRAN_TERM", precision = 18, scale = 0, nullable = false)
    private BigDecimal procTranTerm; // 기간

    @Column(name = "PROC_TRAN_LOTNO", length = 50, nullable = false)
    private String procTranLotno; // LOT번호

    @Column(name = "PROC_TRAN_STAU", columnDefinition = "char(10)", nullable = false)
    private String procTranStau; // 상태

    @Column(name = "PROC_TRAN_REMARK", length = 200, nullable = false)
    private String procTranRemark; // 비고

    @Column(name = "PROC_TRAN_FDATE", nullable = false)
    private LocalDateTime procTranFdate; // 최초등록일

    @Column(name = "PROC_TRAN_FUSER", length = 20, nullable = false)
    private String procTranFuser; // 최초등록자

    @Column(name = "PROC_TRAN_LDATE", nullable = false)
    private LocalDateTime procTranLdate; // 최종수정일

    @Column(name = "PROC_TRAN_LUSER", length = 20, nullable = false)
    private String procTranLuser; // 최종수정자

    @Column(name = "PROC_TRAN_WEIGHT", precision = 18, scale = 0, nullable = false)
    private BigDecimal procTranWeight; // 중량

    @Builder
    public ProcTran(String procTranDate, Integer procTranSosok, String procTranUjcd, Integer procTranAcno,
                    Integer procTranSeq, String procTranPcod, Integer procTranItem, String procTranDeta,
                    String procTranItemVer, String procTranCaridnum, String procTranSpec, String procTranUnit,
                    BigDecimal procTranCnt, BigDecimal procTranConvertWeight, BigDecimal procTranLcnt,
                    String procTranLine, BigDecimal procTranTerm, String procTranLotno, String procTranStau,
                    String procTranRemark, LocalDateTime procTranFdate, String procTranFuser,
                    LocalDateTime procTranLdate, String procTranLuser, BigDecimal procTranWeight) {
        this.procTranDate = procTranDate;
        this.procTranSosok = procTranSosok;
        this.procTranUjcd = procTranUjcd;
        this.procTranAcno = procTranAcno;
        this.procTranSeq = procTranSeq;
        this.procTranPcod = procTranPcod;
        this.procTranItem = procTranItem;
        this.procTranDeta = procTranDeta;
        this.procTranItemVer = procTranItemVer;
        this.procTranCaridnum = procTranCaridnum;
        this.procTranSpec = procTranSpec;
        this.procTranUnit = procTranUnit;
        this.procTranCnt = procTranCnt;
        this.procTranConvertWeight = procTranConvertWeight;
        this.procTranLcnt = procTranLcnt;
        this.procTranLine = procTranLine;
        this.procTranTerm = procTranTerm;
        this.procTranLotno = procTranLotno;
        this.procTranStau = procTranStau;
        this.procTranRemark = procTranRemark;
        this.procTranFdate = procTranFdate;
        this.procTranFuser = procTranFuser;
        this.procTranLdate = procTranLdate;
        this.procTranLuser = procTranLuser;
        this.procTranWeight = procTranWeight;
    }

    // 비즈니스 메서드
    public String getProcTranKey() {
        return procTranDate + "-" + procTranSosok + "-" + procTranUjcd + "-" + procTranAcno + "-" + procTranSeq;
    }

    // 완료 여부 확인
    public boolean isCompleted() {
        return "4150030001".equals(procTranStau);
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class ProcTranId implements Serializable {
        private String procTranDate;
        private Integer procTranSosok;
        private String procTranUjcd;
        private Integer procTranAcno;
        private Integer procTranSeq;

        public ProcTranId(String procTranDate, Integer procTranSosok, String procTranUjcd, 
                         Integer procTranAcno, Integer procTranSeq) {
            this.procTranDate = procTranDate;
            this.procTranSosok = procTranSosok;
            this.procTranUjcd = procTranUjcd;
            this.procTranAcno = procTranAcno;
            this.procTranSeq = procTranSeq;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProcTranId that = (ProcTranId) o;

            if (!procTranDate.equals(that.procTranDate)) return false;
            if (!procTranSosok.equals(that.procTranSosok)) return false;
            if (!procTranUjcd.equals(that.procTranUjcd)) return false;
            if (!procTranAcno.equals(that.procTranAcno)) return false;
            return procTranSeq.equals(that.procTranSeq);
        }

        @Override
        public int hashCode() {
            int result = procTranDate.hashCode();
            result = 31 * result + procTranSosok.hashCode();
            result = 31 * result + procTranUjcd.hashCode();
            result = 31 * result + procTranAcno.hashCode();
            result = 31 * result + procTranSeq.hashCode();
            return result;
        }
    }
}

