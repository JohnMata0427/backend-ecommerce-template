# ============================================================
# Stage 1: Build
# ============================================================
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copiar wrapper y pom.xml primero para cachear dependencias
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml

# Dar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Descargar dependencias (capa cacheada si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar sin tests
RUN ./mvnw package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Crear usuario no-root por seguridad
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copiar el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Crear directorio de logs
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

# Optimización de memoria para Render Free (512 MB)
# - SerialGC: menor overhead de memoria que ZGC/G1 en contenedores pequeños
# - MaxRAMPercentage=65%: deja margen para el SO y overhead nativo
# - MaxMetaspaceSize: limita el metaespace de clases cargadas
# - TieredStopAtLevel=1: compilación JIT mínima, arranque más rápido
# - Xss512k: reduce el tamaño de stack por hilo (default ~1MB)
ENTRYPOINT ["java", \
  "-XX:+UseSerialGC", \
  "-XX:MaxRAMPercentage=65.0", \
  "-XX:MaxMetaspaceSize=120m", \
  "-Xss512k", \
  "-XX:+TieredCompilation", \
  "-XX:TieredStopAtLevel=1", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
