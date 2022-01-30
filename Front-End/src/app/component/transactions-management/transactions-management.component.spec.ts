import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionsManagementComponent } from './transactions-management.component';

describe('TransactionsManagementComponent', () => {
  let component: TransactionsManagementComponent;
  let fixture: ComponentFixture<TransactionsManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionsManagementComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionsManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});