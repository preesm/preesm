#!/bin/bash -eu

## the path to the Preesm binary directory
PREESMDIR=${1}

## the folder containing the application to run (where the .project is)
APPDIR=${2}

## workflow and scenario to run (must be respectively under Workflows and Scenarios folder within the project)
WORKFLOW=${3}
SCENARIO=${4}

# returns the JDK version.
# 8 for 1.8.0_nn, 9 for 9-ea etc, and "no_java" for undetected
jdk_version() {
  local result
  local java_cmd
  if [[ -n $(type -p java) ]]
  then
    java_cmd=java
  elif [[ (-n "$JAVA_HOME") && (-x "$JAVA_HOME/bin/java") ]]
  then
    java_cmd="$JAVA_HOME/bin/java"
  fi
  local IFS=$'\n'
  # remove \r for Cygwin
  local lines=$("$java_cmd" -Xms32M -Xmx32M -version 2>&1 | tr '\r' '\n')
  if [[ -z $java_cmd ]]
  then
    result=no_java
  else
    result=
    for line in $lines; do
      if [[ (-z $result) && ($line = *"version \""*) ]]
      then
        local ver=$(echo $line | sed -e 's/.*version "\(.*\)"\(.*\)/\1/; 1q')
        # on macOS, sed doesn't support '?'
        if [[ $ver = "1."* ]]
        then
          result=$(echo $ver | sed -e 's/1\.\([0-9]*\)\(.*\)/\1/; 1q')
        else
          result=$(echo $ver | sed -e 's/\([0-9]*\)\(.*\)/\1/; 1q')
        fi
      fi
    done
  fi
  echo "$result"
}
 
[ ! -x ${PREESMDIR}/eclipse ] && echo "Error: \$PREESMDIR does not contain a Preesm distro." && exit 1
[ ! -e ${APPDIR}/.project ] && echo "Error: '${APPDIR}' is not an Eclipse project" && exit 1
[ ! -e ${APPDIR}/Workflows/${WORKFLOW} ] && echo "Error: Could not locate workflow" && exit 1
[ ! -e ${APPDIR}/Scenarios/${SCENARIO} ] && echo "Error: Could not locate scenario" && exit 1

JAVA_VERSION=$(jdk_version)

if [ "$JAVA_VERSION" == "no_java" ]; then
  echo "Error: could not locate java"
  exit 1
fi

if [ $JAVA_VERSION -lt 8 ]; then
  echo "Error: java version is too low. Must be greater or equals to 8";
  exit 2
fi


PROJECT=$(cat $APPDIR/.project | grep -oPm1 "(?<=<name>)[^<]+")
echo $PROJECT
WORKSPACE=$(mktemp -d --suffix=_preesm-workspace)

[ -e ${PREESMDIR}/eclipsec.exe ] && PREESMEXEC=${PREESMDIR}/eclipsec || PREESMEXEC=${PREESMDIR}/eclipse

echo ""
echo "***START*** $(date -R)"
echo ""
echo ""
echo "Init workspace and import project"
echo ""

${PREESMEXEC} --launcher.suppressErrors -nosplash -consolelog -data ${WORKSPACE} -application org.eclipse.cdt.managedbuilder.core.headlessbuild -import ${APPDIR}

echo ""
echo "Run workflow from project $PROJECT"
echo ""

${PREESMEXEC} --launcher.suppressErrors -nosplash -consolelog -data ${WORKSPACE} -application org.preesm.cli.workflowCli ${PROJECT} -d -w ${WORKFLOW} -s ${SCENARIO}

echo ""
echo "***END*** $(date -R)"
echo ""

rm -rf ${WORKSPACE}

exit 0
