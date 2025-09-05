FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar el proyecto con Maven instalado
RUN mvn package -DskipTests

# Imagen final
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]