#!/bin/bash

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

(cd "$DIR" && java -jar releng/hooks/checkstyle-7.6.1-all.jar -c releng/VAADER_checkstyle.xml plugins/ test-fragments/ releng/)
