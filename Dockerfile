# Etapa de Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Executa o Maven para construir o projeto ignorando os testes
RUN mvn clean package -DskipTests

# Etapa de Execução
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
# Copia o ficheiro .jar gerado na etapa anterior
COPY --from=build /app/target/PersonalFood-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que o Spring Boot vai usar (configurada como 8080 no application.properties)
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
