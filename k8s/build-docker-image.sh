#!/bin/bash

usage() {
  echo "Usage: $0 <repository_name> <tag>"
  exit 1
}

repo_name=$1
tag_arg=$2
container_name=$3
if [ -z "$tag_arg" ] || [ -z "$repo_name" ]; then
  usage
fi


#docker rm -f $(docker ps -aqf "name=$container_name")
# Remove Docker containers by tag
# docker rm $(docker container ls -a -q --filter ancestor=image-name:tag --filter status=exited)

# remove docker images based on name
# docker rmi $(docker images 'imagename')

local_image=$(docker images -q "$tag_arg"-local)
if [ -n "$local_image" ]; then
  docker rmi -f "$local_image"
fi

cd ../..
working_dir="$repo_name"
cd "$working_dir"
path=$(pwd)
echo "working_dir: $path"

docker build -t "$tag_arg"-local "$path"

if [ $? -ne 0 ]; then
  echo "Error: The command failed."
  exit 1
fi

#docker-compose --env-file ./default.env up --build

