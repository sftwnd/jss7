#!/bin/bash
docker run -it --rm --name jss7 \
  --mount type=bind,source=.,target=/src \
  --mount type=bind,source=$HOME/.m2,target=/root/.m2 \
  --mount type=bind,source=./settings.xml,target=/root/.m2/settings.xml \
  -w /src \
  centos-sctp-mvn-libericajdk8 \
  bash
