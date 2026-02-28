# ============================================================
# Stage 1: Build
# ============================================================
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copiar Maven Wrapper y POM primero para cachear dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Descargar dependencias (capa cacheada si pom.xml no cambia)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Crear usuario no-root por seguridad
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copiar el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorio de logs con permisos correctos
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Health check usando Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
