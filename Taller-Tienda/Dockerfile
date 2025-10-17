# -------- Stage 1: build (JDK 21 + Maven) --------
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos solo lo necesario y conservamos caché de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Ahora el código fuente
COPY src ./src

# Compilamos el JAR (sin tests en esta imagen; en CI puedes correrlos aparte)
RUN mvn -B -ntp clean package -Dmaven.test.skip=true

# -------- Stage 2: runtime (JRE 21) --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el artefacto empaquetado
COPY --from=builder /app/target/*.jar app.jar

# Puerto HTTP de tu Spring Boot
EXPOSE 8080

# Sugerido: healthcheck simple (opcional si usarás uno en compose)
# HEALTHCHECK --interval=10s --timeout=3s --retries=10 \
#  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Usa ENTRYPOINT en forma exec (no shell) para señales correctas
#ENTRYPOINT ["java","-jar","app.jar"]
CMD ["java", "-jar", "app.jar"]
