README – Backend Application (Tehik API)

This is the backend service for the Tehik demo application. It is a Spring Boot project that exposes a REST API, processes background tasks through RabbitMQ, and stores data in an in-memory H2 database.

APPLICATION PROPERTIES

spring.application.name=tehik
spring.web.cors.allowed-origins=http://localhost:4200

spring.datasource.url=jdbc:h2:mem:tehikdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2console/
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
app.rabbitmq.exchange=task-exchange
app.rabbitmq.queue=task-queue
app.rabbitmq.routing-key=task-routing-key
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never

RUNNING THE APPLICATION LOCALLY

Prerequisites:

Java 25 installed and configured.

RabbitMQ service running locally on port 5672 (guest/guest credentials).

IntelliJ IDEA or another IDE with Gradle support.

Import the project:

Run from IntelliJ:

Open the class “com.example.tehik.TehikApplication”.

Click the green Run button or right-click → “Run TehikApplication”.

The application will start on http://localhost:8080
.

The H2 in-memory database console will be available at http://localhost:8080/h2console
API Endpoints documentation http://localhost:8080/swagger-ui.html
.

JDBC URL for console: jdbc:h2:mem:tehikdb
User: sa
Password: (leave empty)

RUNNING VIA GRADLE (CLI)

On Windows:
gradlew.bat bootRun

On macOS/Linux:
./gradlew bootRun

This will start the Spring Boot application with the same default configuration.

RUNNING TESTS

On Windows:
gradlew.bat test

On macOS/Linux:
./gradlew test


NOTES

The default configuration uses an in-memory H2 database that resets each time the application restarts.

In production or persistent testing, configure an external database (e.g. PostgreSQL) and set spring.datasource.url, username, and password accordingly.

The application connects to RabbitMQ using the values defined above. Ensure that the exchange, queue, and routing key names match your RabbitMQ configuration.

The health endpoint is available at http://localhost:8080/actuator/health
.

To run inside Docker, all properties are overridden by environment variables defined in docker-compose.yml.