# Spring Blog REST API

A REST API for a blog application using SpringBoot.

## Tech Stack:
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

Verify the prerequisites

```shell
$ java -version
$ docker info
$ docker compose version
```

## How to?

```shell
# Clone the repository
$ git clone https://github.com/sivaprasadreddy/spring-blog-api.git
$ cd spring-blog-api

# Run tests
$ ./mvnw test

# Automatically format code using spotless-maven-plugin
$ ./mvnw spotless:apply

# Run/Debug application from IDE
Run `src/main/java/com/sivalabs/blog/BlogApplication.java` from IDE.

# Run application using Maven
./mvnw spring-boot:run
```

* Application: http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui/index.html

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
Task is a task runner that we can use to run any arbitrary commands in easier way.

### Installation

```shell
$ brew install go-task
(or)
$ go install github.com/go-task/task/v3/cmd/task@latest

#verify task version
$ task --version
Task version: 3.35.1
```

### Using `task` to perform various tasks:

```shell
# Run tests
$ task test

# Automatically format code using spotless-maven-plugin
$ task format

# Build docker image
$ task build_image

# Run application in docker container
$ task start
$ task stop
$ task restart
```
