#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

(cd "$DIR" && mvn -Dtycho.mode=maven checkstyle:check)
