package com.fisa.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oehwa_seq_gen")
    @SequenceGenerator(name = "oehwa_seq_gen", sequenceName = "OEHWA_SEQ", allocationSize = 1)
	private int id;
	private String guid;    // GUID
	private int account;
	private String userId;
	private String type;
	private String base;
	private LocalDateTime createAt;
	private String userBankId;
}
