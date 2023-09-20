DOCKER_TAG ?= latest
IMAGE_NAME=larmic/workshop_implementing_ddd
CONTAINER_NAME=larmic-workshop_implementing_ddd

help: ## Outputs this help screen
	@grep -E '(^[a-zA-Z0-9_-]+:.*?##.*$$)|(^##)' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}{printf "\033[32m%-30s\033[0m %s\n", $$1, $$2}' | sed -e 's/\[32m##/[33m/'

## —— Build 🏗️———————————————————————————————————————————————————————————————————————————————————————————————————
java-build: ## Builds java artifact excluding tests
	echo "Build jar without tests"
	./mvnw clean package -DskipTests

docker-build: ## Builds docker container
	echo "Build slim docker image using multistage build"
	DOCKER_BUILDKIT=1 docker build -t $(IMAGE_NAME):$(DOCKER_TAG) -f src/main/docker/Dockerfile .

## —— Run application 🏃🏽————————————————————————————————————————————————————————————————————————————————————————
java-run: java/build ## Run java application
	java -jar target/workshop_implementing_ddd.jar

docker-run: ## Run docker image
	docker run --rm -p 8080:8080 --name ${CONTAINER_NAME} $(IMAGE_NAME):$(DOCKER_TAG)

## —— Testing 👀️—————————————————————————————————————————————————————————————————————————————————————————————————
java-test: ## Runs unit and integration tests
	echo "Run java tests"
	./mvnw clean verify

http-test-request: ## Sends a test rest request | NOTE: a running backend application is required
	docker run --rm -i -t -v ./:/workdir jetbrains/intellij-http-client:231.9011.14 \
      -L VERBOSE \
      -e docker \
      -v ./misc/rest-requests/http-client.env.json \
      ./misc/rest-requests/requests.http