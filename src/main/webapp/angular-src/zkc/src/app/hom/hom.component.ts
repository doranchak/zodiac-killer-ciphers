import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';
import * as myGlobals from 'globals';

@Component({
  selector: 'app-hom',
  templateUrl: './hom.component.html',
  styleUrls: ['./hom.component.css']
})
export class HomComponent implements OnInit {
  summary
  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.find(2);
  };

  find(length: number): void {
    this.summary = null;
    this.apiService.hom(myGlobals.cipher["ciphertextRaw"], length).subscribe((data)=>{
      console.log("ct: " + myGlobals.cipher["ciphertextRaw"]);
      console.log(data);
      this.summary = data;
    })
  }
}
