package com.cts.grantserve.budget_service.entity;

import com.cts.grantserve.budget_service.enums.BudgetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long budgetID;

    private Double allocatedAmount;
    private Double spentAmount;
    private Double remainingAmount;

    @Enumerated(EnumType.STRING)
    private BudgetStatus status;

//    @OneToOne
//    @JoinColumn(name = "program_id")
//    @JsonBackReference
    private Long programId;

}
