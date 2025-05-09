package com.income.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.income.config.AESUtil;
import com.income.entity.Income;
import com.income.helper.IncomeDto;
import com.income.helper.IncomeRequestDto;
import com.income.repo.IncomeRepo;
import com.income.shared.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeImplTest {
    @Mock
    private IncomeRepo incomeRepo;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private IncomeImpl incomeService;


    @Test
    void addIncome() throws JsonProcessingException {
        String encrypted = "encryptedIncome";
        String decryptedJson = "{\"amount\":100.0,\"remark\":\"Test\"}";
        Income income = new Income();
        income.setAmount(100f);
        income.setRemark("Test");

        try (MockedStatic<AESUtil> aesMocked = mockStatic(AESUtil.class)) {
            aesMocked.when(() -> AESUtil.decrypt(encrypted)).thenReturn(decryptedJson);
            aesMocked.when(() -> AESUtil.encrypt(anyString())).thenReturn("encryptedSuccess");

            when(objectMapper.readValue(decryptedJson, Income.class)).thenReturn(income);
            when(incomeRepo.saveAndFlush(any(Income.class))).thenReturn(income);

            String result = incomeService.addIncome(encrypted);
            assertEquals("encryptedSuccess", result);
        }
    }

    @Test
    void updateIncome() throws Exception {
        Long incomeId = 1L;
        String encrypted = "encryptedUpdateIncome";
        String decryptedJson = "{\"amount\":200.0,\"remark\":\"Updated\"}";

        IncomeRequestDto requestDto = new IncomeRequestDto();
        requestDto.setAmount(200f);
        requestDto.setRemark("Updated");

        Income existingIncome = new Income();
        existingIncome.setId(incomeId);

        try (MockedStatic<AESUtil> aesMocked = mockStatic(AESUtil.class)) {
            when(AESUtil.decrypt(encrypted)).thenReturn(decryptedJson);
            when(objectMapper.readValue(decryptedJson, IncomeRequestDto.class)).thenReturn(requestDto);
            when(incomeRepo.findById(incomeId)).thenReturn(Optional.of(existingIncome));
            when(AESUtil.encrypt(anyString())).thenReturn("encryptedUpdated");

            String result = incomeService.updateIncome(encrypted, incomeId);
            assertEquals("encryptedUpdated", result);
        }
    }

    @Test
    void deleteIncome() {
        Long incomeId = 1L;
        Income income = new Income();
        income.setId(incomeId);

        when(incomeRepo.findById(incomeId)).thenReturn(Optional.of(income));
        String result = incomeService.deleteIncome(incomeId);

        assertEquals("You have successfully deleted income", result);
        assertEquals(Status.DELETE, income.getStatus());
    }

    @Test
    void getAllIncome() {
        Long userId = 1L;
        Income income = new Income();
        income.setId(1L);
        income.setAmount(10);

        when(incomeRepo.findByUserId(userId)).thenReturn(List.of(income));

        List<IncomeDto> result = incomeService.getAllIncome(userId);
        assertEquals(1, result.size());
    }

    @Test
    void getIncomeByDate() {
        Long userId = 1L;
        Date start = new Date();
        Date end = new Date();
        Income income = new Income();
        income.setAmount(10);

        when(incomeRepo.findByCreatedDateBetweenAndUserId(start, end, userId)).thenReturn(List.of(income));

        List<IncomeDto> result = incomeService.getIncomeByDate(start, end, userId);
        assertEquals(1, result.size());
    }

    @Test
    void getByReceiverId() {
        Long userId = 1L;
        Status status = Status.ACTIVE;
        Income income = new Income();
        income.setAmount(10);

        when(incomeRepo.findByUserIdAndStatus(userId, status)).thenReturn(List.of(income));

        List<IncomeDto> result = incomeService.getByReceiverId(userId, status);
        assertEquals(1, result.size());
    }

    @Test
    void getRecentIncomeHistory() {
        Long userId = 1L;
        Income income = new Income();
        income.setAmount(10);

        when(incomeRepo.findRecentIncomeByUserId(eq(userId), any(PageRequest.class))).thenReturn(List.of(income));

        List<IncomeDto> result = incomeService.getRecentIncomeHistory(userId);
        assertEquals(1, result.size());
    }

    @Test
    void getIncomeById() {
        Long incomeId = 1L;
        Income income = new Income();
        income.setId(incomeId);
        income.setAmount(10);
        income.setRemark("Test");

        IncomeDto incomeDto=new IncomeDto();
        incomeDto.setId(incomeId);
        when(incomeRepo.findById(incomeId)).thenReturn(Optional.of(income));
        when(modelMapper.map(any(Income.class), eq(IncomeDto.class))).thenReturn(incomeDto);

        IncomeDto result = incomeService.getIncomeById(incomeId);
        assertEquals(incomeId, result.getId());
    }
}
