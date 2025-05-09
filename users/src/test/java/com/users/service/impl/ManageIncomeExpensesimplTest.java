package com.users.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.entity.Account;
import com.users.entity.Users;
import com.users.exception.ResourcenotFoundException;
import com.users.helper.ExpensesDto;
import com.users.helper.ExpensesRequestDto;
import com.users.helper.IncomeDto;
import com.users.helper.IncomeRequestDto;
import com.users.repo.AccountRepo;
import com.users.repo.UserRepo;
import com.users.security.service.serviceimpl.AESUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageIncomeExpensesimplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ManageIncomeExpensesimpl service;


    @BeforeEach
    void setUp() throws Exception{
        userRepo = mock(UserRepo.class);
        accountRepo = mock(AccountRepo.class);
        objectMapper = new ObjectMapper();
        restTemplate = mock(RestTemplate.class);
        modelMapper = new ModelMapper();

        service = new ManageIncomeExpensesimpl(userRepo, accountRepo, objectMapper, restTemplate, modelMapper);
        injectPrivateField("gateWayBaseUrl", "http://localhost:8080/");
        injectPrivateField("incomeUrl", "income/");
        injectPrivateField("expensesUrl", "expenses/");
    }
    private void injectPrivateField(String fieldName, String value) throws Exception {
        Field field = ManageIncomeExpensesimpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }


    @Test
    void addIncome() throws Exception {
        Long userId = 1L;
        IncomeDto dto = new IncomeDto();
        dto.setAmount(100);
        Users user = new Users();
        Account account = new Account();
        account.setTotalIncome(0);

        IncomeRequestDto incomeRequestDto = new IncomeRequestDto();
        incomeRequestDto.setUserId(userId);
        modelMapper.map(dto, IncomeRequestDto.class);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepo.findByUsers(user)).thenReturn(account);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(AESUtil.encrypt("Income added"), HttpStatus.OK));

        String result = service.addIncome(userId, dto);
        assertEquals("Income added", result);
        assertEquals(100.0, account.getTotalIncome());
    }

    @Test
    void updateIncome() throws Exception {
        Long userId = 1L;
        Long incomeId = 10L;
        IncomeDto dto = new IncomeDto();
        dto.setAmount(200);
        Users user = new Users();
        Account account = new Account();
        account.setTotalIncome(100);

        IncomeRequestDto oldIncome = new IncomeRequestDto();
        oldIncome.setAmount(50);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepo.findByUsers(user)).thenReturn(account);
        when(restTemplate.exchange(contains("/getById/"), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(oldIncome), HttpStatus.OK));
        when(restTemplate.exchange(contains("/update/"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(AESUtil.encrypt("Updated"), HttpStatus.OK));

        String result = service.updateIncome(userId, dto, incomeId);
        assertEquals("Updated", result);
        assertEquals(250.0, account.getTotalIncome());
    }

    @Test
    void addExpense() throws Exception {
        Long userId = 2L;
        ExpensesDto dto = new ExpensesDto();
        dto.setAmount(50);
        Users user = new Users();
        Account account = new Account();
        account.setTotalExpenses(20);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepo.findByUsers(user)).thenReturn(account);
        when(restTemplate.exchange(contains("/add"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(AESUtil.encrypt("Expense added"), HttpStatus.OK));

        String result = service.addExpense(userId, dto);
        assertEquals("Expense added", result);
        assertEquals(70.0, account.getTotalExpenses());
    }

    @Test
    void updateExpense() throws Exception {
        Long userId = 2L;
        Long expenseId = 99L;
        ExpensesDto dto = new ExpensesDto();
        dto.setAmount(30);
        Users user = new Users();
        Account account = new Account();
        account.setTotalExpenses(100);

        ExpensesRequestDto oldExpense = new ExpensesRequestDto();
        oldExpense.setAmount(50);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepo.findByUsers(user)).thenReturn(account);
        when(restTemplate.exchange(contains("/getById/"), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(oldExpense), HttpStatus.OK));
        when(restTemplate.exchange(contains("/update/"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(AESUtil.encrypt("Expense updated"), HttpStatus.OK));

        String result = service.updateExpense(userId, dto, expenseId);
        assertEquals("Expense updated", result);
        assertEquals(80.0, account.getTotalExpenses());
    }

    @Test
    void testAddIncomeUserNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourcenotFoundException.class, () -> service.addIncome(1L, new IncomeDto()));
    }

    @Test
    void testAddExpenseUserNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourcenotFoundException.class, () -> service.addExpense(1L, new ExpensesDto()));
    }
}
