### The final task in the courses at the Clevertec company.

#### RESTful web service that implements functionality for working with news.

###### Author: Nikita Semeniuk.

#### Description:

##### The idea of the application is the ability to manage the news service through the REST interface. The application implements the possibility of getting a list of news, getting one news with comments to it, creating, updating, deleting news, as well as similar operations with comments and registered users.

##### The application is built on a microservice architecture and contains five microservices.
![alt microservice interaction scheme](src/main/resources/static/images/app_schema.png)
##### There are two non-public microservices (user-data-service and news-data-service), each of which works with a separate database.

##### The public microservices are the user-authentication-service, the user-service, and the news-service.

##### The main purpose of the user-service is user registration in the system.

##### The user-authentication-service is designed to receive a JWT token for users registered in the system.

##### News-service allows you to perform operations on news and comments.

##### Unregistered users can view news and comments without restrictions. Other operations are not available to them. A registered user, within the limits of his powers, can carry out various operations related to changes in news, comments and users themselves. The description of the powers of the various users are defined in the task.
