package com.example.settlement.domain.order.entity;

import com.example.settlement.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderPrice;  // 상품 테이블(Product)의 가격은 판매자의 정책에 따라 언제든 변경될 수 있다.
                                    // 만약 연관 관계를 믿고 실시간 조회하였는데 1년 전에 5만원에 결제한 회원의 주문 내역이
                                    // 현재 바뀐 10만원으로 노출되는 심각한 문제가 발생한다. 금융 데이터의 왜곡을 막기 위해 orderPrice 컬럼에 영구 보존하도록 설계.
}
