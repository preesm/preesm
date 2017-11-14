#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

(cd $DIR && mvn -e -C -U -V -P releng clean deploy)
