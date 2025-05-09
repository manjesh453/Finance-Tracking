package com.expenses.service.impl;

import com.expenses.conf.AESUtil;
import com.expenses.entity.ExpenseCategory;
import com.expenses.entity.Expenses;
import com.expenses.exception.ResourcenotFoundException;
import com.expenses.helper.ExpenseDto;
import com.expenses.helper.ExpenseRequestDto;
import com.expenses.repo.ExpenseRepo;
import com.expenses.shared.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensesImplTest {
    @Mock
    private ExpenseRepo expenseRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ExpensesImpl expensesService;

    @Test
    void addExpense() throws Exception {
        String encrypted = "encrypted";
        String decrypted = "{\"amount\":200.0,\"receiverId\":1,\"remark\":\"Test\"}";
        Expenses expense = new Expenses();
        expense.setStatus(Status.ACTIVE);

        mockStatic(AESUtil.class);
        when(AESUtil.decrypt(encrypted)).thenReturn(decrypted);
        when(objectMapper.readValue(decrypted, Expenses.class)).thenReturn(expense);
        when(AESUtil.encrypt(anyString())).thenReturn("response");

        String result = expensesService.addExpense(encrypted);
        assertEquals("response", result);
        verify(expenseRepo).saveAndFlush(any(Expenses.class));
    }

    @Test
    void updateExpense() {
        ExpenseRequestDto dto = new ExpenseRequestDto();
        dto.setAmount(500f);
        dto.setRemark("Updated");
        dto.setCategory(ExpenseCategory.FOOD);

        Expenses existing = new Expenses();
        when(expenseRepo.findById(1L)).thenReturn(Optional.of(existing));

        String result = expensesService.updateExpense(dto, 1L);

        assertEquals("Your Expenses has been updated successfully", result);
        verify(expenseRepo).save(existing);
    }

    @Test
    void deleteExpense() {
        Expenses existing = new Expenses();
        when(expenseRepo.findById(1L)).thenReturn(Optional.of(existing));

        String result = expensesService.deleteExpense(1L);

        assertEquals("Your Expenses has been deleted successfully", result);
        assertEquals(Status.DELETED, existing.getStatus());
        verify(expenseRepo).save(existing);
    }

    @Test
    void getAllExpensesByReceiverId() {
        List<Expenses> list = List.of(new Expenses());
        when(expenseRepo.findByReceiverIdAndStatus(1L, Status.ACTIVE)).thenReturn(list);
        when(modelMapper.map(any(Expenses.class), eq(ExpenseDto.class))).thenReturn(new ExpenseDto());

        List<ExpenseDto> result = expensesService.getAllExpensesByReceiverId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void filterExpensesByDate() {
        List<Expenses> list = List.of(new Expenses());
        Date from = new Date();
        Date to = new Date();

        when(expenseRepo.findByCreatedDateBetweenAndReceiverId(from, to, 1L)).thenReturn(list);
        when(modelMapper.map(any(Expenses.class), eq(ExpenseDto.class))).thenReturn(new ExpenseDto());

        List<ExpenseDto> result = expensesService.filterExpensesByDate(from, to, 1L);
        assertEquals(1, result.size());
    }

    @Test
    void filterExpensesByCategory() {
        ExpenseCategory category = ExpenseCategory.FOOD;
        List<Expenses> list = List.of(new Expenses());

        when(expenseRepo.findByCategoryAndReceiverId(category, 1L)).thenReturn(list);
        when(modelMapper.map(any(Expenses.class), eq(ExpenseDto.class))).thenReturn(new ExpenseDto());

        List<ExpenseDto> result = expensesService.filterExpensesByCategory(category, 1L);
        assertEquals(1, result.size());
    }

    @Test
    void getRecentExpensesHistory() {
        List<Expenses> list = List.of(new Expenses());
        when(expenseRepo.findRecentExpensesByUserId(eq(1L), any(PageRequest.class))).thenReturn(list);
        when(modelMapper.map(any(Expenses.class), eq(ExpenseDto.class))).thenReturn(new ExpenseDto());

        List<ExpenseDto> result = expensesService.getRecentExpensesHistory(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getExpense() {
        Expenses expense = new Expenses();
        when(expenseRepo.findById(1L)).thenReturn(Optional.of(expense));
        when(modelMapper.map(expense, ExpenseDto.class)).thenReturn(new ExpenseDto());

        ExpenseDto result = expensesService.getExpense(1L);
        assertNotNull(result);
    }

    @Test
    void testGetExpense_NotFound() {
        when(expenseRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourcenotFoundException.class, () -> expensesService.getExpense(1L));
    }
}
