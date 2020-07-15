FROM adoptopenjdk:latest

WORKDIR /app

COPY camel/target/libs /app/libs
COPY camel/target/gucoca-camel-*.jar /app/gucoca.jar

CMD ["java", "-jar", "/app/gucoca.jar"]
