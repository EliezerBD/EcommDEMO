# ========================================
# Etapa 1: BUILD - Compilar la aplicación
# ========================================
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para cache de dependencias)
COPY pom.xml .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Romper caché forzado
RUN echo "Force rebuild 12345"

# Compilar la aplicación (saltando tests para build más rápido)
RUN mvn clean package -DskipTests

# ========================================
# Etapa 2: RUNTIME - Imagen final ligera
# ========================================
FROM eclipse-temurin:21-jre-alpine

# Metadatos de la imagen
LABEL maintainer="tu-email@ejemplo.com"
LABEL description="E-commerce Catalog Service"
LABEL version="0.0.1-SNAPSHOT"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto (configurable con variable de entorno)
EXPOSE 8081

# Configurar JVM para contenedores
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
