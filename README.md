# Vehicle Registration
Vehicle registration coding task

## Technical stack
* Spring Boot - version 2.5.2
* Java 11
* Liquibase - version 4.3.5 (To create database DDL scripts)
* Lombok - version 1.18.20
* Swagger2 - version 3.0.0
* H2 database

## Setup instructions
- Checkout the source code from this GIT repository.
- Build the project with <code>mvn install</code>
- Change directory to <PROJECT_ROOT>\target
- Run the Spring boot application by running the below command
<code><JAVA_HOME>\bin\java.exe -jar registration-0.0.1-SNAPSHOT.jar 
com.vehicle.registration.VehicleRegistrationApplication</code>

## Swagger API documentation 
The Swagger API specification JSON is in the below file.<br>
[swagger/vehicle-registration-0.0.1.json](swagger/vehicle-registration-0.0.1.json) <br>
Access the swagger api documentation from the localhost url while the application is running.<br>
http://localhost:8080/swagger-ui.html#/vehicle-registration-controller

## API Payload cURL
* Create Person <br>
<code>
curl --location --request POST 'http://localhost:8080/vehicle-registration/create-person' \
--header 'Content-Type: application/json' \
--data-raw '{
   "firstName": "John",
   "lastName": "Bob"
}'
</code>

* Create Vehicle <br>
<code>
curl --location --request POST 'http://localhost:8080/vehicle-registration/create-vehicle' \
--header 'Content-Type: application/json' \
--data-raw '{
   "registrationNumber": "ASD-89P"
}'
</code>

* Link Person to Vehicle <br>
<code>
curl --location --request PATCH 'http://localhost:8080/vehicle-registration/link-person-to-vehicle' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "John",
   "lastName": "Bob",
   "registrationNumber": "ASD-89P"
}'
</code>

* Unlink Person from Vehicle <br>
<code>
curl --location --request DELETE 'http://localhost:8080/vehicle-registration/unlink-person-from-vehicle' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "John",
   "lastName": "Bob",
   "registrationNumber": "ASD-89P"
}'
</code>

* Get Person Vehicle Registrations <br>
<code>
curl --location --request GET 'http://localhost:8080/vehicle-registration/get-person-vehicle-registrations?firstName=John&lastName=Bob'
</code>

