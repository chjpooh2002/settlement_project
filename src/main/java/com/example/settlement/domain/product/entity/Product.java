package com.example.settlement.domain.product.entity;

import com.example.settlement.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // JPA에서 ManyToOne의 기본 조회 방식은 Eager(즉시 로딩)이다.
                                       // 하지만 이는 불필요한 테이블 조인을 발생시키고 성능 저하를 야기하므로 지연 로딩으로 설정하여 필요한 시점에만 쿼리가 나가도록 최적화
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller; // 상품을 등록한 판매자(프로) 정보

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2) 
    private BigDecimal price;  // float이나 double 사용 시 소수점 오류가 발생할 수 있어 소수점 연산의 정밀도를 보장하는 BigDecimal 사용

    @Lob
    private String description;
}
