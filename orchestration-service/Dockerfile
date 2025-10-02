# ==== Stage 1: build ====
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /repo

# Copia só o que precisamos para “esquentar” o cache de deps
COPY pom.xml pom.xml
COPY shared-libs/pom.xml shared-libs/pom.xml
COPY orchestration-service/pom.xml orchestration-service/pom.xml

# Baixa dependências do módulo alvo e seus dependentes (mais estável)
RUN mvn -B -U -e -DskipTests -pl orchestration-service -am dependency:go-offline

# Agora copia o resto do código
COPY . .

# Build com retries e sem pool de conexões HTTP
RUN mvn -B -U -e -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.pool=false \
    -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=debug \
    -pl orchestration-service -am clean package spring-boot:repackage

# ==== Stage 2: runtime ====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /repo/orchestration-service/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]