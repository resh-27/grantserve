package com.cts.grantserve.program_service.projection;

import com.cts.grantserve.program_service.entity.Program;
import com.cts.grantserve.program_service.enums.BudgetStatus;

public interface IBudgetProjection {
    Long getBudgetID();
    Program getProgramId();
    Double getAllocatedAmount();
    Double getSpentAmount();
    Double getRemainingAmount();
    BudgetStatus getStatus();
}