# Сборка приложения
FROM maven:3.8-openjdk-17 AS build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/app/target/*.jar app.jar

VOLUME /tmp

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]