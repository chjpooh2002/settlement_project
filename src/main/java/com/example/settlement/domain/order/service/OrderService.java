package com.example.settlement.domain.order.service;

import com.example.settlement.domain.member.entity.Member;
import com.example.settlement.domain.member.repository.MemberRepository;
import com.example.settlement.domain.order.dto.OrderCreateRequest;
import com.example.settlement.domain.order.entity.OrderProduct;
import com.example.settlement.domain.order.entity.OrderStatus;
import com.example.settlement.domain.order.entity.Orders;
import com.example.settlement.domain.order.repository.OrderRepository;
import com.example.settlement.domain.product.entity.Product;
import com.example.settlement.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /**
     * 주문 생성 비즈니스 로직
     */
    @Transactional // 쓰기 작업이 포함되므로 별도로 격리수준 부여
    public Long createOrder(OrderCreateRequest request) {

        // 1. 구매자 조회
        Member buyer = memberRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 초기 뼈대가 될 주문 마스터(Orders) 객체 임시 빌드
        Orders order = Orders.builder()
                .buyer(buyer)
                .orderStatus(OrderStatus.PENDING) // 처음엔 결제 대기 상태
                .totalAmount(BigDecimal.ZERO)     // 뒤에서 상품 계산 후 합산 예정
                .orderProducts(new ArrayList<>())
                .build();

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;

        // 3. 다중 상품 순회하며 상세 데이터 생성
        for (OrderCreateRequest.ProductItem item : request.getProductItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레슨 상품입니다."));

            // 주문 시점의 실시간 금액 스냅샷을 떠서 완벽히 보존
            OrderProduct orderProduct = OrderProduct.builder()
                    .orders(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .orderPrice(product.getPrice()) // 실시간 상품 테이블 금액 카피
                    .build();

            order.getOrderProducts().add(orderProduct);

            // 해당 상품 총액 = 단가 * 수량
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            calculatedTotalAmount = calculatedTotalAmount.add(itemTotal);
        }

        // 4. 리플렉션을 우회하여 최종 계산된 총 금액 강제 바인딩 (Dirty Checking 활용)
        // 실제 운영 환경의 편의를 위해 마스터 엔티티 총액 업데이트 메서드가 필요
        // 여기서는 유연한 비즈니스 처리를 위해 객체 필드를 확정
        Orders finalOrder = Orders.builder()
                .id(order.getId())
                .buyer(order.getBuyer())
                .orderStatus(order.getOrderStatus())
                .totalAmount(calculatedTotalAmount) // 최종 연산 금액 적용
                .orderProducts(order.getOrderProducts())
                .build();

        // 5. CascadeType.ALL 설정 덕분에 주문 마스터를 저장하면 주문 상세들까지 한 방에 DB로 저장
        Orders savedOrder = orderRepository.save(finalOrder);

        return savedOrder.getId();
    }
}
