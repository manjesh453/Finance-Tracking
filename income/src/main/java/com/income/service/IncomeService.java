package com.income.service;

import com.income.helper.IncomeDto;
import com.income.shared.Status;

import java.util.Date;
import java.util.List;

public interface IncomeService {

    String addIncome(String incomeDto);

    String updateIncome(String incomeDto, Long incomeId);

    String deleteIncome(Long incomeId);

    List<IncomeDto> getAllIncome(Long userId);

    List<IncomeDto> getIncomeByDate(Date startDate, Date endDate, Long userId);

    List<IncomeDto> getByReceiverId(Long userId, Status status);

    List<IncomeDto> getRecentIncomeHistory(Long userId);

    IncomeDto getIncomeById(Long incomeId);
}
