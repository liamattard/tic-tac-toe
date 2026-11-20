FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd --create-home --uid 10001 app \
    && mkdir logs \
    && chown app:app logs
COPY target/scala-2.13/tictactoe.jar ./tictactoe.jar
USER app
ENTRYPOINT ["java", "-jar", "/app/tictactoe.jar"]
