FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "./gradlew bootRun --no-daemon $JAVA_OPTS"]
