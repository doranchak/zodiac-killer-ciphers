import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpParams } from  "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  baseurl = "http://localhost:8080"
  constructor(private httpClient: HttpClient) { }
  public getCipher(name){
    console.log("getCipher");
    let response = this.httpClient.get(this.baseurl + "/cipher?name=" + name);
    console.log(response);
    return response;
  }
  public getPivots(ciphertext: string, length: number){
    console.log("getPivots");
    //let response = this.httpClient.get("http://localhost:8080/cipher");
    const params = new HttpParams()
    .set('cipher', encodeURIComponent(ciphertext))
    .set('minsize',length.toString());
        let response = this.httpClient.post(this.baseurl + "/pivots", null, {'params' : params})
    console.log("response: " + response);
    return response;
  }
  public hom(ciphertext: string, length: number){
    console.log("hom");
    const params = new HttpParams()
    .set('cipher', encodeURIComponent(ciphertext))
    .set('length',length.toString());
        let response = this.httpClient.post(this.baseurl + "/hom", null, {'params' : params})
    console.log("response: " + response);
    return response;
  }

}
