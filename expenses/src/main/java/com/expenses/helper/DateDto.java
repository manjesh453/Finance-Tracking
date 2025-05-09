package com.expenses.helper;

import lombok.Data;

import java.util.Date;

@Data
public class DateDto {
    private Date startDate;
    private Date endDate;
    private Long senderId;
}
