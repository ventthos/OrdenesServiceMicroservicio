# 1️⃣ Imagen base con Java 17
FROM eclipse-temurin:17-jdk-jammy

# 2️⃣ Carpeta de trabajo dentro del contenedor
WORKDIR /app

# 3️⃣ Copiar el jar construido de tu Eureka Server
COPY target/OrdenService-0.0.1-SNAPSHOT.jar app.jar

# 4️⃣ Exponer el puerto por defecto de Eureka
EXPOSE 8082

# 5️⃣ Comando para ejecutar la app
ENTRYPOINT ["java","-jar","app.jar"]