FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/canal_clientedu-0.0.1-SNAPSHOT.jar canal_clientedu.jar
ENTRYPOINT ["java","-jar","/canal_clientedu.jar", "&"]
