# ===== Stage 1: Build (usa wrapper do monorepo) =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /repo
COPY . .
# torna o wrapper executável, se existir
RUN chmod +x ./mvnw || true
# Tenta buildar pelo parent agregador; se não houver, usa o pom do módulo
RUN ./mvnw -B -DskipTests -pl orchestration-service -am clean package \
 || ./mvnw -B -DskipTests -f orchestration-service/pom.xml clean package

# ===== Stage 2: Runtime enxuto =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /repo/orchestration-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]