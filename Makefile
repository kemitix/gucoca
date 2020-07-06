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

wipe-history:
	for KEY in $(aws dynamodb scan --table-name GucocaBroadcastHistory --output text --query "Items[].BroadcastDate.N");do\
		aws dynamodb delete-item --table-name GucocaBroadcastHistory --key '{"BroadcastDate":{"N":"$KEY"}}';\
	done
