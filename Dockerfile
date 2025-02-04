FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/capital-gain-1.0.jar capital-gain.jar

CMD ["java", "-jar", "capital-gain.jar"]