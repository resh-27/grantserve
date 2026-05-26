import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerAssignmentComponent } from './manager-assignment';

describe('ManagerAssignment', () => {
  let component: ManagerAssignmentComponent;
  let fixture: ComponentFixture<ManagerAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerAssignmentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ManagerAssignmentComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
