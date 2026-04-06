FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && ./mvnw -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
