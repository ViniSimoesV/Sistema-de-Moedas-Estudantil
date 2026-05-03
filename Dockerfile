# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean install -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre
COPY --from=build /target/UniRewards-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "sleep 10 && java -jar app.jar"]