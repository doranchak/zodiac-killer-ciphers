import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../api.service';
import * as myGlobals from 'globals';

@Component({
  selector: 'app-pivots',
  templateUrl: './pivots.component.html',
  styleUrls: ['./pivots.component.css']
})
export class PivotsComponent implements OnInit {
  //@Input() cipher: string
  g;
  summary;
  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.g = myGlobals;
    this.find(3)
  }

  find(length): void {
    console.log("finding for " + myGlobals.cipher["ciphertextRaw"]);
    this.apiService.getPivots(myGlobals.cipher["ciphertextRaw"], length).subscribe((data)=>{
      this.summary = data;
      console.log(data);
    });
  }

  selectedPivot(ngram): void {
    console.log("selected pivot with ngram " + ngram);
  }

}
