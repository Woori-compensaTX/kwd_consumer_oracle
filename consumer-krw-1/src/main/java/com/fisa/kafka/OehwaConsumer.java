package com.fisa.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.config.TopicBuilder;

import com.fisa.service.MsiService;
import com.fisa.dto.DepositDTO;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OehwaConsumer {
    private final MsiService msiService;

    // 외화 입금 토픽 자동 생성 (파티션 2, 레플리카 2)
    @Bean
    public NewTopic OehwaDeposit() {
        return TopicBuilder.name("ServerOehwaDeposit")
                .partitions(2) // 파티션 2개
                .replicas(2)   // 레플리카 2개 (브로커 2대일 때)
                .build();
    }

    // 외화 출금 토픽 자동 생성 (파티션 2, 레플리카 2)
    @Bean
    public NewTopic OehwaWithdrawal() {
        return TopicBuilder.name("ServerOehwaWithdrawal")
                .partitions(2) // 파티션 2개
                .replicas(2)   // 레플리카 2개
                .build();
    }

    // 외화 입금 메시지 소비 (4회 재시도, DLQ 자동 이관, 지수 백오프)
    @KafkaListener(topics = "ServerOehwaDeposit", groupId = "fisa")
    @RetryableTopic(
        attempts = "4",
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
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

    // 외화 출금 메시지 소비 (4회 재시도, DLQ 자동 이관, 지수 백오프)
    @KafkaListener(topics = "ServerOehwaWithdrawal", groupId = "fisa")
    @RetryableTopic(
        attempts = "4",
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
        dltTopicSuffix = ".DLQ",
        autoCreateTopics = "true"
    )
    public void getOehwaWithdrawal(DepositDTO dto) {
        System.out.printf(
            "Oehwa Consumer [출금] guid: %s, userid: %s, accountid: %s, amount: %d, date: %s\n",
            dto.getGuid(), dto.getUserid(), dto.getAccountid(), dto.getAmount(), dto.getDate()
        );
        msiService.setWithdrawalOehwa(dto);
    }
}
