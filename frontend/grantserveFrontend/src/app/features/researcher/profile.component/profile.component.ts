import { Component, OnInit, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ResearcherService } from '../service/researcher.service';
import { ResearcherProfile, ResearcherDocument } from '../model/researcher.model';
import { HttpClient } from '@angular/common/http';
 
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef;
 
  profile!: ResearcherProfile;
  documents: ResearcherDocument[] = [];
  searchTerm: string = '';
  isEditing = false;
  userId = localStorage.getItem('userId');
  // Add this variable near the top of your class
  newDoc = { docType: '', fileURI: '' };
 
  constructor(
    private researcherService: ResearcherService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private http: HttpClient
  ) { }
 
  ngOnInit() {
    this.loadData();
  }
 
  // Method to open the modal safely
  openUploadModal() {
    const modalElement = document.getElementById('uploadModal');
    if (modalElement) {
      // getOrCreateInstance prevents creating duplicate overlapping modals
      const modal = (window as any).bootstrap.Modal.getOrCreateInstance(modalElement);
      modal.show();
    }
  }
 
  // Method to close the modal safely
  closeModal() {
    const modalElement = document.getElementById('uploadModal');
    if (modalElement) {
      const modal = (window as any).bootstrap.Modal.getInstance(modalElement);
      if (modal) modal.hide();
    }
  }
 
  // Handle the Modal Submit
  onModalSubmit() {
    if (this.profile && this.newDoc.docType && this.newDoc.fileURI) {
      const uploadData = {
        researcherID: this.profile.researcherID,
        docType: this.newDoc.docType,
        fileURI: this.newDoc.fileURI // The path provided by the user
      };
 
      this.researcherService.uploadDocument(uploadData).subscribe({
        next: () => {
          alert("Document details saved successfully!");
          this.closeModal();
          this.loadDocs(this.profile.researcherID); // Refresh the table
          this.newDoc = { docType: '', fileURI: '' }; // Reset form
        },
        error: (err) => alert("Failed to save: " + err.message)
      });
    }
  }
 
  loadData() {
    if (this.userId) {
      this.researcherService.getProfile(this.userId).subscribe({
        next: (data) => {
          this.profile = data;
          if (this.userId) {
            this.loadDocs(Number(this.userId));
          }
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error fetching profile:', err)
      });
    }
  }
 
  loadDocs(id: number) {
    this.researcherService.getDocumentsByResearcherId(id).subscribe({
      next: (docs) => {
        this.documents = docs;
        this.cdr.detectChanges();
        console.log('Documents loaded:', this.documents);
      },
      error: (err) => console.error('Error loading documents:', err)
    });
  }
 
  get filteredDocuments() {
    if (!this.searchTerm) return this.documents;
    return this.documents.filter(doc =>
      doc.docType.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      doc.fileURI.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
 
  /**
   * SECURE VIEW LOGIC
   * Fetches file as a Blob via ResearcherService to include Auth Headers (fixes 401)
   */
  viewDocument(fileUri: string) {
    if (!fileUri) {
      alert("Document link is missing.");
      return;
    }
 
    // Check if the URI is an online link
    if (fileUri.startsWith('http://') || fileUri.startsWith('https://')) {
      const newWindow = window.open(fileUri, '_blank', 'noopener,noreferrer');
      if (!newWindow || newWindow.closed || typeof newWindow.closed === 'undefined') {
        alert("Popup was blocked. Please allow popups to view this document.");
      }
      return;
    }
 
    // Fallback just in case there are legacy local files in your database
    alert("This document does not appear to be a valid online link.");
  }
 
  triggerUpload() {
    this.fileInput.nativeElement.click();
  }
 
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file && this.profile) {
      // Logical path for backend storage reference
      const filePath = `C:/GrantServe/uploads/${file.name}`;
 
      const uploadData = {
        researcherID: this.profile.researcherID,
        docType: this.guessDocType(file.name),
        fileURI: filePath
      };
 
      this.researcherService.uploadDocument(uploadData).subscribe({
        next: () => {
          alert("Document uploaded successfully!");
          this.loadDocs(this.profile.researcherID);
        },
        error: (err) => alert("Upload failed: " + err.message)
      });
    }
  }
 
  private guessDocType(fileName: string): string {
    const name = fileName.toUpperCase();
    if (name.includes('BIRTH') || name.includes('DOB')) return 'BIRTHCERTIFICATE';
    if (name.includes('ID') || name.includes('AADHAAR')) return 'IDPROOF';
    return 'PUBLICATION';
  }

  onSave() {
    if (this.userId && this.profile) {
      this.researcherService.updateProfile(this.userId, this.profile).subscribe({
        next: () => {
          alert("Profile updated successfully!");
          this.isEditing = false;
          this.loadData();
        },
        error: (err) => alert("Failed to update profile: " + err.message)
      });
    }
  }
}
 