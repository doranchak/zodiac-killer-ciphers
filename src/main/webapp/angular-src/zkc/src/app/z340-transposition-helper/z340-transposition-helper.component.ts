import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-z340-transposition-helper',
  templateUrl: './z340-transposition-helper.component.html',
  styleUrls: ['./z340-transposition-helper.component.css']
})
export class Z340TranspositionHelperComponent implements OnInit {
  // transposed (original) z340
  z340: string = "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+"
  // untransposed z340
  z340u: string = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4Ef|pz/JNb>M)+l5||.VqL+Ut*5cUGR)VE5FVZ2cW+|TB45|TC^D4ct-c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG+BCOTBzF1K<SMF6N*(+HK29^:OFTO<Sf4pl/Ucy59^W(+l#2C.B)7<FBy-dkF|W<7t_BOYB*-CM>cHD8OZzSkpNA|K;+"
  // transposed (original) z340 plaintext
  z340p: string = "IRONCAOOIIERGRTMLECHETTATNWNNIAABWEITEOHSRTWTWGTAISDCCLOAPAOYCAHHOAMBNOHALPLEVFIHSEIUCPOOFAASALYIFNMNTTVHAUTTMSERTONAGETTMSBPTAHBENAHUGNLIOHEHROMFEEIDDEASAASOHOSHACLIFEISSHOUVLRENNECEROAAMIEAOSEAVREONHSEFADNDITHEEVFEETTPOATFEOBVMEENEOELHHIERRATENYRNOSRVSHEENYAEATACONBOUTMOERHGRDYIHFAWEEWGHHWWYAWEIADIRUTWCEFILWILLEBNAEASYENONIECIDARAPDEATH"
  // untranposed z340 plaintext
  z340pu: string = "IHOPEYOUAREHAVINGLOTSOFFANINTRYINGTOCATCHMETHATWASNTMEONTHETVSHOWWHICHBRINGOUPAPOINTABOUTMEIAMNOTAFRAIDOFTHEGASCHAMBERBECAASEITWILLSENDMETOPARADLCEALLTHESOOHERBECAUSEENOWHAVEENOUGHSLAVESTOWORVFORMEWHEREEVERYONEELSEHASNOTHINGWHENTHEYREACHPARADICESOTHEYAREAFRAIDOFDEATHIAMNOTAFRAIDBECAUSEIVNOWTHATMYNEWLIFEISLIFEWILLBEANEASYONEINPARADICEDEATH"

  ciphers = [
    this.z340, this.z340u, this.z340p, this.z340pu
  ]
  // translate position from z340 to transposed (solved/readable) position
  untranspose = [
    0, 19, 38, 57, 76, 95, 114, 133, 152, 1, 20, 39, 58, 77, 96, 115, 134, 136, 2, 21, 40, 59, 78, 97, 116, 135, 137, 3, 22, 41, 60, 79, 98, 117, 119, 138, 4, 23, 42, 61, 80, 99, 118, 120, 139, 5, 24, 43, 62, 81, 100, 102, 121, 140, 6, 25, 44, 63, 82, 101, 103, 122, 141, 7, 26, 45, 64, 83, 85, 104, 123, 142, 8, 27, 46, 65, 84, 86, 105, 124, 143, 9, 28, 47, 66, 68, 87, 106, 125, 144, 10, 29, 48, 67, 69, 88, 107, 126, 145, 11, 30, 49, 51, 70, 89, 108, 127, 146, 12, 31, 50, 52, 71, 90, 109, 128, 147, 13, 32, 34, 53, 72, 91, 110, 129, 148, 14, 33, 35, 54, 73, 92, 111, 130, 149, 15, 17, 36, 55, 74, 93, 112, 131, 150, 16, 18, 37, 56, 75, 94, 113, 132, 151, 153, 172, 191, 210, 229, 247, 267, 286, 305, 154, 173, 192, 211, 230, 248, 268, 287, 289, 155, 174, 193, 212, 231, 249, 269, 288, 290, 156, 175, 194, 213, 232, 250, 270, 272, 291, 157, 176, 195, 214, 233, 251, 271, 273, 292, 158, 177, 196, 215, 234, 252, 255, 274, 293, 159, 178, 197, 216, 235, 253, 256, 275, 294, 160, 179, 198, 217, 236, 238, 257, 276, 295, 161, 180, 199, 218, 237, 239, 258, 277, 296, 162, 181, 200, 219, 221, 240, 259, 278, 297, 163, 182, 201, 220, 222, 254, 260, 279, 298, 183, 202, 204, 223, 241, 261, 280, 299, 184, 203, 205, 224, 242, 262, 281, 300, 185, 187, 206, 225, 243, 263, 282, 301, 186, 188, 207, 226, 244, 264, 283, 302, 170, 189, 208, 227, 245, 265, 284, 303, 171, 190, 209, 228, 246, 266, 285, 304, 164, 165, 166, 167, 168, 169, 309, 308, 307, 306, 310, 311, 312, 313, 315, 314, 317, 316, 318, 319, 320, 321, 324, 323, 322, 326, 325, 334, 333, 332, 331, 330, 329, 328, 327, 335, 336, 337, 338, 339
  ]
  // translate transposed (solved/readable) position back to original position in z340
  transpose = [
    0, 9, 18, 27, 36, 45, 54, 63, 72, 81, 90, 99, 108, 117, 126, 135, 144, 136, 145, 1, 10, 19, 28, 37, 46, 55, 64, 73, 82, 91, 100, 109, 118, 127, 119, 128, 137, 146, 2, 11, 20, 29, 38, 47, 56, 65, 74, 83, 92, 101, 110, 102, 111, 120, 129, 138, 147, 3, 12, 21, 30, 39, 48, 57, 66, 75, 84, 93, 85, 94, 103, 112, 121, 130, 139, 148, 4, 13, 22, 31, 40, 49, 58, 67, 76, 68, 77, 86, 95, 104, 113, 122, 131, 140, 149, 5, 14, 23, 32, 41, 50, 59, 51, 60, 69, 78, 87, 96, 105, 114, 123, 132, 141, 150, 6, 15, 24, 33, 42, 34, 43, 52, 61, 70, 79, 88, 97, 106, 115, 124, 133, 142, 151, 7, 16, 25, 17, 26, 35, 44, 53, 62, 71, 80, 89, 98, 107, 116, 125, 134, 143, 152, 8, 153, 162, 171, 180, 189, 198, 207, 216, 225, 234, 243, 300, 301, 302, 303, 304, 305, 284, 292, 154, 163, 172, 181, 190, 199, 208, 217, 226, 235, 244, 252, 260, 268, 276, 269, 277, 285, 293, 155, 164, 173, 182, 191, 200, 209, 218, 227, 236, 245, 253, 261, 254, 262, 270, 278, 286, 294, 156, 165, 174, 183, 192, 201, 210, 219, 228, 237, 246, 238, 247, 255, 263, 271, 279, 287, 295, 157, 166, 175, 184, 193, 202, 211, 220, 229, 221, 230, 239, 256, 264, 272, 280, 288, 296, 158, 167, 176, 185, 194, 203, 212, 248, 204, 213, 222, 231, 240, 249, 257, 265, 273, 281, 289, 297, 159, 168, 177, 186, 195, 187, 196, 205, 214, 223, 232, 241, 250, 258, 266, 274, 282, 290, 298, 160, 169, 178, 170, 179, 188, 197, 206, 215, 224, 233, 242, 251, 259, 267, 275, 283, 291, 299, 161, 309, 308, 307, 306, 310, 311, 312, 313, 315, 314, 317, 316, 318, 319, 320, 321, 324, 323, 322, 326, 325, 334, 333, 332, 331, 330, 329, 328, 327, 335, 336, 337, 338, 339
  ]

  highlights
  constructor() { }

  ngOnInit(): void {
    this.clear();
  }

  clear(): void {
    this.highlights = []
    for (let a=0; a<4; a++) {
      this.highlights[a] = []
      for (let b=0; b<340; b++) {
        this.highlights[a][b] = false;
      }
    }
    console.log(this.highlights)

  }

  split(string): string {
    return string.split('')
  }
  cl(which: number, pos: number): string {
    let cl = which < 2 ? "cipher" : "pp"
    return this.highlights[which][pos] ? cl + " h" : cl
  }  
  h(which: number, pos: number): void {
    this.highlights[which][pos] = !this.highlights[which][pos];
    console.log(which + "," + pos);
    for (let a=0; a<3; a++) {
      which = (which + 1) % 4;
      pos = which % 2 == 0 ? this.untranspose[pos] : this.transpose[pos];
      this.highlights[which][pos] = !this.highlights[which][pos]
      console.log(which + "," + pos);
    }
  }  
}

