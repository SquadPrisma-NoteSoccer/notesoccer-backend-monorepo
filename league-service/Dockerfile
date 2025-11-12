# ==== Stage 1: build ====
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /repo

# Copia o repo inteiro porque o módulo usa POM pai e pode depender de shared-libs
# (use .dockerignore para não mandar target/, .git/, etc)
COPY . .

# Builda apenas o módulo orchestration-service e seus dependentes (-am), sem testes
RUN mvn -q -e -DskipTests clean package spring-boot:repackage

# ==== Stage 2: runtime ====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Instalar curl para o healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /repo/target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT:-8082} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} -jar /app/app.jar"]
