# Getting Started

### Tools
For further reference, please consider the following sections:

* Install Java 8
* Install postgres (import the scema.sql file)
* Install Infinispan

### Starting the Application

* Clone the project
* Access the application.properties file (Replace valies with your variables)
* On the project root run command "gradle boot-run" Application should start

### Obtaining Token
* Make a request to "POST address:port/oauth/token"
* Add basic auth username: easy, password: $2a$04$QHEtEbyL7RQ3KWw25RZm2uBMAe28pS5o0atB055ncM2UNCrBPs3yS
* Add formData username: azr1 (or any username fromthe db), password: *******, grant_type: password, client_id: easy

* Sample request 
POST /oauth/token HTTP/1.1
Host: localhost:8096
Authorization: Basic ZWFzeTokMmEkMDQkUUhFdEVieUw3UlEzS1d3MjVSWm0ydUJNQWUyOHBTNW8wYXRCMDU1bmNNMlVOQ3JCUHMzeVM=
FormData: username = azr1 | password = 20S0KPNOIM | grant_type = password | client_id: easy 

* you'll get an access token to use for subsequent requests