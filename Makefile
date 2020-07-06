graphs:
	mvn validate

build:
	mvn install -P quick -Dskip.tests -Dskip.its -Dpitest.skip

run:
	#mvn camel:run -pl camel -Pquick
	mvn hawtio:camel-cdi -pl camel -Pquick

install:
	mvn install

test:
	mvn verify

clean:
	mvn clean
