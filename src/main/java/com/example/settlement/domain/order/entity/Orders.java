package com.example.settlement.domain.order.entity;

import com.example.settlement.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // orders는 SQL 예약어인 경우가 많아 명시해줌
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Orders {  // 다중 판매자 이커머스 상황에서 구매자는 장바구니에 여러 판매자의 상품을 동시에 담아 1회 결제 가능
                       // 전체 주문을 담당하는 Orders(마스터 테이블)와 OrderProduct(상세 테이블)로 나누어 1:N 구조로 영속성 문제 해결

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer; // 구매자 정보

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 총 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus; // PENDING, COMPLETED, CANCELED

    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 엔티티 내부의 도메인 비즈니스 메서드 고도화
    public void complete() {
        // 이미 취소되거나 완료된 주문은 다시 완료될 수 없음
        if (this.orderStatus == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 결제가 완료된 주문입니다.");
        }
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문은 결제 완료 처리할 수 없습니다.");
        }

        this.orderStatus = OrderStatus.COMPLETED; // 상태 격상 (주문 완료 처리)
    }
}
