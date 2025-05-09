package com.income.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.income.config.AESUtil;
import com.income.entity.Income;
import com.income.exception.ResourcenotFoundException;
import com.income.helper.IncomeDto;
import com.income.helper.IncomeRequestDto;
import com.income.repo.IncomeRepo;
import com.income.service.IncomeService;
import com.income.shared.Status;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeImpl implements IncomeService {
    private final IncomeRepo incomeRepo;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public String addIncome(String incomeDto) {
        String decryptPayload= AESUtil.decrypt(incomeDto);
        Income income = objectMapper.readValue(decryptPayload, Income.class);
        income.setId(null);
        income.setStatus(Status.ACTIVE);
        incomeRepo.saveAndFlush(income);
        return AESUtil.encrypt("You have successfully added income");
    }

    @Override
    @SneakyThrows
    public String updateIncome(String incomeDto, Long incomeId) {
        String decryptPayload= AESUtil.decrypt(incomeDto);
        IncomeRequestDto requestDto = objectMapper.readValue(decryptPayload, IncomeRequestDto.class);
        Income income = incomeRepo.findById(incomeId).orElseThrow(() -> new ResourcenotFoundException("Income", "incomeId", incomeId));
        income.setAmount(requestDto.getAmount());
        income.setRemark(requestDto.getRemark());
        return AESUtil.encrypt("You have successfully updated income");
    }

    @Override
    public String deleteIncome(Long incomeId) {
        Income income = incomeRepo.findById(incomeId).orElseThrow(() -> new ResourcenotFoundException("Income", "incomeId", incomeId));
        income.setStatus(Status.DELETE);
        return "You have successfully deleted income";
    }

    @Override
    public List<IncomeDto> getAllIncome(Long userId) {
        List<Income>incomeList=incomeRepo.findByUserId(userId);
        return incomeList.stream().map(list -> modelMapper.map(list, IncomeDto.class)).toList();
    }

    @Override
    public List<IncomeDto> getIncomeByDate(Date startDate, Date endDate, Long userId) {
        List<Income>incomeList=incomeRepo.findByCreatedDateBetweenAndUserId(startDate,endDate,userId);
        return incomeList.stream().map(list -> modelMapper.map(list, IncomeDto.class)).toList();
    }

    @Override
    public List<IncomeDto> getByReceiverId(Long userId,Status status) {
        List<Income>incomeList=incomeRepo.findByUserIdAndStatus(userId,status);
        return incomeList.stream().map(list -> modelMapper.map(list, IncomeDto.class)).toList();
    }

    @Override
    public List<IncomeDto> getRecentIncomeHistory(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Income> recentIncomes = incomeRepo.findRecentIncomeByUserId(userId, pageRequest);
        return recentIncomes.stream().map(list -> modelMapper.map(list, IncomeDto.class)).toList();
    }

    @Override
    public IncomeDto getIncomeById(Long incomeId) {
        Income income = incomeRepo.findById(incomeId).orElseThrow(() -> new ResourcenotFoundException("Income", "incomeId", incomeId));
        return modelMapper.map(income,IncomeDto.class);
    }

}
