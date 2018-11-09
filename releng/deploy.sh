#!/bin/bash -eu

DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

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

$DIR/releng/fetch-rcptt-runner.sh

if [ -x /usr/bin/Xvfb ]; then
  XDN=$(find_free_display_number)
  export DISPLAY=:${XDN}.0
  /usr/bin/Xvfb :${XDN} -ac -screen 0 1280x1024x16&
  XVFBPID=$!
fi

(cd $DIR && mvn -e -C -U -V -P doUpdateSite clean deploy)

if [ -x /usr/bin/Xvfb ]; then
  kill -2 $XVFBPID
fi
