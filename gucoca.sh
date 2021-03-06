#!/usr/bin/env bash

set -e

docker run -d \
       --name gucoca \
       -p 8080:8080 \
       --mount type=bind,source=${HOME}/.config,target=/root/.config \
       --mount type=bind,source=${HOME}/.aws,target=/root/.aws \
       --env GUCOCA_ENV=${GUCOCA_ENV} \
       gucoca:latest
