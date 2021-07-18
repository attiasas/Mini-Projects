# AES3
Implementation of Encryption algorithms and Hacking

#### Programs
* AES Encryption algorithm (one key)
* AES3 Encryption algorithm (3 times AES = 3 keys)
* Hack AES3 as black box (cyper <-> msg) - Find the 3 Keys

Hack programs JAR has CLI to use the hacking program in cmd
 ```bash  
o –b : instruction to break the encryption algorithm
o –m <path>: denotes the path to the plain-text message
o –c <path>: denotes the path to the cipher-text message
o –o <path>: a path to the output file with the key(s) found.
 
Usage: Java –jar aes.jar -b –m <path-to-message> –c <path-to-cipher> -o < output-path>
```  

Assignment for the course 'Computer & Informaion Security'