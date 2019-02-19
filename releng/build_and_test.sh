#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

FETCH=NO
FAST=NO
CHECK=NO
if [ ! -z ${1+x} ]; then
  if [ "$1" == "--fetch" ]; then
    FETCH=YES
  fi
  if [ "$1" == "--check" ]; then
    CHECK=YES
  fi
  if [ "$1" == "--fast" ]; then
    FAST=YES
  fi
fi

# enable Sonar on Travis, from preesm/preesm only
if [ ! -z ${TRAVIS+x} ] && [ ! -z ${TRAVIS_REPO_SLUG+x} ] && [ "${TRAVIS_REPO_SLUG}" == "preesm/preesm" ] && [ ! -z ${TRAVIS_PULL_REQUEST+x} ] && [ "${TRAVIS_PULL_REQUEST}" == "false" ]; then
  SONAR="sonar:sonar"
else
  SONAR=
fi

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


#fetch version:
if [ "$FETCH" == "YES" ]; then
  echo "Fetch dependencies ..."
  time (
    (cd $DIR && ./releng/fetch-rcptt-runner.sh)
    (cd $DIR && mvn -U -e -C -B -Dtycho.mode=maven dependency:go-offline)
    (cd $DIR && mvn -U -e -C -B help:help)
  )
  exit 0
fi

#check version:
if [ "$CHECK" == "YES" ]; then
  echo "Check code ..."
  time (cd $DIR && mvn  -e -C -B -Dtycho.mode=maven checkstyle:check)
  exit 0
fi

#fast version:
if [ "$FAST" == "YES" ]; then
  echo "Fast build ..."
  time (
    (cd $DIR && ./releng/fetch-rcptt-runner.sh)
    (cd $DIR && mvn -e -B -C clean verify ${SONAR})
  )
  exit 0
fi

time (
  #validate POM
  echo ""
  echo "Validate POM"
  echo ""
  (cd $DIR && mvn -U -e -C -B -V -Dtycho.mode=maven help:help -q) || exit 1
  #fetch maven deps
  echo ""
  echo "Fetch Maven Deps"
  echo ""
  (cd $DIR && ./releng/fetch-rcptt-runner.sh) || exit 21
  (cd $DIR && mvn -U -e -C -B -V -Dtycho.mode=maven dependency:go-offline) || exit 22
  #CHECKSTYLE (offline)
  echo ""
  echo "Checkstyle"
  echo ""
  (cd $DIR && mvn -e -C -B -V -Dtycho.mode=maven checkstyle:check) || exit 3
  #fetch P2 deps
  echo ""
  echo "Fetch P2 Deps"
  echo ""
  (cd $DIR && mvn -U -e -C -B -V help:help) || exit 4
  #clean (offline)
  echo ""
  echo "Clean"
  echo ""
  (cd $DIR && mvn -e -C -B -V -Dtycho.mode=maven clean) || exit 5
  #build code and package (offline, no tests)
  echo ""
  echo "Build & Package"
  echo ""
  (cd $DIR && mvn -e -C -B -V package -fae -Dmaven.test.skip=true) || exit 6
  # build and run tests (offline)
  echo ""
  echo "Test all & Run Sonar"
  echo ""
  (cd $DIR && mvn -e -C -B -V verify ${SONAR} -fae) || exit 7
)


if [ -x /usr/bin/Xvfb ]; then
  kill -2 $XVFBPID
fi

exit 0
