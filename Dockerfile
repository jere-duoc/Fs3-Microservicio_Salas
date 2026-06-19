# ======================
# Build Stage
# ======================
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copiar configuración Maven primero
COPY pom.xml .

# Descargar dependencias (para aprovechar la caché de Docker)
RUN mvn dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn -B clean package -DskipTests

# ======================
# Runtime Stage
# ======================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Instalar utilidades para el healthcheck y wait-for-it (`curl` y `netcat`)
RUN apt-get update && apt-get install -y curl netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copiar script para esperar por la base de datos
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Crear usuario sin privilegios por seguridad
RUN useradd -m springuser

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /workspace/target/*.jar ./app.jar

# Asignar permisos al usuario springuser
RUN chown -R springuser:springuser /app

# Cambiar al usuario sin privilegios
USER springuser

# Puerto del microservicio de salas (según tu docker-compose es el 8081)
EXPOSE 8083

# Ejecutar aplicación: esperar a MySQL en el puerto 3306 y luego iniciar la JVM optimizada
ENTRYPOINT ["/wait-for-it.sh","mysql:3306","-t","60","--","java","-Xms256m","-Xmx512m","-jar","/app/app.jar"]