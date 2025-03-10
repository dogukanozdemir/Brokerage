FROM openjdk:22-jdk

RUN groupadd -r app && useradd -r -g app app && mkdir /data && chown -R app:app /data

USER app

COPY build/libs/brokerage-latest.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]