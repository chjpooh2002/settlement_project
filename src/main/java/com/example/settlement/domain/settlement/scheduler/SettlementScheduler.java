package com.example.settlement.domain.settlement.scheduler;

import com.example.settlement.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final SettlementService settlementService;


     //매일 밤 12시에 정산 로직 자동 실행 배치
     //초 분 시 일 월 요일 (Cron 표현식)

    @Scheduled(cron = "0 0 0 * * *")
    public void executeDailySettlement() {
        log.info("▶ [정산 배치 스케줄러] 매일 밤 12시 정산 데이터 생성을 시작합니다.");

        try {
            settlementService.runSettlementBatch();
            log.info("✓ [정산 배치 스케줄러] 당일 정산 파이프라인 적재를 성공적으로 완료했습니다.");
        } catch (Exception e) {
            log.error("❌ [정산 배치 스케줄러] 정산 처리 도중 치명적인 예외 발생: ", e);
        }
    }
}
