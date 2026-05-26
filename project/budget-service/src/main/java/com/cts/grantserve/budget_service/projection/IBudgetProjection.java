package com.cts.grantserve.budget_service.projection;

import com.cts.grantserve.budget_service.enums.BudgetStatus;

public interface IBudgetProjection {
    Long getBudgetID();
    Long getProgramId();
    Double getAllocatedAmount();
    Double getSpentAmount();
    Double getRemainingAmount();
    BudgetStatus getStatus();
}