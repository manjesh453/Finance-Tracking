package com.users.helper;

import lombok.Data;

@Data
public class ExpensesRequestDto {
    private String remark;
    private float amount;
    private Long receiverId;
    private String category;
}
