DOCKER_TAG ?= latest

java/build:
	echo "Build jar without tests"
	./mvnw clean package -DskipTests

java/test:
	echo "Run java unit tests"
	./mvnw clean test

java/run:
	java -jar target/workshop_implementing_ddd.jar

docker/build:
	echo "Build slim docker image using multistage build"
	docker build -t larmic/workshop_implementing_ddd:$(DOCKER_TAG) -f src/main/docker/Dockerfile .

docker/run:
	docker run --rm -p 8080:8080 larmic/workshop_implementing_ddd:$(DOCKER_TAG)

http-call:
	curl -i -H "Accept: text/plain" --request GET http://localhost:8080/api/hello