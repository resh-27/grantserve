import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControlOptions,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramService } from '../service/program.service';
import { finalize, Observable } from 'rxjs';
import { ManagerHeaderComponent } from '../../../shared/components/navigation/manager-header.component/manager-header.component';
import { Program } from '../model/program.model';
import { ToastService } from '../../../shared/services/toast.service';

@Component({
  selector: 'app-program-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ManagerHeaderComponent],
  templateUrl: './program-form.component.html',
  styleUrl: './program-form.component.css',
})
export class ProgramFormComponent implements OnInit {
  programForm!: FormGroup;
  isEditMode = false;
  programId: string | null = null;
  isSubmitting = false;
  serverErrors: { [key: string]: string } = {};

  constructor(
    private fb: FormBuilder,
    private toast: ToastService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private programService: ProgramService,
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.programId = this.route.snapshot.paramMap.get('id');
    if (this.programId) {
      this.isEditMode = true;
      this.loadProgramData(this.programId);
    }
  }

  get hasServerErrors(): boolean {
    return Object.keys(this.serverErrors).length > 0;
  }

  private initForm(): void {
    this.programForm = this.fb.group(
      {
        title: ['', [Validators.required, Validators.maxLength(200)]],
        description: ['', [Validators.required, Validators.maxLength(5000)]],
        startDate: ['', [Validators.required, this.futureDateValidator]],
        endDate: ['', Validators.required],
      },
      {
        validators: [this.dateRangeValidator],
      } as AbstractControlOptions,
    );
  }

  dateRangeValidator(group: FormGroup): any {
    const start = group.get('startDate')?.value;
    const end = group.get('endDate')?.value;
    return start && end && (new Date(start) > new Date(end) ? { invalidDate: true } : null);
  }

  futureDateValidator(control: any): { [key: string]: any } | null {
    if (!control.value) return null;

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Normalize to start of the day
    const selectedDate = new Date(control.value);
    selectedDate.setHours(0, 0, 0, 0); // Normalize to start of the day
    return selectedDate < today ? { pastDate: true } : null;
  }

  get minDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  private loadProgramData(id: string): void {
    // 1. Ensure the observable is typed as a Page/any or array based on your latest service
    this.programService.searchPrograms(undefined, Number(id)).subscribe((response: any) => {
      // 2. Access the content array (since your backend now returns a Page object)
      const programs: Program[] = response.content || [];

      // 3. Explicitly type 'p' as Program to resolve the error
      const prog = programs.find((p: Program) => String(p.programID) === String(id));

      if (prog) {
        this.programForm.patchValue({
          title: prog.title,
          description: prog.description,
          startDate: prog.startDate,
          endDate: prog.endDate,
        });
      } else {
        this.toast.show(`No program found with ID: ${id}`, 'danger');
        this.router.navigate(['/manager/programs']);
      }
    });
  }

  cancel(): void {
    if (!this.isSubmitting) {
      this.router.navigate(['/manager/programs']);
    }
  }

  handleSave(status: 'DRAFT' | 'ACTIVE'): void {
    if (this.programForm.invalid) {
      this.programForm.markAllAsTouched();
      // this.serverErrors = { message: "Please fill in all required fields correctly." };
      this.toast.show('Please fix the errors in the form.', 'warning');
      return;
    }

    this.isSubmitting = true;
    this.serverErrors = {};

    const payload = {
      ...this.programForm.value,
      status: 'DRAFT',
      programID: this.programId || undefined,
      budget: 0,
    };

    const request: Observable<any> = this.isEditMode
      ? this.programService.updateProgram(payload)
      : this.programService.createProgram(payload);

    request
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
        }),
      )
      .subscribe({
        next: (response) => {
          if (status === 'ACTIVE') {
            const idToPass = this.isEditMode ? this.programId : (response as any).programID;
            this.toast.show('Draft saved successfully!', 'success');
            this.router.navigate(['/manager/programs/add-budget'], { queryParams: { id: idToPass } });
          } else {
            this.toast.show('Draft saved successfully!', 'success');
            this.router.navigate(['/manager/programs']);
          }
        },
        error: (err: any) => {
          // console.error("Full Error Response:", err);
          this.mapServerErrors(err);
          if (this.serverErrors['message']) {
            this.toast.show(this.serverErrors['message'], 'danger');
          }
        },
      });
  }

  private mapServerErrors(err: any): void {
    let errorData = err?.error;

    if (err.status === 404) {
      this.toast.show('This program no longer exists.', 'danger');
      this.router.navigate(['/manager/programs']);
    } else if (err.status === 500 || err.status === 503) {
      this.toast.show('Server error. Please try again later.', 'danger');
      this.serverErrors = { message: 'The server is currently unable to process your request.' };
    } else if (err.status === 400 && errorData?.message) {
      // If it's a specific Business Logic error (e.g. "Program already closed")
      this.toast.show(errorData.message, 'danger');
      this.serverErrors = { message: errorData.message };
    }

    // Plain text error
    if (typeof errorData === 'string') {
      try {
        errorData = JSON.parse(errorData);
      } catch {
        this.serverErrors = { message: errorData };
        return;
      }
    }

    // Spring validation format
    if (Array.isArray(errorData?.errors)) {
      this.serverErrors = {};
      errorData.errors.forEach((e: any) => {
        if (e.field && e.defaultMessage) {
          this.serverErrors[e.field] = e.defaultMessage;
        }
      });
      return;
    }

    // Field map errors
    if (typeof errorData === 'object' && errorData !== null && !errorData.message) {
      this.serverErrors = errorData;
      return;
    }

    // Generic message
    this.serverErrors = {
      message: errorData?.message || 'Something went wrong. Please try again.',
    };
  }
}
