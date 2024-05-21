<div align="center">
  <h3 align="center">Notification Service</h3>
  <p align="center">
    This is a Spring Boot Microservice
    <br />
    <a href="./docs"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="ci_job_url">CI Job</a>
    .
    <a href="http://localhost:11136/api-docs/swagger-ui/">OpenAPI UI</a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#purpose-of-microservice">Purpose of Microservice</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#build">Build</a></li>
        <li><a href="#database-setup">Database Setup</a></li>
        <li><a href="#running-the-service">Running the Service</a></li>
      </ul>
    </li>
    <li><a href="#team">Team</a></li>
  </ol>
</details>

<!-- GETTING STARTED -->

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

For exact versions refer to repository root `docs` directory.

* Java
* Docker
* IntelliJ Idea
* Postgres

### Build

To build the project execute following command.

```
$ ./gradlew clean build
```

> `$` signifies command prompt. You don't have to type it.

## Running the service

You can run Postgres in a Docker container by executing following command

```
  docker run --name postgresdb -e POSTGRES_PASSWORD=<password in application.properties> -e POSTGRES_DB=notificationservicedb -p 5432:5432 -d postgres:12.6
```

You can connect to Postgres using psql CLI.

```
  docker run -it --rm --link postgresdb postgres:12.6 psql -h postgresdb -U postgres
```

Next, you can run the service from either IDE or CLI

```
java -jar build/libs/notification-service-0.1.0.jar
```

## Team

Below are the team members who maintain this service

* Shubham Jangra