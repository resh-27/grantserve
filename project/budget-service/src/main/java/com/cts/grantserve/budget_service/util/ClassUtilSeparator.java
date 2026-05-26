package com.cts.grantserve.budget_service.util;

import com.cts.grantserve.budget_service.dto.*;
import com.cts.grantserve.budget_service.entity.*;
import lombok.Data;

@Data
public class ClassUtilSeparator {

    public static Budget budgetUtil(BudgetDto budgetDto) {
        Budget budget = new Budget();

        budget.setBudgetID(budgetDto.budgetID());
        budget.setStatus(budgetDto.status());
        budget.setSpentAmount(budgetDto.spentAmount());
        budget.setAllocatedAmount(budgetDto.allocatedAmount());
        budget.setRemainingAmount(budgetDto.remainingAmount());

        return budget;
    }

    public static BudgetDto convertToDto(Budget budget) {
        return new BudgetDto(
                budget.getBudgetID(),
                budget.getAllocatedAmount(),
                budget.getSpentAmount(),
                budget.getRemainingAmount(),
                budget.getStatus(),
                budget.getProgramId()
        );
    }

}
