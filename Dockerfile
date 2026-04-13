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
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR da stage anterior
COPY --from=builder /app/target/PersonalFood-0.0.1-SNAPSHOT.jar app.jar

# Expor porta
EXPOSE 8080

# Executar aplicação com perfil AWS
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=aws"]
