package com.income.helper;

import lombok.Data;

@Data
public class IncomeRequestDto {
    private String remark;
    private float amount;
    private Long userId;
}
