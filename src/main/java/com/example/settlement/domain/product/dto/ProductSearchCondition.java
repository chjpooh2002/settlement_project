package com.example.settlement.domain.product.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductSearchCondition {
    private Long sellerId;         // 💡 특정 프로(판매자)별 필터링 조건
    private BigDecimal minPrice;   // 💡 최소 금액 이상 필터링 조건
    private BigDecimal maxPrice;   // 💡 최대 금액 이하 필터링 조건
}
