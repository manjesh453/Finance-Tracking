package com.expenses.service.impl;

import com.expenses.conf.AESUtil;
import com.expenses.entity.ExpenseCategory;
import com.expenses.entity.Expenses;
import com.expenses.exception.ResourcenotFoundException;
import com.expenses.helper.ExpenseDto;
import com.expenses.helper.ExpenseRequestDto;
import com.expenses.repo.ExpenseRepo;
import com.expenses.service.ExpensesService;
import com.expenses.shared.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpensesImpl implements ExpensesService {

    private final ExpenseRepo expenseRepo;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public String addExpense(String expenses) {
        String decryptPayload= AESUtil.decrypt(expenses);
        Expenses expense = objectMapper.readValue(decryptPayload, Expenses.class);
        expense.setId(null);
        expense.setStatus(Status.ACTIVE);
        expenseRepo.saveAndFlush(expense);
        return AESUtil.encrypt("You have successfully added a new expense");
    }

    @Override
    public String updateExpense(ExpenseRequestDto expensesDto, Long expensesId) {
        Expenses expenses = expenseRepo.findById(expensesId).orElseThrow(() -> new ResourcenotFoundException("Expenses", "expenseId", expensesId));
        expenses.setAmount(expensesDto.getAmount());
        expenses.setRemark(expensesDto.getRemark());
        expenses.setCategory(expensesDto.getCategory());
        expenseRepo.save(expenses);
        return "Your Expenses has been updated successfully";
    }

    @Override
    public String deleteExpense(Long expensesId) {
        Expenses expenses = expenseRepo.findById(expensesId).orElseThrow(() -> new ResourcenotFoundException("Expenses", "expenseId", expensesId));
        expenses.setStatus(Status.DELETED);
        expenseRepo.save(expenses);
        return "Your Expenses has been deleted successfully";
    }

    @Override
    public List<ExpenseDto> getAllExpensesByReceiverId(Long receiverId) {
        List<Expenses> expensesList = expenseRepo.findByReceiverIdAndStatus(receiverId, Status.ACTIVE);
        return expensesList.stream().map(list -> modelMapper.map(list, ExpenseDto.class)).toList();
    }

    @Override
    public List<ExpenseDto> filterExpensesByDate(Date from, Date to, Long receiverId) {
        List<Expenses> expensesList = expenseRepo.findByCreatedDateBetweenAndReceiverId(from, to, receiverId);
        return expensesList.stream().map(list -> modelMapper.map(list, ExpenseDto.class)).toList();
    }

    @Override
    public List<ExpenseDto> filterExpensesByCategory(ExpenseCategory category, Long receiverId) {
        List<Expenses> expensesList = expenseRepo.findByCategoryAndReceiverId(category, receiverId);
        return expensesList.stream().map(list -> modelMapper.map(list, ExpenseDto.class)).toList();
    }

    @Override
    public List<ExpenseDto> getRecentExpensesHistory(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Expenses> recentIncomes = expenseRepo.findRecentExpensesByUserId(userId, pageRequest);
        return recentIncomes.stream().map(list -> modelMapper.map(list, ExpenseDto.class)).toList();
    }

    @Override
    public ExpenseDto getExpense(Long expensesId) {
        Expenses expenses = expenseRepo.findById(expensesId).orElseThrow(() -> new ResourcenotFoundException("Expenses", "expenseId", expensesId));
        return modelMapper.map(expenses, ExpenseDto.class);
    }
}
