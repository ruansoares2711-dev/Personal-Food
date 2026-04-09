# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
# O parâmetro -U força o Maven a baixar dependências atualizadas
RUN mvn clean package -DskipTests -U

# Estágio 2: Execução
FROM eclipse-temurin:21-jre-jammy
# Usar o curinga * evita erro se o nome do JAR mudar um pouquinho
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]