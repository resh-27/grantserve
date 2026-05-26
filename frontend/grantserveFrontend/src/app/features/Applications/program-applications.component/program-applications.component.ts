import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common'; // Import CommonModule for pipes/directives
import { ApplicationsService } from '../service/applications.service';
import { ApplicationCard } from '../application-card/application-card';
import { ManagerHeaderComponent } from '../../../shared/components/navigation/manager-header.component/manager-header.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastService } from '../../../shared/services/toast.service';

@Component({
  selector: 'app-program-applications',
  standalone: true,
  // Add the components and modules you use in your HTML here
  imports: [CommonModule, ApplicationCard, ManagerHeaderComponent],
  templateUrl: './program-applications.componenet.html',
  styleUrl: './program-applications.component.css',
})
export class ProgramApplicationsComponent implements OnInit {
  programID: number;
  applications: any[] = [];

  constructor(
    private applicationsService: ApplicationsService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private router: Router,
    private toast: ToastService,
    private location : Location
  ) {
    const programID = this.route.snapshot.paramMap.get('id');
    const isOnlyDigits = (str: string) => /^\d+$/.test(str);
    this.programID = Number(programID);

    if (!programID || !isOnlyDigits(programID)) {
      this.toast.show('Invalid program ID', 'danger');
      this.router.navigate(['/manager/programs']);
    }
  }

  ngOnInit(): void {
    this.loadApplications();
  }
  
  goBack() {
    this.location.back();
  }

  loadApplications(): void {
    // Basic validation to ensure programID exists
    if (this.programID !== undefined && this.programID !== null) {
      this.applicationsService.fetchApplications(this.programID).subscribe({
        next: (data) => {
          this.applications = data;
          console.log("Fetched applications:", this.applications);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error("Fetch failed:", err);
        }
      });
    }
    else {
      console.error("Invalid programID:", this.programID);
    }
  }
}