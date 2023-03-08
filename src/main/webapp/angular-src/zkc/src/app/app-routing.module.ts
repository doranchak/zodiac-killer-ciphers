import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CipherComponent } from './cipher/cipher.component';
import { PivotsComponent } from './pivots/pivots.component';
import { HomComponent } from './hom/hom.component';
import { Z340TranspositionHelperComponent } from './z340-transposition-helper/z340-transposition-helper.component';

const routes: Routes = [
  {path:'cipher', component: CipherComponent},
  {path:'pivots', component: PivotsComponent},
  {path:'hom', component: HomComponent},
  {path:'z340tc', component: Z340TranspositionHelperComponent}
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes)    
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule { }
