# SFTP Loader

This project implements a Spring Boot service for synchronizing files from an SFTP server to a local directory using PostgreSQL for metadata storage.

## Building

```
mvn package
```

## Running
Set the required environment variables for the SFTP connection and database credentials, then run:

```
java -jar target/sftp-loader-0.0.1-SNAPSHOT.jar
```

## Docker

Build the container image and start PostgreSQL alongside the service using Docker Compose:

```
docker-compose build
docker-compose up
```
