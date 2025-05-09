package com.users.helper;

import lombok.Data;

@Data
public class IncomeRequestDto {
    private Long id;
    private String remark;
    private float amount;
    private Long userId;
}
