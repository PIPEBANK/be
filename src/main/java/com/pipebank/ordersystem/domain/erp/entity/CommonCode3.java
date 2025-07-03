package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "co_comm_cod3")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CommonCode3.CommonCode3Id.class)
public class CommonCode3 {

    @Id
    @Column(name = "COMM_COD3_COD1", columnDefinition = "char(3)", nullable = false)
    private String commCod3Cod1;

    @Id
    @Column(name = "COMM_COD3_COD2", columnDefinition = "char(3)", nullable = false)
    private String commCod3Cod2;

    @Id
    @Column(name = "COMM_COD3_COD3", columnDefinition = "char(4)", nullable = false)
    private String commCod3Cod3;

    @Column(name = "COMM_COD3_CODE", columnDefinition = "char(10)", nullable = false, unique = true)
    private String commCod3Code;

    @Column(name = "COMM_COD3_NUM", nullable = false, length = 50)
    private String commCod3Num;

    @Column(name = "COMM_COD3_WORD", nullable = false, length = 50)
    private String commCod3Word;

    @Column(name = "COMM_COD3_HNAM", nullable = false, length = 50)
    private String commCod3Hnam;

    @Column(name = "COMM_COD3_ENAM", nullable = false, length = 50)
    private String commCod3Enam;

    @Column(name = "COMM_COD3_HSUB", nullable = false, length = 50)
    private String commCod3Hsub;

    @Column(name = "COMM_COD3_ESUB", nullable = false, length = 50)
    private String commCod3Esub;

    @Column(name = "COMM_COD3_SORT", nullable = false, columnDefinition = "int(10)")
    private Integer commCod3Sort;

    @Column(name = "COMM_COD3_VIEW", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod3View;

    @Column(name = "COMM_COD3_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod3Use;

    @Column(name = "COMM_COD3_REMARK", nullable = false, length = 100)
    private String commCod3Remark;

    @Column(name = "COMM_COD3_FDATE", nullable = false)
    private LocalDateTime commCod3Fdate;

    @Column(name = "COMM_COD3_FUSER", nullable = false, length = 20)
    private String commCod3Fuser;

    @Column(name = "COMM_COD3_LDATE", nullable = false)
    private LocalDateTime commCod3Ldate;

    @Column(name = "COMM_COD3_LUSER", nullable = false, length = 20)
    private String commCod3Luser;

    // 비즈니스 메서드
    public boolean isActive() {
        return commCod3Use == 1;
    }

    public boolean isVisible() {
        return commCod3View == 1;
    }

    public String getFullCode() {
        return commCod3Code;
    }

    public String getDisplayName() {
        return commCod3Word;
    }

    public String getHangulName() {
        return commCod3Hnam;
    }

    public String getEnglishName() {
        return commCod3Enam;
    }

    public String getHangulSub() {
        return commCod3Hsub;
    }

    public String getEnglishSub() {
        return commCod3Esub;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class CommonCode3Id implements Serializable {
        private String commCod3Cod1;
        private String commCod3Cod2;
        private String commCod3Cod3;

        public CommonCode3Id(String commCod3Cod1, String commCod3Cod2, String commCod3Cod3) {
            this.commCod3Cod1 = commCod3Cod1;
            this.commCod3Cod2 = commCod3Cod2;
            this.commCod3Cod3 = commCod3Cod3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CommonCode3Id that = (CommonCode3Id) o;

            if (!commCod3Cod1.equals(that.commCod3Cod1)) return false;
            if (!commCod3Cod2.equals(that.commCod3Cod2)) return false;
            return commCod3Cod3.equals(that.commCod3Cod3);
        }

        @Override
        public int hashCode() {
            int result = commCod3Cod1.hashCode();
            result = 31 * result + commCod3Cod2.hashCode();
            result = 31 * result + commCod3Cod3.hashCode();
            return result;
        }
    }
} 