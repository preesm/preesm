#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

(cd "$DIR" && mvn -P doUpdateSite -Dtycho.mode=maven checkstyle:check)
