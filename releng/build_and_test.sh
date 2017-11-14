#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

(cd $DIR && mvn -U -e -C -V -P releng -Dtycho.mode=maven help:help -q)
(cd $DIR && mvn -U -e -C -V -P releng -Dtycho.mode=maven dependency:go-offline)
(cd $DIR && mvn --offline -e -C -V -P releng -Dtycho.mode=maven checkstyle:check)
(cd $DIR && mvn -U -e -C -V -P releng help:help)
(cd $DIR && mvn --offline -e -C -V clean package -fae)
(cd $DIR && mvn --offline -e -C -V clean verify -fae)
(cd $DIR && mvn --offline -e -C -V -P releng clean package -fae -Dmaven.test.skip=true)
