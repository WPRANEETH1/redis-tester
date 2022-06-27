FROM openjdk:11.0.12-slim
WORKDIR /application
COPY redis-tester-1.0.0.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","application.jar"]
