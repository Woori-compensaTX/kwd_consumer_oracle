package com.fisa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fisa.dto.DepositDTO;
import com.fisa.service.MsiService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/oehwa")
@RequiredArgsConstructor
public class OehwaController {

    private final MsiService msiService;

    // 외화 입금
    @PostMapping("/deposit")
    public ResponseEntity<String> depositOehwa(@RequestBody DepositDTO dto) {
        try {
            msiService.setDepositOehwa(dto);
            return ResponseEntity.ok("입금이 완료되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(299).body("입금에 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(299).body("입금에 실패했습니다.");
        }
    }

    // 외화 출금
    @PostMapping("/withdrawal")
    public ResponseEntity<String> withdrawOehwa(@RequestBody DepositDTO dto) {
        try {
            msiService.setWithdrawal(dto);
            return ResponseEntity.ok("출금이 완료되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(299).body("출금에 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(299).body("출금에 실패했습니다.");
        }
    }

    // 출금 성공 여부 체크 (GUID)
    @PostMapping("/check/withdrawal")
    public ResponseEntity<?> checkWithdrawal(@RequestBody Map<String, String> body) {
        try {
            String guid = body.get("guid");
            if (guid == null || guid.isEmpty()) {
                return ResponseEntity.status(299).body("GUID가 누락되었습니다.");
            }
            if (msiService.existsOehwaByGuid(guid)) {
                return ResponseEntity.ok().build(); // 값 없이 200만 반환
            } else {
                return ResponseEntity.status(299).body("해당 GUID의 출금 기록이 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(299).body("출금 성공 여부 확인에 실패했습니다.");
        }
    }

    // 입금 성공 여부 체크 (GUID)
    @PostMapping("/check/deposit")
    public ResponseEntity<?> checkDeposit(@RequestBody Map<String, String> body) {
        try {
            String guid = body.get("guid");
            if (guid == null || guid.isEmpty()) {
                return ResponseEntity.status(299).body("GUID가 누락되었습니다.");
            }
            if (msiService.existsOehwaByGuid(guid)) {
                return ResponseEntity.ok().build(); // 값 없이 200만 반환
            } else {
                return ResponseEntity.status(299).body("해당 GUID의 입금 기록이 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(299).body("입금 성공 여부 확인에 실패했습니다.");
        }
    }
}
