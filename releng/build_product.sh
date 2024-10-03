#!/bin/sh

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

cd $DIR && mvn -T 2.0C clean package -DskipTests

