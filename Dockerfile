FROM bellsoft/liberica-openjdk-alpine:17
ARG JAR_FILE=build/libs/recommend-app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
