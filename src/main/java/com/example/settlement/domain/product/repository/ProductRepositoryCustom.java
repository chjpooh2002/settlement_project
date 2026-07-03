package com.example.settlement.domain.product.repository;

import com.example.settlement.domain.product.dto.ProductSearchCondition;
import com.example.settlement.domain.product.entity.Product;
import java.util.List;

public interface ProductRepositoryCustom {
    // 동적 검색 조건을 받아 필터링된 상품 리스트를 반환하는 메서드 정의
    List<Product> searchProducts(ProductSearchCondition condition);
}
