#!/bin/bash -eu

### Config
DEV_BRANCH=develop
MAIN_BRANCH=master

# First check access on git & SF
#TODO

### Commands
[ "$#" -ne "1" ] && echo "usage: $0 <new version>" && exit 1

#warning
echo "Warning: this script will delete ignored files and remove all changes in $DEV_BRANCH and $MAIN_BRANCH"
read -p "Do you want to conitnue ? [NO/yes] " ANS
LCANS=`echo "${ANS}" | tr '[:upper:]' '[:lower:]'`
[ "${LCANS}" != "yes" ] && echo "Aborting." && exit 1

NEW_VERSION=$1

CURRENT_BRANCH=$(cd `dirname $0` && echo `git branch`)
ORIG_DIR=`pwd`
DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)
TODAY_DATE=`date +%Y.%m.%d`
#change to git root dir
cd $DIR

#move to dev branch and clean repo
git stash -u
git checkout $DEV_BRANCH
git reset --hard
git clean -xdf

./releng/fix_header_copyright_and_authors.sh
#update version in code and create commit
./releng/update-version.sh $NEW_VERSION
sed -i -e "s/X\.Y\.Z/$NEW_VERSION/g" release_notes.md
sed -i -e "s/XXXX\.XX\.XX/$TODAY_DATE/g" release_notes.md
git add -A
git commit -m "[RELENG] Prepare version $NEW_VERSION"

# make sure integration works before deplying and pushing
#TODO

#merge in master, add tag
git checkout $MAIN_BRANCH
git merge --no-ff $DEV_BRANCH -m "merge branch '$DEV_BRANCH' for new version $NEW_VERSION"
git tag v$NEW_VERSION

#move to snapshot version in develop and push
git checkout $DEV_BRANCH
./releng/update-version.sh $NEW_VERSION-SNAPSHOT
cat release_notes.md | tail -n +3 > tmp
cat > release_notes.md << EOF
PREESM Changelog
================

## Release version X.Y.Z
*XXXX.XX.XX*

### New Feature

### Changes

### Bug fix

EOF
cat tmp >> release_notes.md
rm tmp
git add -A
git commit -m "[RELENG] Move to snapshot version"
git push

#deploy and push master (that is new version)
git checkout master
git push
git push --tags
./releng/deploy.sh

#get back to original branch and restore work
git checkout $CURRENT_BRANCH
STASH_COUNT=`git stash list | wc -l`
[ "$STASH_COUNT" != "0" ] && git stash pop
#get back to original dir
cd $ORIG_DIR
