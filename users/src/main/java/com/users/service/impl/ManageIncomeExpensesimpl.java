package com.users.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.entity.Account;
import com.users.entity.Users;
import com.users.exception.NotFoundException;
import com.users.exception.ResourcenotFoundException;
import com.users.helper.ExpensesDto;
import com.users.helper.ExpensesRequestDto;
import com.users.helper.IncomeDto;
import com.users.helper.IncomeRequestDto;
import com.users.repo.AccountRepo;
import com.users.repo.UserRepo;
import com.users.security.service.serviceimpl.AESUtil;
import com.users.service.ManageIncomeExpenses;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ManageIncomeExpensesimpl implements ManageIncomeExpenses {
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final ObjectMapper objectMapper;
    @Qualifier("restTemplate")
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    @Value("${gateway.baseURL}")
    private String gateWayBaseUrl;
    @Value("${income.Routes}")
    private String incomeUrl;
    @Value("${expenses.Routes}")
    private String expensesUrl;

    @SneakyThrows
    @Override
    public String addIncome(Long userId, IncomeDto requestDto) {
        Users users = userRepo.findById(userId).orElseThrow(() -> new ResourcenotFoundException("User", "UserId", userId));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jwt = getJwtFromContext();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
        }
        IncomeRequestDto incomeRequestDto=modelMapper.map(requestDto, IncomeRequestDto.class);
        incomeRequestDto.setUserId(userId);
        String url = gateWayBaseUrl+incomeUrl+ "add";
        String encyptData= AESUtil.encrypt(objectMapper.writeValueAsString(incomeRequestDto));
        HttpEntity<String> entity = new HttpEntity<>(encyptData, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Income cannot be added");
        }
        Account account = accountRepo.findByUsers(users);
        account.setTotalIncome(account.getTotalIncome() + requestDto.getAmount());
        accountRepo.save(account);
        return  AESUtil.decrypt(response.getBody());
    }

    @Override
    @SneakyThrows
    public String updateIncome(Long userId, IncomeDto requestDto, Long incomeId) {
        Users users = userRepo.findById(userId).orElseThrow(() -> new ResourcenotFoundException("User", "UserId", userId));
       //send to fetch income detail
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jwt = getJwtFromContext();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
        }
        ResponseEntity<String> response = restTemplate.exchange(
                gateWayBaseUrl+incomeUrl+ "/getById/"+incomeId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Income cannot be added");
        }
        IncomeRequestDto incomeDetails=objectMapper.readValue(response.getBody(),IncomeRequestDto.class);
        //send to update income
        IncomeRequestDto incomeRequestDto=modelMapper.map(requestDto, IncomeRequestDto.class);
        incomeRequestDto.setUserId(userId);
        String encyptData= AESUtil.encrypt(objectMapper.writeValueAsString(incomeRequestDto));
        HttpEntity<String> entity = new HttpEntity<>(encyptData, headers);
        ResponseEntity<String> responses = restTemplate.exchange(
                gateWayBaseUrl+incomeUrl+ "/update/"+incomeId,
                HttpMethod.POST,
                entity,
                String.class
        );
        if(responses.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Income cannot be added");
        }
        Account account=accountRepo.findByUsers(users);
        account.setTotalIncome(account.getTotalIncome() -incomeDetails.getAmount()+ requestDto.getAmount());
        accountRepo.save(account);
        return  AESUtil.decrypt(responses.getBody());
    }

    @SneakyThrows
    @Override
    public String addExpense(Long userId, ExpensesDto requestDto) {
        Users users = userRepo.findById(userId).orElseThrow(() -> new ResourcenotFoundException("User", "UserId", userId));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jwt = getJwtFromContext();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
        }
        ExpensesRequestDto expensesRequestDto=modelMapper.map(requestDto, ExpensesRequestDto.class);
        expensesRequestDto.setReceiverId(userId);
        String encyptData= AESUtil.encrypt(objectMapper.writeValueAsString(expensesRequestDto));
        HttpEntity<String> entity = new HttpEntity<>(encyptData, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                gateWayBaseUrl + expensesUrl + "add",
                HttpMethod.POST,
                entity,
                String.class
        );
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Income cannot be added");
        }
        Account account = accountRepo.findByUsers(users);
        account.setTotalExpenses(account.getTotalExpenses() + requestDto.getAmount());
        accountRepo.save(account);
        return  AESUtil.decrypt(response.getBody());
    }

    @Override
    @SneakyThrows
    public String updateExpense(Long userId, ExpensesDto requestDto, Long expensesId) {
        Users users = userRepo.findById(userId).orElseThrow(() -> new ResourcenotFoundException("User", "UserId", userId));
        //send to fetch expenses detail
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jwt = getJwtFromContext();
        if (jwt != null) {
            headers.setBearerAuth(jwt);
        }
        ResponseEntity<String> response = restTemplate.exchange(
                gateWayBaseUrl+expensesUrl+ "/getById/"+expensesId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Expenses cannot be found");
        }
        ExpensesRequestDto expensesDetails=objectMapper.readValue(response.getBody(),ExpensesRequestDto.class);
        //send to update expenses
        ExpensesRequestDto expensesRequestDto=modelMapper.map(requestDto, ExpensesRequestDto.class);
        expensesRequestDto.setReceiverId(userId);
        String encyptData= AESUtil.encrypt(objectMapper.writeValueAsString(expensesRequestDto));
        HttpEntity<String> entity = new HttpEntity<>(encyptData, headers);
        ResponseEntity<String> responses = restTemplate.exchange(
                gateWayBaseUrl+incomeUrl+ "/update/"+expensesId,
                HttpMethod.POST,
                entity,
                String.class
        );
        if(responses.getStatusCode() != HttpStatus.OK) {
            throw new NotFoundException("Expenses cannot be update");
        }
        Account account=accountRepo.findByUsers(users);
        account.setTotalExpenses(account.getTotalExpenses() -expensesDetails.getAmount()+ requestDto.getAmount());
        accountRepo.save(account);
        return  AESUtil.decrypt(responses.getBody());
    }

    public String getJwtFromContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof String jwt) {
            return jwt;
        }
        return null;
    }
}
