# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim
LABEL authors="Anass BOUCHTAOUI"

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file into the container
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

# Make port 8080 available to the world outside this container
EXPOSE $PORT

# Run the jar file
# CMD ["./mvnw", "spring-boot:run"]
ENTRYPOINT ["./mvnw", "spring-boot:run"]