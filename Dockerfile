# FROM openjdk:latest
# COPY ./target/seMethods-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp
# WORKDIR /tmp
# ENTRYPOINT ["java", "-jar", "seMethods-1.0-SNAPSHOT-jar-with-dependencies.jar"]



FROM openjdk:latest
WORKDIR /app
COPY ./target/seMethods-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
RUN useradd -m appuser
USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
