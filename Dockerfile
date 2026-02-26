# Use lightweight Java runtime
FROM eclipse-temurin:22-jre
LABEL authors="Sakawijaya"

WORKDIR /app

# Copy your jar (assume Maven build)
COPY target/*.jar app.jar

# JVM memory limits for Pi 3 B+ (1GB RAM)
ENTRYPOINT ["java","-Xms128m","-Xmx256m","-jar","/app/app.jar"]