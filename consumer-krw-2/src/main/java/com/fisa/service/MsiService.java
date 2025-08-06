package com.fisa.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fisa.entity.UserBank;
import com.fisa.dto.DepositDTO;
import com.fisa.entity.Oehwa;
import com.fisa.repository.MisUserBankRepository;
import com.fisa.repository.OehwaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MsiService {
    private final OehwaRepository oehwaRepository;
    private final MisUserBankRepository misUserBankRepository;

    // 공통 트랜잭션 (입금/출금)
    public void processOehwaTransaction(DepositDTO dto, boolean isDeposit) {
        String id = String.valueOf(dto.getAccountid());
        int amount = dto.getAmount();

        // 1. 계좌 없음
        UserBank userBank = misUserBankRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다. (accountId=" + id + ")"));

        int newAmount = isDeposit
            ? userBank.getAccount() + amount
            : userBank.getAccount() - amount;

        // 2. 출금 시 잔액 부족
        if (!isDeposit && newAmount < 0) {
            throw new IllegalStateException("잔액이 부족합니다. (현재잔액=" + userBank.getAccount() + ", 요청금액=" + amount + ")");
        }

        userBank.setAccount(newAmount);

        // guid, status 추가
        String guid = (dto.getGuid() != null) ? dto.getGuid().toString() : null;

        Oehwa newOehwa = Oehwa.builder()
            .account(amount)
            .createAt(LocalDateTime.now())
            .type(isDeposit ? "입금" : "출금")
            .base(userBank.getBase())
            .userBankId(userBank.getId())
            .userId(dto.getUserid())   // userId는 DepositDTO에서 직접 가져옴
            .guid(guid)
            .build();

        // 저장 (예외 발생 시 컨트롤러에서 500으로 처리)
        misUserBankRepository.save(userBank);
        oehwaRepository.save(newOehwa);
    }

    // 입금 처리
    public void setDepositOehwa(DepositDTO dto) {
        processOehwaTransaction(dto, true);
    }

    // 출금 처리
    public void setWithdrawalOehwa(DepositDTO dto) {
        processOehwaTransaction(dto, false);
    }

    // userid로 가장 최근 1건 Oehwa 찾기 (Oracle 11g XE용 네이티브 쿼리 사용!)
    public Optional<UserBank> findOehwaByUserId(String userId) {
        return misUserBankRepository.findByUserId(userId); // ← 이 부분을 네이티브 쿼리 메서드로!
    }

    // (기존) GUID로 Oehwa 기록 존재 여부만 체크
    public boolean existsOehwaByGuid(String guid) {
        return oehwaRepository.findByGuid(guid).isPresent();
    }
}
