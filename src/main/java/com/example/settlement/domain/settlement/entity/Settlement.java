package com.example.settlement.domain.settlement.entity;

import com.example.settlement.domain.member.entity.Member;
import com.example.settlement.domain.order.entity.OrderProduct;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller; // 정산을 받아야 하는 판매자(프로)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProduct orderProduct; // 정산의 대상이 된 주문 상세 내역

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saleAmount; // 원래 판매 금액

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal feeAmount; // 플랫폼이 떼어가는 수수료

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vatAmount; // 수수료에 대한 부가세

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal settlementAmount; // 판매자에게 최종 지급될 정산 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus settlementStatus; // EXPECTED(정산 예정), COMPLETED(정산 완료)

    private LocalDateTime settledAt; // 실제 계좌 이체 등으로 지급 완료된 일시
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 엔티티 내부 정산 계산 및 생성 팩토리 메서드 구현
    public static Settlement createSettlement(OrderProduct orderProduct) {
        // 1. 원본 판매 금액 계산 (단가 * 수량)
        BigDecimal saleAmount = orderProduct.getOrderPrice()
                .multiply(BigDecimal.valueOf(orderProduct.getQuantity()));

        // 2. 플랫폼 수수료 계산 (판매 금액의 10%)
        BigDecimal feeAmount = saleAmount.multiply(new BigDecimal("0.10"));

        // 3. 수수료에 대한 부가세 계산 (수수료의 10%)
        BigDecimal vatAmount = feeAmount.multiply(new BigDecimal("0.10"));

        // 4. 판매자 최종 정산 지급액 계산 (판매 금액 - 수수료 - 부가세)
        BigDecimal settlementAmount = saleAmount.subtract(feeAmount).subtract(vatAmount);

        // 5. 정산 객체 반환
        return Settlement.builder()
                .seller(orderProduct.getProduct().getSeller()) // 상품 등록 프로
                .orderProduct(orderProduct)
                .saleAmount(saleAmount)
                .feeAmount(feeAmount)
                .vatAmount(vatAmount)
                .settlementAmount(settlementAmount)
                .settlementStatus(SettlementStatus.EXPECTED) // 초기엔 정산 예정 상태
                .build();
    }
}
