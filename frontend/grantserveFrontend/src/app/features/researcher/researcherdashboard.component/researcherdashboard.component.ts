import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResearcherService } from '../service/researcher.service';
import { ResearcherProfile, ResearcherDocument } from '../model/researcher.model';

 
@Component({
  selector: 'app-researcherdashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './researcherdashboard.component.html',
  styleUrls: ['./researcherdashboard.component.css']
})
export class ResearcherDashboardComponent implements OnInit {
  today: Date = new Date();
  profile!: ResearcherProfile;
  documents: ResearcherDocument[] = [];
  grantCount: number = 0; // Set to 0 by default
  userId = localStorage.getItem('userId');
  router: any;
 
  constructor(
    private researcherService: ResearcherService,
    private cdr: ChangeDetectorRef
  ) { }
 
  ngOnInit() {
    this.fetchDashboardData();
    this.loadDocs(Number(this.userId));
    this.loadStats();
          
  }
  loadDocs(researcherId: number) {
    this.researcherService.getDocumentsByResearcherId(researcherId).subscribe({
      next: (docs) => {
        this.documents = docs;
        this.cdr.detectChanges();
        console.log('Documents loaded:', this.documents);
      },
      error: (err) => console.error('Error loading documents:', err)
    });
  }
 
  fetchDashboardData() {
    if (this.userId) {
      this.researcherService.getProfile(this.userId).subscribe({
        next: (data) => {
          this.profile = data;
          this.cdr.detectChanges();
        }
      });
    }
  }
 
 // researcherdashboard.component.ts

loadStats() {
  this.researcherService.getApplicationCount(Number(this.userId)).subscribe({
    next: (count) => {
      this.grantCount = count.All;
      console.log('Grant count loaded:', this.grantCount);
      this.cdr.detectChanges();
    }
  });




}
 
 get isEligible(): boolean {
   return this.documents.length > 0 && 
         this.documents.every(doc => doc.verificationStatus === 'Approved');
  }

  /**
   * PROGRAMMATIC NAVIGATION
   * Triggered by the (click) event in HTML
   */
  navigateToApplications() {
    if (this.isEligible) {
      this.router.navigate(['/home/my-applications']);
    } else {
      alert("Access Denied: You are not eligible to apply or view applications until all your documents are 'Approved'.");
    }
  }

  /**
   * DASHBOARD UI MAPPING
   * Determines the color and icon of the status card
   */
  get statusUI() {
    if (this.documents.length === 0) {
      return { label: 'PENDING', class: 'status-red', icon: 'bi-clock-fill' };
    }
 
    if (this.isEligible) {
      return { label: 'VERIFIED', class: 'status-green', icon: 'bi-patch-check-fill' };
    } else {
      return { label: 'PENDING', class: 'status-red', icon: 'bi-clock-fill' };
    }
  }


  // researcherdashboard.component.ts


}


 
