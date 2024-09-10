def vigenere_decrypt(ciphertext, keyword):
    plaintext = ''
    keyword_index = 0
    for c in ciphertext:
        if c.isalpha():
            keyword_char = keyword[keyword_index % len(keyword)]
            key = ord(keyword_char.upper()) - ord('A')
            if c.isupper():
                plaintext += chr((ord(c) - ord('A') - key) % 26 + ord('A'))
            else:
                plaintext += chr((ord(c) - ord('a') - key) % 26 + ord('a'))
            keyword_index += 1
        else:
            plaintext += c
    return plaintext

# Known Clues
FLRV = 'EAST'
QQPRNGKSS = 'NORTHEAST'
NYPVTTMZFPK = 'BERLINCLOCK'

# Ciphertext
ciphertext = 'OBKR UOXOGHULBSOLIFBBWFLRVQQPRNGKSSOTWTQSJQSSEKZZWATALUDIAWINFBNYPVTTMZFPKWGDKZXTCDCIGKUHUAEKCAR'

# Find all possible keywords
keywords = []
for i in range(26):
    for j in range(26):
        for k in range(26):
            keyword = chr(i + ord('A')) + chr(j + ord('A')) + chr(k + ord('A'))
            keyword += NYPVTTMZFPK
            try:
                plaintext = vigenere_decrypt(ciphertext, keyword)
                if FLRV in plaintext and QQPRNGKSS in plaintext and NYPVTTMZFPK in plaintext:
                    keywords.append(keyword)
            except:
                pass

# Print all decryptions
for keyword in keywords:
    plaintext = vigenere_decrypt(ciphertext, keyword)
    print('Keyword:\n' + keyword + '\n')
    print('Decrypted message using keyword from known clues:\n' + plaintext + '\n')