package com.example.settlement.domain.settlement.service;

import com.example.settlement.domain.order.entity.OrderStatus;
import com.example.settlement.domain.order.entity.OrderProduct;
import com.example.settlement.domain.order.repository.OrderProductRepository;
import com.example.settlement.domain.settlement.entity.Settlement;
import com.example.settlement.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final OrderProductRepository orderProductRepository;
    private final SettlementRepository settlementRepository;


    // 결제 완료된 주문 상세 내역을 긁어모아 정산 테이블로 적재하는 배치 비즈니스

    @Transactional
    public void runSettlementBatch() {
        // 1. 결제 완료(COMPLETED) 상태인 주문 상품 상세 내역 전체 조회
        List<OrderProduct> completedOrderProducts =
                orderProductRepository.findAllByOrdersOrderStatus(OrderStatus.COMPLETED);

        // 2. 순회하며 정산 데이터 연산 및 적재
        for (OrderProduct orderProduct : completedOrderProducts) {
            Settlement settlement = Settlement.createSettlement(orderProduct);
            settlementRepository.save(settlement);
        }
    }
}
