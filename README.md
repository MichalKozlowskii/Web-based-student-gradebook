# Web-based student gradebook

## Description
A web-based student gradebook built with microservice architecture. Features grade and attendance management, USOS integration via OAuth 1.0a, JWT authentication, and an automated exam grading module powered by Azure Computer Vision OCR. Built with Spring Boot, React, and deployed with Docker.

## Architecture
The system consists of 7 backend components built with Spring Boot — 6 microservices and a reactive API Gateway (Spring Cloud Gateway). Most microservices have their own dedicated MySQL database. The summary-service is stateless and aggregates data from other services. Microservices communicate with each other via OpenFeign HTTP clients. Redis is used as a shared store for JWT blacklisting and rate limiting. The frontend is a React SPA served by nginx, which also handles TLS termination and request routing.

### C4 level 2 inspired diagram
![level2 (1)](https://github.com/user-attachments/assets/d75f77f9-60f7-4873-87ce-4af0d34f89aa)

### Deployment diagram
![deploy (3)](https://github.com/user-attachments/assets/32f05e5d-0124-4049-98fe-a1facada8baa)

## Build instructions

### Requirements
* Docker
* USOS UWR API Key
* AzureCV key

### How to
1. Clone the repo.
2. Navigate to `docker-compose` directory.
3. Generate RSA key pair in `docker-compose/keys` directory.
4. Generate TLS certificate in `docker-compose/nginx/certs` directory.
5. Create and fill up `.env` file (see `.env.example` for required fields).
6. Run the containers using a command `docker compose up -d`.

### RSA Keys generation
`openssl genrsa -out private.pem 2048`
<br>
`openssl rsa -in private.pem -pubout -out public.pem`

### Dummy TLS certificate generation
`openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout privkey.pem -out fullchain.pem -subj "/C=US/ST=Dev/L=Local/O=Dev/CN=localhost"`
