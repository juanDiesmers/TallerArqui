FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
CMD sh -c 'until nc -z -v -w30 mysql 3306; do echo "Waiting for MySQL..."; sleep 3; done; java -jar app.jar'
