import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Z340TranspositionHelperComponent } from './z340-transposition-helper.component';

describe('Z340TranspositionHelperComponent', () => {
  let component: Z340TranspositionHelperComponent;
  let fixture: ComponentFixture<Z340TranspositionHelperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Z340TranspositionHelperComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Z340TranspositionHelperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
