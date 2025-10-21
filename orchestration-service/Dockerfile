# ==== Stage 1: build ====
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /repo

# 1) Copia POMs para esquentar cache de deps (todas as entradas do <modules>)
COPY pom.xml pom.xml
COPY auth-service/pom.xml            auth-service/pom.xml
COPY flyer-service/pom.xml           flyer-service/pom.xml
COPY gateway/pom.xml                 gateway/pom.xml
COPY match-service/pom.xml           match-service/pom.xml
COPY notification-service/pom.xml    notification-service/pom.xml
COPY shared-libs/pom.xml             shared-libs/pom.xml
COPY league-service/pom.xml          league-service/pom.xml
COPY user-service/pom.xml            user-service/pom.xml
COPY orchestration-service/pom.xml   orchestration-service/pom.xml

# 2) Baixa as dependências do módulo alvo e dos dependentes do reactor
RUN mvn -B -U -e -DskipTests -pl orchestration-service -am dependency:go-offline

# 3) Agora copia o resto do código
COPY . .

# 4) Builda somente o orchestration-service (+ dependentes) e empacota o jar
RUN mvn -B -U -e -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.pool=false \
    -pl orchestration-service -am clean package spring-boot:repackage

# ==== Stage 2: runtime ====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /repo/orchestration-service/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]
