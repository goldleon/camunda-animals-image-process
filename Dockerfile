# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-alpine
LABEL authors="Anass BOUCHTAOUI"

RUN apk update && apk upgrade

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file into the container
COPY target/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]