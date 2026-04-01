# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia o pom.xml e baixa as dependências antes (aproveita cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o restante do código e compila
COPY src ./src
RUN mvn package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copia apenas o JAR gerado
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
