package com.fisa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

@Entity
public class Oehwa {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <== MySQL에서는 IDENTITY 사용
    private int id;

    private String guid;    // GUID
    private int account;
    private String userId;
    private String type;
    private String base;
    private LocalDateTime createAt; // MySQL은 TIMESTAMP로 자동 매핑됨
    private String userBankId;
}
