package com.pipebank.ordersystem.domain.erp.dto;

import com.pipebank.ordersystem.domain.erp.entity.OrderTran;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderTranResponse {
    
    // 복합키 필드들
    private String orderTranDate;       // 주문일자
    private Integer orderTranSosok;     // 소속
    private String orderTranUjcd;       // 업장코드  
    private Integer orderTranAcno;      // 계정번호
    private Integer orderTranSeq;       // 순번
    
    // 품목 관련 필드들
    private String orderTranItemVer;    // 품목버전
    private Integer orderTranItem;      // 품목코드
    private String orderTranDeta;       // 품목명
    private String orderTranSpec;       // 규격
    private String orderTranUnit;       // 단위
    
    // 체크박스 필드들
    private Integer orderTranCalc;      // 정산 CheckBox
    private Integer orderTranVdiv;      // V CheckBox 부가세여부
    private Integer orderTranAdiv;      // A CheckBox 예수금여부
    private Integer orderTranLdiv;      // 판매가미반영 CheckBox
    
    // 가격 관련 필드들
    private BigDecimal orderTranRate;           // 기본단가
    private BigDecimal orderTranCnt;            // 현재고
    private BigDecimal orderTranConvertWeight;  // 환산중량
    private BigDecimal orderTranDcPer;          // 할인율
    private BigDecimal orderTranDcAmt;          // 할인금액
    private BigDecimal orderTranForiAmt;        // 외화단가
    private BigDecimal orderTranAmt;            // 판매단가
    private BigDecimal orderTranNet;            // 공급가
    private BigDecimal orderTranVat;            // 부가세
    private BigDecimal orderTranAdv;            // 예수금
    private BigDecimal orderTranTot;            // 합계
    private BigDecimal orderTranLrate;          // 이전판매단가
    private BigDecimal orderTranPrice;          // 매입가
    private BigDecimal orderTranPrice2;         // 매입가증감
    private BigDecimal orderTranWamt;           // 중량단가
    
    // 기타 필드들
    private String orderTranRemark;     // 비고
    private String orderTranStau;       // 상태 코드
    private LocalDateTime orderTranFdate;   // 최초등록일
    private String orderTranFuser;      // 최초등록자
    private LocalDateTime orderTranLdate;   // 최종수정일
    private String orderTranLuser;      // 최종수정자
    
    // 추가 정보 필드들
    private String orderTranKey;        // 복합키 조합 (DATE-SOSOK-UJCD-ACNO-SEQ)
    private String orderKey;            // 주문키 (DATE-SOSOK-UJCD-ACNO) - OrderMast와 매핑용
    private String orderNumber;         // 주문번호 (DATE-ACNO)
    
    // 코드 표시명 필드들
    private String orderTranStauDisplayName;    // 상태코드 표시명
    private String orderTranUjcdDisplayName;    // 업장코드 표시명
    
    // 관련 엔티티 정보
    private String orderTranSosokName;          // 소속명
    
    public static OrderTranResponse from(OrderTran orderTran) {
        return OrderTranResponse.builder()
                .orderTranDate(orderTran.getOrderTranDate())
                .orderTranSosok(orderTran.getOrderTranSosok())
                .orderTranUjcd(orderTran.getOrderTranUjcd())
                .orderTranAcno(orderTran.getOrderTranAcno())
                .orderTranSeq(orderTran.getOrderTranSeq())
                .orderTranItemVer(orderTran.getOrderTranItemVer())
                .orderTranItem(orderTran.getOrderTranItem())
                .orderTranDeta(orderTran.getOrderTranDeta())
                .orderTranSpec(orderTran.getOrderTranSpec())
                .orderTranUnit(orderTran.getOrderTranUnit())
                .orderTranCalc(orderTran.getOrderTranCalc())
                .orderTranVdiv(orderTran.getOrderTranVdiv())
                .orderTranAdiv(orderTran.getOrderTranAdiv())
                .orderTranRate(orderTran.getOrderTranRate())
                .orderTranCnt(orderTran.getOrderTranCnt())
                .orderTranConvertWeight(orderTran.getOrderTranConvertWeight())
                .orderTranDcPer(orderTran.getOrderTranDcPer())
                .orderTranDcAmt(orderTran.getOrderTranDcAmt())
                .orderTranForiAmt(orderTran.getOrderTranForiAmt())
                .orderTranAmt(orderTran.getOrderTranAmt())
                .orderTranNet(orderTran.getOrderTranNet())
                .orderTranVat(orderTran.getOrderTranVat())
                .orderTranAdv(orderTran.getOrderTranAdv())
                .orderTranTot(orderTran.getOrderTranTot())
                .orderTranLrate(orderTran.getOrderTranLrate())
                .orderTranPrice(orderTran.getOrderTranPrice())
                .orderTranPrice2(orderTran.getOrderTranPrice2())
                .orderTranLdiv(orderTran.getOrderTranLdiv())
                .orderTranRemark(orderTran.getOrderTranRemark())
                .orderTranStau(orderTran.getOrderTranStau())
                .orderTranFdate(orderTran.getOrderTranFdate())
                .orderTranFuser(orderTran.getOrderTranFuser())
                .orderTranLdate(orderTran.getOrderTranLdate())
                .orderTranLuser(orderTran.getOrderTranLuser())
                .orderTranWamt(orderTran.getOrderTranWamt())
                // 추가 정보
                .orderTranKey(orderTran.getOrderTranKey())
                .orderKey(orderTran.getOrderTranDate() + "-" + orderTran.getOrderTranSosok() + "-" + orderTran.getOrderTranUjcd() + "-" + orderTran.getOrderTranAcno())
                .orderNumber(orderTran.getOrderTranDate() + "-" + orderTran.getOrderTranAcno())
                .build();
    }
    
    // 코드 표시명을 포함한 팩토리 메서드
    public static OrderTranResponse from(OrderTran orderTran, 
                                        String stauDisplayName,
                                        String ujcdDisplayName,
                                        String sosokName) {
        return OrderTranResponse.builder()
                .orderTranDate(orderTran.getOrderTranDate())
                .orderTranSosok(orderTran.getOrderTranSosok())
                .orderTranUjcd(orderTran.getOrderTranUjcd())
                .orderTranAcno(orderTran.getOrderTranAcno())
                .orderTranSeq(orderTran.getOrderTranSeq())
                .orderTranItemVer(orderTran.getOrderTranItemVer())
                .orderTranItem(orderTran.getOrderTranItem())
                .orderTranDeta(orderTran.getOrderTranDeta())
                .orderTranSpec(orderTran.getOrderTranSpec())
                .orderTranUnit(orderTran.getOrderTranUnit())
                .orderTranCalc(orderTran.getOrderTranCalc())
                .orderTranVdiv(orderTran.getOrderTranVdiv())
                .orderTranAdiv(orderTran.getOrderTranAdiv())
                .orderTranRate(orderTran.getOrderTranRate())
                .orderTranCnt(orderTran.getOrderTranCnt())
                .orderTranConvertWeight(orderTran.getOrderTranConvertWeight())
                .orderTranDcPer(orderTran.getOrderTranDcPer())
                .orderTranDcAmt(orderTran.getOrderTranDcAmt())
                .orderTranForiAmt(orderTran.getOrderTranForiAmt())
                .orderTranAmt(orderTran.getOrderTranAmt())
                .orderTranNet(orderTran.getOrderTranNet())
                .orderTranVat(orderTran.getOrderTranVat())
                .orderTranAdv(orderTran.getOrderTranAdv())
                .orderTranTot(orderTran.getOrderTranTot())
                .orderTranLrate(orderTran.getOrderTranLrate())
                .orderTranPrice(orderTran.getOrderTranPrice())
                .orderTranPrice2(orderTran.getOrderTranPrice2())
                .orderTranLdiv(orderTran.getOrderTranLdiv())
                .orderTranRemark(orderTran.getOrderTranRemark())
                .orderTranStau(orderTran.getOrderTranStau())
                .orderTranFdate(orderTran.getOrderTranFdate())
                .orderTranFuser(orderTran.getOrderTranFuser())
                .orderTranLdate(orderTran.getOrderTranLdate())
                .orderTranLuser(orderTran.getOrderTranLuser())
                .orderTranWamt(orderTran.getOrderTranWamt())
                // 추가 정보
                .orderTranKey(orderTran.getOrderTranKey())
                .orderKey(orderTran.getOrderTranDate() + "-" + orderTran.getOrderTranSosok() + "-" + orderTran.getOrderTranUjcd() + "-" + orderTran.getOrderTranAcno())
                .orderNumber(orderTran.getOrderTranDate() + "-" + orderTran.getOrderTranAcno())
                // 코드 표시명들
                .orderTranStauDisplayName(stauDisplayName)
                .orderTranUjcdDisplayName(ujcdDisplayName)
                // 관련 엔티티 정보
                .orderTranSosokName(sosokName)
                .build();
    }
} 