# city-list

# Overview
This page documents the city-list backend apis.

---
# Important Information
The application does not need manual configuration/setup.

# Starting the app
Start the application as a simple java application with the following command: `java city-list-backend.jar`
or building the image: `docker build -t city-list-backend .`
and starting a docker container with this jar: `docker run -it --rm -p 8080:80 --name city-list-backend `

# DB
There are liquebase changesets under `/resources/db/changelog/` and these will be automatically run on startup.
The application is in early phase and currently using postgres db.

---

# Future plans
### API documentation
The api contract will be found here in
[swagger ui](http://localhost:8080/swagger-ui/index.html) or
[json](http://localhost:8080/v3/api-docs) or
[yaml](http://localhost:8080/v3/api-docs.yaml)