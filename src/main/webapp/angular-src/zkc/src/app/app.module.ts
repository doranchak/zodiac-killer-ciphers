import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';

import { HttpClientModule } from '@angular/common/http';
import { CipherComponent } from './cipher/cipher.component';
import { AppRoutingModule } from './app-routing.module';
import { PivotsComponent } from './pivots/pivots.component';
import { HomComponent } from './hom/hom.component';
import { Z340TranspositionHelperComponent } from './z340-transposition-helper/z340-transposition-helper.component';

@NgModule({
  declarations: [
    AppComponent,
    CipherComponent,
    PivotsComponent,
    HomComponent,
    Z340TranspositionHelperComponent
  ],
  imports: [
    BrowserModule,
    // import HttpClientModule after BrowserModule.
    HttpClientModule,
    AppRoutingModule,    
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
