.DEFAULT_GOAL := setup-and-run

run-application:
	docker pull selenoid/chrome:115.0
	docker pull selenoid/video-recorder:latest-release
	docker compose \
		--project-directory=${PWD} \
		--project-name=$(shell basename $(CURDIR)) \
		-f deploy/docker-compose.yml \
		up --build -d

stop-application:
	docker compose \
		--project-directory=${PWD} \
		--project-name=$(shell basename $(CURDIR)) \
		-f deploy/docker-compose.yml \
		down

run-tests:
	gradle \
		--project-dir services/tests \
		--info \
		runTests

setup-and-run:
	make run-application
	make run-tests

