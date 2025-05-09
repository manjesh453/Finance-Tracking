package com.income.helper;

import lombok.Data;

@Data
public class IncomeDto {
    private Long id;
    private String remark;
    private float amount;
    private Long userId;
}
