FROM openjdk:24-jdk-slim
WORKDIR /app
COPY target/sftp-loader-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
