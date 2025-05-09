package com.expenses.service;

import com.expenses.entity.ExpenseCategory;
import com.expenses.helper.ExpenseDto;
import com.expenses.helper.ExpenseRequestDto;

import java.util.Date;
import java.util.List;

public interface ExpensesService {

    String addExpense(String expenses);

    String updateExpense(ExpenseRequestDto expenses, Long expensesId);

    String deleteExpense(Long expensesId);

    List<ExpenseDto> getAllExpensesByReceiverId(Long receiverId);

    List<ExpenseDto> filterExpensesByDate(Date from, Date to,Long receiverId);

    List<ExpenseDto> filterExpensesByCategory(ExpenseCategory category, Long receiverId);

    List<ExpenseDto> getRecentExpensesHistory(Long userId);

    ExpenseDto getExpense(Long expensesId);

}
