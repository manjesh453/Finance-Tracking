package com.users.service;


import com.users.helper.ExpensesDto;
import com.users.helper.IncomeDto;

public interface ManageIncomeExpenses {
    String addIncome(Long userId, IncomeDto requestDto);
    String updateIncome(Long userId, IncomeDto requestDto,Long incomeId);

    String addExpense(Long userId, ExpensesDto requestDto);
    String updateExpense(Long userId, ExpensesDto requestDto,Long incomeId);
}
