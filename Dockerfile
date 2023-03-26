FROM alpine:latest
RUN apk update && apk upgrade && apk add openjdk11-jre-headless
VOLUME /tmp
EXPOSE 443 8080

ARG JAR_FILE=target/bloomberg-xchange-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ARG TRUSTSTORE=src/main/resources/Client_truststore.jks
COPY ${TRUSTSTORE} Client_truststore.jks

ARG KEYSTORE=src/main/resources/Server_keystore.jks
COPY ${KEYSTORE} Server_keystore.jks


ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=k8s","/app.jar"]