# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
ENV TZ=UTC
WORKDIR /app
COPY --from=build /app/target/friendly-wager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Bind to Render/Heroku-style PORT env if provided; default to 8080
ENTRYPOINT ["sh","-c","exec java -jar /app/app.jar --spring.profiles.active=prod --server.port=${PORT:-8080}"]

