#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

#fast version:
#(cd $DIR && mvn -U -e -C -B -V -P doUpdateSite clean verify sonar:sonar -fae)

function find_free_display_number {
  USEDXDISPLAYS=`find /tmp -maxdepth 1 -type f -name ".X*-lock" | rev | cut -d"/" -f 1 | colrm 1 5 | rev | colrm 1 2`
  for i in {99..1}
  do
    FREE=YES
    for usedd in $USEDXDISPLAYS; do
      if [ "$usedd" == "$i" ]; then
        FREE=NO
        break
      fi
    done
    if [ "$FREE" == "YES" ]; then
      echo $i
      return
    fi
  done
}
if [ -x /usr/bin/Xvfb ]; then
  XDN=$(find_free_display_number)
  export DISPLAY=:${XDN}.0
  /usr/bin/Xvfb :${XDN} -ac -screen 0 1280x1024x16&
  XVFBPID=$!
fi

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

if [ -x /usr/bin/Xvfb ]; then
  kill -2 $XVFBPID
fi

exit 0
