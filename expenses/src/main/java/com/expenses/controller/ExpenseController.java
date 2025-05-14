package com.expenses.controller;

import com.expenses.entity.ExpenseCategory;
import com.expenses.helper.DateDto;
import com.expenses.helper.ExpenseDto;
import com.expenses.helper.ExpenseRequestDto;
import com.expenses.service.ExpensesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpensesService expensesService;

    @PostMapping("/add")
    public String addExpense(@RequestBody String expense) {
        return expensesService.addExpense(expense);
    }

    @PostMapping("/update/{expensesId}")
    public String updateExpense(@RequestBody ExpenseRequestDto expense, @PathVariable Long expensesId) {
        return expensesService.updateExpense(expense, expensesId);
    }

    @GetMapping("/delete/{expensesId}")
    public String deleteExpense(@PathVariable Long expensesId) {
        return expensesService.deleteExpense(expensesId);
    }

    @GetMapping("/getAllExpenses/{userId}")
    public List<ExpenseDto> getAllExpenses(@PathVariable Long userId) {
        return expensesService.getAllExpensesByReceiverId( userId);
    }

    @PostMapping("/filterByDate")
    public List<ExpenseDto> updateExpense(@RequestBody DateDto dateDto) {
        return expensesService.filterExpensesByDate(dateDto.getStartDate(), dateDto.getEndDate(), dateDto.getSenderId());
    }

    @PostMapping("/getByCategory/{userId}")
    public List<ExpenseDto> filterExpensesByCategory(@RequestBody ExpenseCategory category, @PathVariable Long userId) {
        return expensesService.filterExpensesByCategory(category, userId);
    }

    @GetMapping("/getByRecentExpenses/{userId}")
    public List<ExpenseDto> getByRecentExpenses(@PathVariable Long userId) {
        return expensesService.getRecentExpensesHistory(userId);
    }
    @GetMapping("/test")
    public String testMe(){
        return "I am expenses";
    }

    @GetMapping("/getById/{expensesId}")
    public ExpenseDto getById(@PathVariable Long expensesId) {
        return expensesService.getExpense(expensesId);
    }
}
