# Spring Blog REST API

A REST API for a blog application using SpringBoot.

[![Maven Build](https://github.com/sivaprasadreddy/spring-blog-api/actions/workflows/maven.yml/badge.svg)](https://github.com/sivaprasadreddy/spring-blog-api/actions/workflows/maven.yml)

You can find the frontend application(s) using this REST API in the following repositories:
- [blog-angular](https://github.com/sivaprasadreddy/blog-angular)

## Tech Stack
* Java
* Spring Boot
* Spring Modulith
* Spring Data JPA
* Spring Security

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
