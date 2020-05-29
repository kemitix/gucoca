RUN_PARAMS=

graphs:
	mvn validate


install:
	mvn install

dev:
	mvn -pl runner quarkus:dev ${RUN_PARAMS}

run: install
	java ${RUN_PARAMS} -jar runner/target/gucoca-DEV-SNAPSHOT-runner.jar

clean:
	mvn clean

quick-build:
	mvn clean install -DskipTests -DskipITs

kill-runners:
	ps ax|grep clover-runne[r]|cut -b-5|xargs kill -9
