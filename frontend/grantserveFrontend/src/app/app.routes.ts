import { Routes } from '@angular/router';
import { Login } from './features/Auth/component/login.component/login.component';
import { authGuard } from './core/guards/auth-guard';
import { ProgramComponent } from './features/program/program.component/program.component';
import { ProgramFormComponent } from './features/program/program-form.component/program-form.component';
import { AddBudgetComponent } from './features/program/add-budget.component/add-budget.component';
import { ReviewerDashboard } from './features/review/reviewer-dashboard/reviewer-dashboard.component';
import { ProgramListComponent } from './features/Applications/program-list.component/program-list.component';
import { ReviewFormComponent } from './features/review/reviewer-dashboard/review-form.component';
import { ViewProgramComponent } from './features/program/view-program.component/view-program.component';
import { ProfileComponent } from './features/researcher/profile.component/profile.component';
import { ResearcherDashboardComponent } from './features/researcher/researcherdashboard.component/researcherdashboard.component';
import { ManagerAssignmentComponent } from './features/review/manager-assignment/manager-assignment';
import { ProgramDashboardComponent } from './features/program/program-dashboard.component/program-dashboard.component';
import { ManagerEvaluationComponent } from './features/review/manager-evaluation/manager-evaluation';
import { provideState } from '@ngrx/store';
import * as applicationEffects from './features/Applications/applications.component/application.effects'; // Adjust path
import { countReducer } from './features/Applications/applications.component/application.reducer';
import { provideEffects } from '@ngrx/effects';
import { AuditComplianceLayoutComponent } from './features/audit-compliance/audit-compliance-layout/audit-compliance-layout.component';
import { NotFoundComponent } from './shared/components/ui/not-found.component/not-found.component';

export const routes: Routes = [
    {
        path: '',
        component: Login
    },
    {
        path: 'register',
        loadComponent: () => import('./features/Auth/component/register.comoponent/register.comoponent').then(m => m.RegisterComoponent)
    },
    // --- Researcher Routes (Inside Home Layout) ---
    {
        path: 'home',
        canActivate: [authGuard],
        data: { roles: ['RESEARCHER'] },
        loadComponent: () => import('./features/home-layout/home-layout').then(m => m.HomeLayout),
        children: [
            {
                path: 'register',
                loadComponent: () => import('./features/Auth/component/register.comoponent/register.comoponent').then(m => m.RegisterComoponent)
            },
            {
                path: 'dashboard',
                component: ResearcherDashboardComponent
            },
            {
                path: 'profile',
                component: ProfileComponent
            },
            {
                path: 'programs',
                component: ProgramListComponent
            },
            {
                path: 'programs/:id',
                component: ViewProgramComponent
            },
            // Default child route when you navigate to /home
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
        ]
    },
    {
        path: 'create-application/:programID/:researcherID',
        loadComponent: () => import('./features/New_Application/create-application/create-application').then(m => m.CreateApplication)
    },

    // --- Other Shared Routes ---
    {
        path: 'applications',
        canActivate: [authGuard],
        providers:[
            provideState({ name: 'applications', reducer:countReducer  }),
            provideEffects(applicationEffects)
        ],
        loadComponent: () => import('./features/Applications/applications.component/applications.component').then(m => m.ApplicationsComponent)
    },
    {
        path: 'proposals/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./features/Applications/proposal.component/proposal.component').then(m => m.ProposalComponent)
    },
    {
        path: 'disbursements',
        loadComponent: () => import('./features/disbursement/disbursement.component/disbursement.component')
            .then(m => m.DisbursementComponent)
    },

    // --- Manager & Admin Restricted Routes ---
    {
        path: 'manager',
        component: ProgramComponent,
        canActivate: [authGuard],
        canActivateChild: [authGuard],
        data: { roles: ['MANAGER', 'ADMIN'] },
        children: [
            {
                path: 'programs/:id/applications',
                loadComponent: () => import('./features/Applications/program-applications.component/program-applications.component').then(m => m.ProgramApplicationsComponent),
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: '',
                component: ProgramDashboardComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'programs',
                component: ProgramDashboardComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'programs/add-budget',
                component: AddBudgetComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'programs/create',
                component: ProgramFormComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'programs/edit/:id',
                component: ProgramFormComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'programs/:id',
                component: ViewProgramComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'disbursements',
                loadComponent: () => import('./features/disbursement/manager-disbursement.component/manager-disbursement.component')
                    .then(m => m.ManagerDisbursementComponent),
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'assign',
                component: ManagerAssignmentComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
            {
                path: 'evaluation',
                component: ManagerEvaluationComponent,
                data: { roles: ['MANAGER', 'ADMIN'] }
            },
        ]
    },

    // --- Reviewer & Manager Specifics ---
    {
        path: 'reviewer-dashboard',
        component: ReviewerDashboard,
        canActivate: [authGuard],
        data: { roles: ['REVIEWER'] }
    },
    {
        path: 'review-form',
        component: ReviewFormComponent,
        canActivate: [authGuard],
        data: { roles: ['REVIEWER'] }
    },

    {
        path: 'compliance',
        component: AuditComplianceLayoutComponent,
        canActivate: [authGuard],
        data: { roles: ['AUDITOR', 'COMPLIANCE', 'ADMIN'] }
    },

    {
        path: 'reports',
        redirectTo: 'compliance',
        pathMatch: 'full'
    },

    // --- Fallback ---
    {
        path: '**',
        component: NotFoundComponent,
        title: 'Page Not Found'
    }
];