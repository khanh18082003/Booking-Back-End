FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} booking-back-end-service.jar


ENTRYPOINT ["java", "-jar", "booking-back-end-service.jar", "--spring.profiles.active=prod"]

EXPOSE 8081