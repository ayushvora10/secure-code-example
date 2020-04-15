FROM openjdk:8-jdk

COPY secure-code.yml /data/secure-code/secure-code.yml
COPY target/secure-code-0.1.0-SNAPSHOT.jar /data/secure-code/secure-code-0.1.0-SNAPSHOT.jar

WORKDIR /data/secure-code

CMD ["java", "-jar", "secure-code-0.1.0-SNAPSHOT.jar", "server", "secure-code.yml"]

EXPOSE 8080-8081

