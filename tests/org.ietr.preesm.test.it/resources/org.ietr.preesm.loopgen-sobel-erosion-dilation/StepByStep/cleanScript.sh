#!/bin/bash

function log() {
	echo "$(date +%X) $1"
}

# Do some initialization
cd "$( dirname "${BASH_SOURCE[0]}" )"
COMMITID=$(git log -1 --skip=4 --pretty="%h")

# Reset the Git repository to original state
log "Reset the repository to original commit $COMMITID"
git reset --hard HEAD~4