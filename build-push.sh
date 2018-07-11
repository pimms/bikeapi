#!/bin/bash

if [ -z "$ECS_DOCKER_REPO" ]; then
    echo 'ERROR: Variable "ECS_DOCKER_REPO" is undefined.'
    exit 1
fi

eval $(aws ecr get-login | sed 's/-e none //')

mvn clean install || exit 1

docker build -t bikeapi . || exit 2
docker tag bikeapi:latest "$ECS_DOCKER_REPO/bikeapi:latest" || exit 3
docker push "$ECS_DOCKER_REPO/bikeapi:latest" || exit 4
