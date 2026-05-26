import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgramDashboardComponent } from './program-dashboard.component';

describe('ProgramDashboardComponent', () => {
  let component: ProgramDashboardComponent;
  let fixture: ComponentFixture<ProgramDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgramDashboardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramDashboardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
