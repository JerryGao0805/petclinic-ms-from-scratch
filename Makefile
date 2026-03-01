SHELL := /bin/bash

MVNW := ./mvnw
MVN_FLAGS := -ntp -B
COMPOSE := docker compose

TAG ?= dev

.PHONY: help test clean package verify docker-build up ps logs down

help:
	@echo ""
	@echo "petclinic-ms-from-scratch - developer commands"
	@echo ""
	@echo "Usage:"
	@echo "  make <target>"
	@echo ""
	@echo "Targets:"
	@echo "  make test         - Run all tests (all modules)"
	@echo "  make package      - Package jars (skip tests)"
	@echo "  make docker-build TAG=<tag> - Build Docker images (default TAG=$(TAG))"
	@echo "  make up           - docker compose up -d (after docker-build)"
	@echo "  make ps           - docker compose ps"
	@echo "  make logs         - docker compose logs -f --tail=200"
	@echo "  make down         - docker compose down"
	@echo "  make clean        - mvn clean (all modules)"
	@echo "  make verify       - Verify running stack (HTTP checks)"
	@echo ""

test:
	$(MVNW) $(MVN_FLAGS) test

clean:
	$(MVNW) $(MVN_FLAGS) clean

package:
	$(MVNW) $(MVN_FLAGS) -DskipTests package

docker-build: package
	docker build -t petclinic/config-server:$(TAG) ./config-server
	docker build -t petclinic/discovery-server:$(TAG) ./discovery-server
	docker build -t petclinic/customers-service:$(TAG) ./customers-service
	docker build -t petclinic/vets-service:$(TAG) ./vets-service
	docker build -t petclinic/visits-service:$(TAG) ./visits-service
	docker build -t petclinic/api-gateway:$(TAG) ./api-gateway

up: docker-build
	TAG=$(TAG) $(COMPOSE) up -d

ps:
	TAG=$(TAG) $(COMPOSE) ps

logs:
	TAG=$(TAG) $(COMPOSE) logs -f --tail=200

down:
	TAG=$(TAG) $(COMPOSE) down

verify:
	bash scripts/verify.sh

