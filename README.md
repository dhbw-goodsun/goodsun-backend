# Goodsun Frontend

## Description
Frontend for the goodsun application. Uses the backend to calculate precise solar output. 


## License
This application is licensed under `CC BY-NC 4.0`. No commercial use. This source must be named when using or developing further.


## Installation and useage
Donload this project and install it using `npm install`. Make sure that angular is installed on your system. 
If necessary change the angular serve-ip adress. For local testing it is recommened to use self signed SSL-Certiciates (see below).

Configuration for the backend can be changed under `/src/environments/environment.ts` and `..environment.prod.ts`.

Serve locally with `ng serve`.

### SSL encryption for local testing
This Application need SSL-encryption (https). 

#### Generating self signed SSL-Certificates (openssl)
In the file `san.cnf`, adjust `commonName` and `IP.1` with the IP of the host. 

Then execute (while in the same directionary) `openssl req -x509 -nodes -days 730 -newkey rsa:2048 -keyout key.pem -out cert.pem -config san.cnf`

To verify use `openssl x509 -in cert.pem -text -noout`. Execute `openssl x509 -outform der -in cert.pem -out cert.crt` to create Windows-useable .crt file.

#### Useage within Angular
Add following to angular.json:

"serve": {
    ...
    "options": {
        "sslKey": "key.pem",
        "sslCert": "cert.pem",
        "ssl": true
    }
},

See additional config: https://stackoverflow.com/questions/39210467/get-angular-cli-to-ng-serve-over-https


### Further Reference
- https://medium.com/@antelle/how-to-generate-a-self-signed-ssl-certificate-for-an-ip-address-f0dd8dddf754
- https://stackoverflow.com/questions/56514116/how-do-i-get-deviceorientationevent-and-devicemotionevent-to-work-on-safari
