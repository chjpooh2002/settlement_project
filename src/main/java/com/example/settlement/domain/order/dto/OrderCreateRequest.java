package com.example.settlement.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {

    @NotNull(message = "구매자 ID는 필수입니다.")
    private Long buyerId;

    @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
    private List<ProductItem> productItems;

    @Getter
    @Setter
    public static class ProductItem {
        @NotNull(message = "상품 ID는 필수입니다.")
        private Long productId;

        @NotNull(message = "수량은 필수입니다.")
        private Integer quantity;
    }
}