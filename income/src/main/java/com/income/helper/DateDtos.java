package com.income.helper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
public class DateDtos {
    private Date startDate;
    private Date endDate;
    private Long receiverId;

    // Getters
    public Date getStartDate() {
        return startDate;
    }


    public Date getEndDate() {
        return endDate;
    }

    public Long getReceiverId() {
        return receiverId;
    }

}
