FROM openjdk:10-jre-slim

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=out/artifacts/authentication_service_jar/authentication-service.jar

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]