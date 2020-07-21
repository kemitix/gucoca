graphs:
	mvn validate

build:
	mvn install -P quick -DskipTests -DskipITs -Dpitest.skip

run: hawtio.war gucoca-jms.properties
	mvn camel:run -pl camel -Pquick

dev: hawtio.war gucoca-jms.properties
	mvn quarkus:dev -pl gucoca

hawtio.war:
	curl https://repo1.maven.org/maven2/io/hawt/hawtio-default/2.10.1/hawtio-default-2.10.1.war \
		-o hawtio.war

gucoca-jms.properties:
	echo gucoca.jms.brokerurl=tcp://$(HOSTNAME):61616 > gucoca-jms.properties

install:
	mvn install

test:
	mvn verify

clean:
	if [ -f .package ] ; then rm .package ; fi
	if [ -f gucoca-jms.properties ] ; then rm gucoca-jms.properties ; fi
	mvn clean

KEYS:=$(shell aws dynamodb scan --table-name GucocaBroadcastHistory --output text --query "Items[].BroadcastDate.N")

wipe-history:
	for KEY in ${KEYS} ; do \
		aws dynamodb delete-item --table-name GucocaBroadcastHistoryDEV --key "{\"BroadcastDate\":{\"N\":\"$$KEY\"}}" ;\
	done

package: .package

.package:
	mvn package
	touch .package

image: .package hawtio.war gucoca-jms.properties
	docker build . --tag gucoca:latest

stop:
	docker stop gucoca
	docker rm gucoca
