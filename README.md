# Spring Blog REST API

A REST API for a blog application using SpringBoot.

[![Maven Build](https://github.com/sivaprasadreddy/spring-blog-api/actions/workflows/maven.yml/badge.svg)](https://github.com/sivaprasadreddy/spring-blog-api/actions/workflows/maven.yml)

> [!NOTE]  
> You can find the frontend application(s) using this REST API in the following repositories:
> - [blog-angular](https://github.com/sivaprasadreddy/blog-angular)

## Documentation
* [Project Overview](docs/project.md)
* [Requirements](docs/requirements.md)
* [REST API Docs](docs/rest-apis.md)
* [Spring Boot Best Practices](docs/spring-boot-best-practices.md)

## Tech Stack
* Language: Java 25
* Framework: Spring Boot 4.x
* Web/API: Spring Web MVC
* Security: Spring Security + OAuth2 Resource Server + JWT (RSA keys)
* Validation: Jakarta Bean Validation
* Persistence: Spring Data JPA + Hibernate
* Database: PostgreSQL
* Migrations: Flyway
* Mapping: MapStruct
* Modular architecture/events: Spring Modulith
* Email: Spring Mail (JavaMail) + Console email adapter
* API docs: springdoc OpenAPI / Swagger UI
* Build: Maven
* Testing: JUnit 5, Spring Boot test starters, Testcontainers

## Prerequisites
* JDK 25
* Docker and Docker Compose
* Your favourite IDE (Recommended: [IntelliJ IDEA](https://www.jetbrains.com/idea/))

Install JDK using [SDKMAN](https://sdkman.io/)

```shell
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk install java 25-tem
$ sdk install maven
```

## How to?

```shell
# Run tests
$ ./mvnw test

# Run application using Maven
$ ./mvnw spring-boot:run

# Run application with the "local" profile (enables Swagger UI)
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

All REST endpoints are exposed under the `/api` base path.

* REST API: http://localhost:8080/api (e.g. http://localhost:8080/api/posts)
* Swagger UI: http://localhost:8080/swagger-ui/index.html

> **Note**: Swagger UI is disabled by default (`springdoc.swagger-ui.enabled=false`).
> It is only available when running with the `local` profile,
> which enables it via `application-local.properties`.

## Generating certs

```shell
# create rsa key pair
openssl genrsa -out keypair.pem 2048

# extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# create private key in PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

## Using [Taskfile](https://taskfile.dev/) utility
Task is a task runner that we can use to run any arbitrary commands in an easier way.

```shell
# Run tests
$ task test

# Build docker image
$ task build_image

# Run application in docker container
$ task start
$ task stop
$ task restart
```
