#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

#fast version:
#(cd $DIR && mvn -U -e -C -B -V -P doUpdateSite clean verify sonar:sonar -fae)

time (
  #validate POM
  echo ""
  echo "Validate POM"
  echo ""
  (cd $DIR && mvn -U -e -C -B -V -P doUpdateSite -Dtycho.mode=maven help:help -q) || exit 1
  #fetch maven deps
  echo ""
  echo "Fetch Maven Deps"
  echo ""
  (cd $DIR && mvn -U -e -C -B -V -P doUpdateSite -Dtycho.mode=maven dependency:go-offline) || exit 2
  #CHECKSTYLE (offline)
  echo ""
  echo "Checkstyle (offline)"
  echo ""
  (cd $DIR && mvn --offline -e -C -B -V -Dtycho.mode=maven checkstyle:check) || exit 3
  #fetch P2 deps
  echo ""
  echo "Fetch P2 Deps"
  echo ""
  (cd $DIR && mvn -U -e -C -B -V -P doUpdateSite help:help) || exit 4
  #clean (offline)
  echo ""
  echo "Clean (offline)"
  echo ""
  (cd $DIR && mvn --offline -e -C -B -V -P doUpdateSite -Dtycho.mode=maven clean) || exit 5
  #build code and package (offline, no tests)
  echo ""
  echo "Build & Package (offline)"
  echo ""
  (cd $DIR && mvn --offline -e -C -B -V package -fae -Dmaven.test.skip=true) || exit 6
  # build and run tests (offline)
  echo ""
  echo "Test all & Run Sonar (offline)"
  echo ""
  (cd $DIR && mvn --offline -e -C -B -V verify -fae) || exit 7
  #package update site (offline, no tests)
  echo ""
  echo "Package update site (offline)"
  echo ""
  (cd $DIR && mvn --offline -e -C -B -V -P doUpdateSite package -fae -Dmaven.test.skip=true) || exit 8
)

exit 0
