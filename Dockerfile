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
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Copiar código fuente
COPY src src

# Compilar sin tests
RUN ./mvnw package -DskipTests -B --no-transfer-progress

# Extraer JAR en capas: separa dependencias (cambian poco) del código (cambia seguido)
# Resultado: rebuilds más rápidos porque Docker cachea las capas de dependencias
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination extracted

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Crear usuario no-root por seguridad
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copiar capas en orden de frecuencia de cambio (menor → mayor)
# Si solo cambia el código, Docker reutiliza las capas de dependencias del cache
COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./

# Crear directorio de logs
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Optimización de memoria para Render Free (512 MB)
# - SerialGC: menor overhead de memoria que ZGC/G1 en contenedores pequeños
# - MaxRAMPercentage=65%: deja margen para el SO y overhead nativo
# - MaxMetaspaceSize: limita el metaspace de clases cargadas
# - Xss512k: reduce stack por hilo de plataforma (virtual threads usan stack dinámico)
ENTRYPOINT ["java", \
  "-XX:+UseSerialGC", \
  "-XX:MaxRAMPercentage=65.0", \
  "-XX:MaxMetaspaceSize=120m", \
  "-Xss512k", \
  "org.springframework.boot.loader.launch.JarLauncher"]
