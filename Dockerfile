FROM maven:3.8.5-openjdk-17 AS MAVEN_BUILD
LABEL maintainer="Veersa technologies"
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -Dmaven.test.skip package

FROM openjdk:17.0.2-jdk
RUN groupadd -r weblogic && useradd -r -g weblogic weblogic
USER weblogic
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/payor-rest-adaptor-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT exec java -Xms2G -Xmx6G  -jar payor-rest-adaptor-0.0.1-SNAPSHOT.jar