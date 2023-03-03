#!/bin/bash -eu

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

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

SONAR=NO
CI=NO
RUNTEST=YES

if [ ! -z ${1+x} ]; then
  if [ "$1" == "--sonar" ]; then
    SONAR=YES
  fi
  if [ "$1" == "--notest" ]; then
    RUNTEST=NO
  fi
  if [ "$1" == "--ci" ]; then
    CI=YES
    SONAR=NO
  fi
fi

if [ "${RUNTEST}" == "NO" ]; then
  TESTMODE="-DskipTests=true -Dmaven.test.skip=true"
  BUILDGOAL=package
  SONAR=NO
else
  TESTMODE=
  BUILDGOAL=verify
fi

if [ "${CI}" == "YES" ]; then
  BATCHMODE="-B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
else
  BATCHMODE=
fi

if [ -x /usr/bin/Xvfb ]; then
  XDN=$(find_free_display_number)
  export DISPLAY=:${XDN}.0
  /usr/bin/Xvfb :${XDN} -ac -screen 0 1280x1024x16&
  XVFBPID=$!
fi

echo ""
echo " -- Build script values --"
echo "Batch mode = ${BATCHMODE}"
echo "Run tests = ${RUNTEST}"
echo "Test mode = ${TESTMODE}"
echo "Sonar = ${SONAR}"
echo "Build goal = ${BUILDGOAL}"
echo " --"
echo ""


(cd $DIR && ./releng/fetch-rcptt-runner.sh)
time (cd $DIR && mvn -X -e -c ${BATCHMODE} clean ${BUILDGOAL} ${TESTMODE} &> maven.log)

cat maven.log

# Sonar
if [ "${SONAR}" == "YES" ]; then
  # needs to be run in a second step; See:
  # https://community.sonarsource.com/t/jacoco-coverage-switch-from-deprecated-binary-to-xml-format-in-a-tycho-build-shows-0/
  (cd $DIR && mvn -e -c ${BATCHMODE} -Dtycho.mode=maven jacoco:report -Djacoco.dataFile=../../target/jacoco.exec -Dsonar.projectKey=preesm_preesm -Dsonar.login=$SONAR_TOKEN sonar:sonar )


fi


if [ -x /usr/bin/Xvfb ]; then
  kill -2 $XVFBPID
fi

exit 0
