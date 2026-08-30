FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:copy-dependencies -DoutputDirectory=libs
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
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/client-1.0.0.jar app.jar
COPY --from=build /app/libs/*.jar /app/lib/

RUN echo '#!/bin/bash\n\
export DISPLAY=:1\n\
rm -f /tmp/.X1-lock\n\
Xvfb :1 -screen 0 1280x800x24 &\n\
sleep 3\n\
fluxbox &\n\
sleep 3\n\
x11vnc -display :1 -forever -shared -nopw &\n\
sleep 3\n\
websockify --web /usr/share/novnc 8080 localhost:5900 &\n\
sleep 3\n\
java -cp "app.jar:lib/*" com.centremedical.client.Main' > /start.sh && chmod +x /start.sh

EXPOSE 8080
ENTRYPOINT ["/start.sh"]