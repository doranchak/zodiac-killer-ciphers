import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CipherComponent } from './cipher.component';

describe('CipherComponent', () => {
  let component: CipherComponent;
  let fixture: ComponentFixture<CipherComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CipherComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CipherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
