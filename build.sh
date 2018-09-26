#!/bin/sh

set -ex

DIST=false
PUSH=false
VERSION=$(cat VERSION | tr -d '\n')
TAG=nokamoto13/webpush-testing-service:$VERSION

for arg in "$@"
do
    if [ "$arg" = "--push" ]
    then
        PUSH=true
    elif [ "$arg" = "--dist" ]
    then
        DIST=true
    fi
done

if "$DIST"
then
    sbt universal:packageZipTarball
fi

docker build -t $TAG --build-arg VERSION=$VERSION .

if "$PUSH"
then
    docker push $TAG
fi
