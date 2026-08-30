# Étape 1 : Build avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre


RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    fluxbox \
    novnc \
    websockify \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar


RUN echo '#!/bin/bash\n\
Xvfb :1 -screen 0 1280x800x24 &\n\
sleep 2\n\
fluxbox &\n\
sleep 2\n\
x11vnc -display :1 -forever -shared -nopw &\n\
sleep 2\n\
websockify --web /usr/share/novnc 8080 localhost:5900 &\n\
sleep 2\n\
java -jar app.jar' > /start.sh && chmod +x /start.sh

EXPOSE 8080
ENTRYPOINT ["/start.sh"]