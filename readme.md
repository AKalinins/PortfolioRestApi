Tools used:<br/>
Spring Boot<br/>
Spring Boot Security<br/>
Spring Doc<br/>
H2 Database<br/>
Lombok<br/>
JUnit<br/>
Mockito<br/><br/>

App uses the H2 in-memory database to store rest endpoint user credentials.<br/>
Database is erased as soon as the application is stopped and recreated when application is started.<br/>
Database console can be accessed using URL http://localhost:8080/h2-console when the application is running.<br/>
All necessary information for logging into the H2 console can be found in application.properties file.<br/><br/>

SpringDoc is used to generate API docs.<br/>
Documentation can be accessed using URL http://localhost:8080/swagger-ui.html when the application is running.<br/><br/>

How to run project:<br/>
1) Add Tryme environment credentials to the application.properties file (graphql.service.username and graphql.service.password).<br/>
2) Start the Spirng Boot app.<br/><br/>

Lombok is used to for code generation. Maven plugin is used to allow annotation processing,<br/>
but if it fails for Intellij Idea, you should do the following:<br/>
1) Install Lombok plugin<br/>
2) Enable annotation processing:<br/>
Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors -> Enable annotation processing <br/><br/>

Rest endpoint credentials:<br/>
Username: user<br/>
Password: password123<br/><br/>

In order to make a request from Postman:<br/>
1) Create a GET request<br/>
2) Use http://localhost:8080/api/v1/transactions path<br/>
3) In Authorization tab choose Basic Auth and use Rest endpoint credentials<br/>
for Username and password<br/>
4) In Params tab pass the parameters required, for example:<br/>
![img.png](readmepics/img.png) <br/><br/>

Alternatively you can make request using SpringDoc http://localhost:8080/swagger-ui.html and choosing "Try It Out" option<br/><br/>
![img.png](readmepics/img2.png)