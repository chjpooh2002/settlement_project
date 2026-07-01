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
}
