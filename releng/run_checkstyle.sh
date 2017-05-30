#!/bin/bash

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

(cd "$DIR" && mvn -P releng -Dtycho.mode=maven -Dmain.basedir="$DIR" checkstyle:check)
