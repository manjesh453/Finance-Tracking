package com.income.controller;

import com.income.helper.DateDtos;
import com.income.helper.IncomeDto;
import com.income.service.IncomeService;
import com.income.shared.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping("/add")
    public String addIncome(@RequestBody String income) {
        return incomeService.addIncome(income);
    }

    @PostMapping("/update/{incomeId}")
    public String updateIncome(@RequestBody String income, @PathVariable Long incomeId) {
        return incomeService.updateIncome(income, incomeId);
    }

    @GetMapping("/delete/{incomeId}")
    public String deleteIncome(@PathVariable Long incomeId) {
        return incomeService.deleteIncome(incomeId);
    }

    @GetMapping("/getAllIncome/{receiverId}")
    public List<IncomeDto> getAllIncome(@PathVariable Long receiverId) {
        return incomeService.getAllIncome(receiverId);
    }

    @GetMapping("/getRecentIncomeHistory/{receiverId}")
    public List<IncomeDto> getRecentIncomeHistory(@PathVariable Long receiverId) {
        return incomeService.getRecentIncomeHistory(receiverId);
    }

    @PostMapping("/getByDate")
    public List<IncomeDto> filterByDate(@RequestBody DateDtos dateDto) {
        return incomeService.getIncomeByDate(dateDto.getStartDate(), dateDto.getEndDate(), dateDto.getReceiverId());
    }

    @PostMapping("/getByReceiverIdAndStatus/{receiverId}")
    public List<IncomeDto> getIncomeByReceiverId(@PathVariable Long receiverId, @RequestBody String status) {
        return incomeService.getByReceiverId(receiverId, Status.valueOf(status));
    }

    @GetMapping("/test")
    public String testMe() {
        return "I am income";
    }

    @GetMapping("/getById/{incomeId}")
    public IncomeDto getIncomeById(@PathVariable Long incomeId) {
        return incomeService.getIncomeById(incomeId);
    }

}
