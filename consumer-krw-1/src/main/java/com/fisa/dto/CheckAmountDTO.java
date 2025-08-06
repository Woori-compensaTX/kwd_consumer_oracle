package com.fisa.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckAmountDTO {
    private String userid;
    private String base;
    private String base_account;
    private int base_amount;
}