# To-Do List Application
This is a Java 17-based To-Do List application built using the Spring framework, MySQL for database management, 
and Docker for containerization. It allows users to register (with email confirmation), create, edit, and delete tasks. 
Additionally, there is an API for task management, accessible via JWT tokens, and documented using Swagger. 
The application is hosted on AWS EC2, and Continuous Integration/Continuous Deployment (CI/CD) is set up using AWS CodeBuild 
to automatically build and deploy the application to the host when changes are pushed to the master branch.

## Features
 - User Registration: Users can register for an account, and a confirmation email is sent to their provided email address for account verification.
 - Task Management: Users can create, edit, and delete tasks.
 - API Access: There is an API for task management, and access is secured using JWT tokens.
 - Swagger Documentation: The API is documented using Swagger, making it easy for developers to understand and use.
 - AWS EC2 Hosting: The application is hosted on an AWS EC2 instance for scalability and reliability.
 - CI/CD with AWS CodeBuild: Continuous Integration and Continuous Deployment are set up using AWS CodeBuild, ensuring that changes pushed to the master branch are automatically tested, built, and deployed to the host.
 - Security: Passwords are hashed and salted using BCrypt, and JWT tokens are used for API access. 

## Technologies Used
 - Java 17
 - Spring Framework (Security, Web, Data JPA)
 - MySQL
 - Docker
 - AWS EC2
 - AWS CodeBuild
 - JWT for Authentication
 - Swagger for API Documentation
 - JavaScript (for frontend)
 - JavaMailSender for sending emails

## Running the Application
To run the application, you will need to have Docker installed. 
Once you have Docker installed, you can run the application by running the following command in the root directory of the project:
```
docker-compose up
```
Before you need set up variables in the `docker-compose.yml` file:
