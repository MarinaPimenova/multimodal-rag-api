#!/bin/bash

# remove all containers
docker rm -v -f $(docker ps -qa)
docker ps -qa

# multimodal-rag-api
# repo_name   tag   container_name
./build-docker-image.sh multimodal-rag-api rag-api rag-api-service
#./build-docker-image.sh /mnt/c/Users/Marina_Pimenova/sb-projects/multimodal-rag-api  rag-api-service

./build-docker-image.sh rag-spa rag-ui rag-ui-service
#./build-docker-image.sh /mnt/c/Users/Marina_Pimenova/sb-projects/rag-spa rag-ui rag-ui-service
