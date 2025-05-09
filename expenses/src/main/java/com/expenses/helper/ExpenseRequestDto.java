package com.expenses.helper;

import com.expenses.entity.ExpenseCategory;
import lombok.Data;

@Data
public class ExpenseRequestDto {
    private String remark;
    private float amount;
    private Long receiverId;
    private ExpenseCategory category;
}
