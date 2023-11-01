.DEFAULT_GOAL := setup-and-run

run-application:
	docker compose \
		--project-directory=${PWD} \
		--project-name=$(shell basename $(CURDIR)) \
		-f deploy/docker-compose.yml \
		up -d

stop-application:
	docker compose \
		--project-directory=${PWD} \
		--project-name=$(shell basename $(CURDIR)) \
		-f deploy/docker-compose.yml \
		down

run-tests:
	gradle --project-dir app runTests

setup-and-run:
	make run-application
	make run-tests

