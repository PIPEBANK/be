package com.pipebank.ordersystem.domain.erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "co_comm_cod2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CommonCode2.CommonCode2Id.class)
public class CommonCode2 {

    @Id
    @Column(name = "COMM_COD2_COD1", columnDefinition = "char(3)", nullable = false)
    private String commCod2Cod1;

    @Id
    @Column(name = "COMM_COD2_COD2", columnDefinition = "char(3)", nullable = false)
    private String commCod2Cod2;

    @Column(name = "COMM_COD2_CODE", columnDefinition = "char(6)", nullable = false, unique = true)
    private String commCod2Code;

    @Column(name = "COMM_COD2_NUM", nullable = false, length = 10)
    private String commCod2Num;

    @Column(name = "COMM_COD2_WORD", nullable = false, length = 50)
    private String commCod2Word;

    @Column(name = "COMM_COD2_NAME", nullable = false, length = 50)
    private String commCod2Name;

    @Column(name = "COMM_COD2_SORT", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod2Sort;

    @Column(name = "COMM_COD2_VIEW", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod2View;

    @Column(name = "COMM_COD2_USE", nullable = false, columnDefinition = "tinyint(3)")
    private Integer commCod2Use;

    @Column(name = "COMM_COD2_REMARK", nullable = false, length = 50)
    private String commCod2Remark;

    @Column(name = "COMM_COD2_FDATE", nullable = false)
    private LocalDateTime commCod2Fdate;

    @Column(name = "COMM_COD2_FUSER", nullable = false, length = 20)
    private String commCod2Fuser;

    @Column(name = "COMM_COD2_LDATE", nullable = false)
    private LocalDateTime commCod2Ldate;

    @Column(name = "COMM_COD2_LUSER", nullable = false, length = 20)
    private String commCod2Luser;

    // 비즈니스 메서드
    public boolean isActive() {
        return commCod2Use == 1;
    }

    public boolean isVisible() {
        return commCod2View == 1;
    }

    public String getFullCode() {
        return commCod2Code;
    }

    public String getDisplayName() {
        return commCod2Name != null ? commCod2Name : commCod2Word;
    }

    // 복합키 클래스
    @Getter
    @NoArgsConstructor
    public static class CommonCode2Id implements Serializable {
        private String commCod2Cod1;
        private String commCod2Cod2;

        public CommonCode2Id(String commCod2Cod1, String commCod2Cod2) {
            this.commCod2Cod1 = commCod2Cod1;
            this.commCod2Cod2 = commCod2Cod2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CommonCode2Id that = (CommonCode2Id) o;

            if (!commCod2Cod1.equals(that.commCod2Cod1)) return false;
            return commCod2Cod2.equals(that.commCod2Cod2);
        }

        @Override
        public int hashCode() {
            int result = commCod2Cod1.hashCode();
            result = 31 * result + commCod2Cod2.hashCode();
            return result;
        }
    }
} 