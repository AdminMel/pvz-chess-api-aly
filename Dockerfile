# ====== FASE 1: build del JAR ======
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos descriptor y descargamos dependencias primero (capa caché)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Ahora copiamos el código fuente
COPY src ./src

# Build del JAR (sin tests para más rápido)
RUN mvn -q -DskipTests clean package


# ====== FASE 2: imagen ligera para producción ======
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el JAR generado desde la fase de build
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

# Puerto interno donde escucha Spring Boot
EXPOSE 8080

# Variables de entorno (sin secretos, solo nombres)
# Render te inyectará los valores reales
ENV DB_URL=""
ENV DB_USER=""
ENV DB_PASS=""
ENV FIREBASE_CRED_PATH="/etc/secrets/firebase-admin-key.json"

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
