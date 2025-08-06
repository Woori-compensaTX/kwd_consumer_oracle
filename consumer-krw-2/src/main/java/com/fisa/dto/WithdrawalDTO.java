package com.fisa.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WithdrawalDTO {
    private UUID guid;              // 거래 GUID
    private String userid;          // 계좌 주인
    private String accountid;       // 계좌번호
    private int amount;             // 금액
    private LocalDateTime date;     // 날짜
    private Integer retryCount;     // ★ 재시도 카운트(처음엔 null, 필요시 0부터)
}
