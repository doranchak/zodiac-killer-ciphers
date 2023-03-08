import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PivotsComponent } from './pivots.component';

describe('PivotsComponent', () => {
  let component: PivotsComponent;
  let fixture: ComponentFixture<PivotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PivotsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PivotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
