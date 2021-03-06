FROM adoptopenjdk:latest

WORKDIR /app

COPY hawtio.war /app/
COPY camel/target/libs /app/libs
COPY gucoca-jms.properties /app/
COPY camel/target/gucoca-camel-*.jar /app/gucoca.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/gucoca.jar"]
