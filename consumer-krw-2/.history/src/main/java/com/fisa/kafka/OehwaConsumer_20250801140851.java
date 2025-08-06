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
        System.out.printf(
            "Oehwa Consumer [출금] guid: %s, userid: %s, accountid: %s, amount: %d, date: %s, attempt: %d\n",
            dto.getGuid(), dto.getUserid(), dto.getAccountid(), dto.getAmount(), dto.getDate(), attempt + 1
        );

        msiService.setWithdrawalOehwa(dto);
    }
}
