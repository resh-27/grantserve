import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramApplicationsComponent } from './program-applications.component';

describe('ProgramApplicationsComponenet', () => {
  let component: ProgramApplicationsComponent;
  let fixture: ComponentFixture<ProgramApplicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgramApplicationsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramApplicationsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
