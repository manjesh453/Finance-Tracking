package com.users.helper;

import lombok.Data;

@Data
public class ExpensesDto {
    private String remark;
    private float amount;
    private String category;
}
