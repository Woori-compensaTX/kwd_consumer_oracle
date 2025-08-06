package com.fisa.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import com.fisa.service.MsiService;
import com.fisa.dto.DepositDTO;
import com.fisa.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OehwaConsumer {
    private final MsiService msiService;
    private final KafkaTemplate<String, DepositDTO> kafkaTemplate;

    // 외화 입금 토픽 자동 생성
    @Bean
    public NewTopic OehwaDeposit() {
        return TopicBuilder.name("ServerOehwaDeposit")
                .replicas(1)
                .build();
    }

    // 외화 출금 토픽 자동 생성
    @Bean
    public NewTopic OehwaWithdrawal() {
        return TopicBuilder.name("ServerOehwaWithdrawal")
                .replicas(1)
                .build();
    }

    
    @KafkaListener(topics = "ServerOehwaDeposit", groupId = "fisa")
    @RetryableTopic(
        attempts = "2",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = ".DLQ",
        autoCreateTopics = "true"
    )
    public void getOehwaDeposit(DepositDTO dto) {
        System.out.printf(
            "Oehwa Consumer [입금] guid: %s, userid: %s, accountid: %s, amount: %d, date: %s\n",
            dto.getGuid(), dto.getUserid(), dto.getAccountid(), dto.getAmount(), dto.getDate()
        );
    
        msiService.setDepositOehwa(dto);
    }

    // 외화 출금 메시지 소비 (2회 재시도, 비즈니스 예외 discard, 시스템 예외 DLQ)
    @KafkaListener(topics = "ServerOehwaWithdrawal", groupId = "fisa")
    @RetryableTopic(
        attempts = "2",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = ".DLQ",
        autoCreateTopics = "true"
    )
    public void getOehwaWithdrawal(DepositDTO dto) {
        int maxRetry = 2;
        int attempt = 0;
        boolean success = false;
        Exception lastEx = null;

        while (attempt < maxRetry) {
            try {
                System.out.printf(
                    "Oehwa Consumer [출금] guid: %s, userid: %s, accountid: %s, amount: %d, date: %s, attempt: %d\n",
                    dto.getGuid(), dto.getUserid(), dto.getAccountid(), dto.getAmount(), dto.getDate(), attempt + 1
                );
                msiService.setWithdrawal(dto);
                success = true;
                break;
            } catch (BusinessException be) {
                System.err.println("Oehwa 비즈니스 예외 - 메시지 discard: " + be.getMessage());
                return;
            } catch (Exception e) {
                attempt++;
                lastEx = e;
                System.err.printf("Oehwa 출금 처리 실패 시도: %d / %d - %s\n", attempt, maxRetry, e.getMessage());
            }
        }
        if (!success) {
            if (dto.getRetryCount() == null) dto.setRetryCount(0);
            System.err.printf("[Oehwa DLQ로 이관] guid: %s | retryCount: %d | 사유: %s\n",
                dto.getGuid(), dto.getRetryCount(), (lastEx != null ? lastEx.getMessage() : ""));
            kafkaTemplate.send("ServerOehwaWithdrawal.DLQ", dto);
        }
    }
}
