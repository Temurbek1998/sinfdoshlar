# 1-bosqich: loyihani yig'ish
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# 2-bosqich: faqat tayyor jar bilan yengil obraz
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/sinfdoshlar-*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
