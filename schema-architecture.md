# Smart Clinic Management System Architecture

## Section 1: Architecture Summary

This Spring Boot application follows a three-tier architecture consisting of the presentation layer, application layer, and database layer. The application uses both MVC and REST controllers to handle different types of user interactions. Thymeleaf templates are used for the Admin Dashboard and Doctor Dashboard, while REST APIs are used for appointment management, patient dashboards, and patient records.

The backend application uses a common service layer to process business logic and communicate with the repository layer. The application connects with two databases: MySQL and MongoDB. MySQL is used to store structured relational data such as doctors, patients, appointments, and admin details using JPA entities. MongoDB is used to store flexible document-based prescription records using document models. This architecture improves scalability, maintainability, and separation of concerns.

## Section 2: Numbered Flow of Data and Control

1. Users access modules such as AdminDashboard, DoctorDashboard, Appointment, PatientDashboard, or PatientRecord.
2. User requests are routed to either Thymeleaf Controllers or REST Controllers based on the request type.
3. The controllers validate the requests and forward them to the Service Layer.
4. The Service Layer applies business logic and coordinates workflows between components.
5. The Service Layer communicates with MySQL repositories and MongoDB repositories for data operations.
6. Data from MySQL is mapped into JPA entities, while MongoDB data is mapped into document models.
7. The processed data is returned to the user as HTML pages through Thymeleaf or as JSON responses through REST APIs.
