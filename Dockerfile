# Multi-stage build para otimizar a imagem Docker
FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /app

# Copiar pom.xml e arquivos de build
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Copiar código-fonte
COPY src src

# Build do aplicativo
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar JAR da stage anterior
COPY --from=builder /app/target/PersonalFood-0.0.1-SNAPSHOT.jar app.jar

# Expor porta
EXPOSE 8080

# Variáveis de ambiente para PostgreSQL (padrão)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/personal_food \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=password \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SPRING_PROFILES_ACTIVE=prod

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]