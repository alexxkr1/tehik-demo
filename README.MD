Tehik Infosystem - README

Overview:
This project is a simple infosystem that allows users to input a number, processes it asynchronously, and displays the results. It consists of a frontend (Angular) and a backend (Spring Boot) with RabbitMQ integration. The backend uses an H2 in-memory database for data storage.

Project Structure:

frontend: tehik-front

backend: tehik-api

Tech Stack:

Frontend: Angular

Backend: Spring Boot

Java 25

H2 in-memory database

RabbitMQ

Running the Application:

Make sure Docker and Docker Compose are installed on your machine.

Copy the sample environment file to create your own configuration:
cp .env.sample .env

Start the system using Docker Compose:
docker compose up -d

Frontend:

Accessible at: http://localhost:4200

Backend:

Swagger UI: http://localhost:8080/swagger-ui.html

H2 Console: http://localhost:8080/h2-console

Notes:

The H2 database is in-memory and does not persist between application restarts.

RabbitMQ is used for asynchronous task processing.

No authentication is implemented; all tasks are visible based on the user's IP address.

Frontend will display error until backend is completely running and accessible.

Communication is happening in real-time using WebSockets and REST api.