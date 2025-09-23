# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /repo

# Copia apenas o necessário primeiro (melhor cache)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY orchestration-service/pom.xml orchestration-service/pom.xml
# (se houver dependências compartilhadas)
COPY shared-libs/pom.xml shared-libs/pom.xml || true

# Depois os fontes
COPY orchestration-service/ orchestration-service/
COPY shared-libs/ shared-libs/ || true

RUN chmod +x mvnw
# -pl orchestration-service -am garante build das dependências do módulo
RUN ./mvnw -B -DskipTests --no-transfer-progress \
    -pl orchestration-service -am clean package

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# copia o jar gerado (wildcard ok)
ARG JAR_PATH=/repo/orchestration-service/target/*-SNAPSHOT.jar
COPY --from=build ${JAR_PATH} app.jar

# Ajuste de memória em container pequeno (Render Free/Starter)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

# Render injeta $PORT; deixamos o padrão local em 8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# Usa $PORT do Render quando existir
CMD ["sh","-c","java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]