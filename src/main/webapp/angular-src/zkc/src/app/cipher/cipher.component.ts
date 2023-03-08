import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';
import * as myGlobals from 'globals';

@Component({
  selector: 'app-cipher',
  templateUrl: './cipher.component.html',
  styleUrls: ['./cipher.component.css']
})
export class CipherComponent implements OnInit {
  //cipher;
  g
  count: number = 0
  constructor(private apiService: ApiService) { }

  text(): string {
    if (myGlobals.cipher == null) return "";
    return myGlobals.cipher["ciphertextRaw"];
  }
  name(): string {
    if (myGlobals.cipher == null) return "";
    return myGlobals.cipher["name"];
  }
  description(): string {
    if (myGlobals.cipher == null) return "";
    return myGlobals.cipher["description"];
  }
  ngOnInit(): void {
    this.switchCipher("z340");
  }

  splitCipher(): any {
    return "";
  }

  switchCipher(name) {
    console.log("got here");
    this.apiService.getCipher(name).subscribe((data)=>{
      //this.cipher = data;
      myGlobals.cipher = data;
      this.g = myGlobals;
      myGlobals.cipher["ciphertextRaw"] = myGlobals.cipher["ciphertextRaw"]
      myGlobals.cipher["ciphertextGridSplit"] = this.splitCipher()
      console.log(myGlobals.cipher)
    });
  }

}
