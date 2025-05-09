package com.users.controller;

import com.users.helper.ExpensesDto;
import com.users.helper.IncomeDto;
import com.users.service.ManageIncomeExpenses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class IncomeExpensesController {
    private final ManageIncomeExpenses manageIncomeExpenses;

    @PostMapping("/addIncome/{userId}")
    public String addIncome(@PathVariable Long userId, @RequestBody IncomeDto requestDto) {
        return manageIncomeExpenses.addIncome(userId, requestDto);
    }

    @PostMapping("/addExpenses/{userId}")
    public String addExpenses(@PathVariable Long userId, @RequestBody ExpensesDto requestDto) {
        return manageIncomeExpenses.addExpense(userId, requestDto);
    }

    @PostMapping("/updateIncome/{userId}/{incomeId}")
    public String updateIncome(@PathVariable Long userId, @RequestBody IncomeDto requestDto, @PathVariable Long incomeId) {
        return manageIncomeExpenses.updateIncome(userId, requestDto, incomeId);
    }

    @PostMapping("/updateExpenses/{userId}/{expensesId}")
    public String updateExpenses(@PathVariable Long userId, @RequestBody ExpensesDto requestDto, @PathVariable Long expensesId) {
        return manageIncomeExpenses.updateExpense(userId, requestDto, expensesId);
    }
}
