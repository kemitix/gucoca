RUN_PARAMS=

graphs:
	mvn validate


install:
	mvn install -P quick

dev:
	mvn -pl runner spring-boot:run ${RUN_PARAMS}

run: install
	java ${RUN_PARAMS} -jar runner/target/gucoca-DEV-SNAPSHOT.jar

clean:
	mvn clean

quick-build:
	mvn clean install -DskipTests -DskipITs
