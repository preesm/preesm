#!/bin/bash -eu

### Config
DEV_BRANCH=develop
MAIN_BRANCH=master
REPO=antoine-morvan/preesm

if [ "$#" -ne "1" ]; then
  CURRENT_VERSION=`mvn -B -Dtycho.mode=maven help:evaluate -Dexpression=project.version | grep -v 'INFO'`
  echo -e "usage: $0 <new version>\nNote: current version = ${CURRENT_VERSION}"
  exit 1
fi

# First check access on git (will exit on error)
echo "Testing Github permission"
git ls-remote git@github.com:${REPO}.git > /dev/null

# then test github oauth access token
echo "Testing Github OAuth token validity"
if [ ! -e ~/.ghtoken ]; then
  echo "Error: could not locate '~/.ghtoken' for reading the Github OAuth token"
  echo "Visit 'https://github.com/settings/tokens/new' and generate a new token with rights on 'repo',"
  echo "then create the file '~/.ghtoken' with the token value as its only content."
  exit 1
fi
OAUTH_TOKEN=$(cat ~/.ghtoken)
STATUS=$(curl -s -i -H "Authorization: $OAUTH_TOKEN to" https://api.github.com/repos/${REPO}/releases | grep "^Status:" | cut -d' ' -f 2)
if [ "$STATUS" != "200" ]; then
  echo "Error: given token in '~/.ghtoken' is invalid or revoked."
  echo "  STATUS = $STATUS"
  echo "Visit 'https://github.com/settings/tokens/new' and generate a new token with rights on 'repo',"
  echo "then replace the content of '~/.ghtoken' with the token value."
  exit 1
fi

# then on SF with maven settings
TMPDIR=`mktemp -d`
cat > $TMPDIR/pom.xml << EOF
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>org.ietr.test</artifactId>
  <groupId>org.ietr</groupId>
  <version>1.0.0</version>
  <packaging>pom</packaging>
  <build>
    <plugins>
      <!-- Finally upload merged metadata and new content -->
      <plugin>
        <groupId>org.preesm.maven</groupId>
        <artifactId>sftp-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
          <execution>
            <id>upload-repo</id>
            <phase>verify</phase>
            <configuration>
              <serverId>preesm-insa-rennes</serverId>
              <serverHost>preesm.insa-rennes.fr</serverHost>
              <serverPort>8022</serverPort>
              <strictHostKeyChecking>false</strictHostKeyChecking>
              <transferThreadCount>1</transferThreadCount>
              <mode>receive</mode>
              <remotePath>/repo/README</remotePath>
              <localPath>${TMPDIR}/to_delete</localPath>
            </configuration>
            <goals>
              <goal>sftp-transfert</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
EOF
echo "Testing Insa server permission using Maven Sftp plugin"
(cd $TMPDIR && mvn -q verify)
rm -rf $TMPDIR


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

#update version in code and stash changes
./releng/update-version.sh $NEW_VERSION
sed -i -e "s/X\.Y\.Z/$NEW_VERSION/g" release_notes.md
sed -i -e "s/XXXX\.XX\.XX/$TODAY_DATE/g" release_notes.md
git stash

# Fix headers
./releng/fix_header_copyright_and_authors.sh
# commit fixed headers (if any)
NBCHANGES=`git status --porcelain | wc -l`
if [ $NBCHANGES -ne 0 ]; then
  git add -A
  git commit -m "[RELENG] Fix headers"
fi

# make sure integration works before deploying and pushing
git stash pop
./releng/build_and_test.sh

#commit new version in develop
git add -A
git commit -m "[RELENG] Prepare version $NEW_VERSION"

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

#deploy from master
git checkout $MAIN_BRANCH
./releng/deploy.sh

#push if everything went fine
git push --tags
git push
git checkout $DEV_BRANCH
git push

TAG=v$NEW_VERSION
GENERATE_POST_BODY() {
  cat <<EOF
{
  "tag_name": "${TAG}",
  "name": "${TAG}",
  "body": "",
  "draft": false,
  "prerelease": false
}
EOF
}

# create GitHub release using API
echo ""
echo "Create release"
API_RESPONSE=$(curl --silent -H "Authorization: token ${OAUTH_TOKEN}" --data "$(GENERATE_POST_BODY)" -X POST "https://api.github.com/repos/${REPO}/releases")

echo ""
echo "Extract Upload URL"
UPLOAD_URL=$(echo ${API_RESPONSE} | \
  sed '0,/{/s/{//' | rev | tac | sed '0,/}/s/}//' | rev | tac | \
  perl -0777 -pe 's/\[[^\]]*\]//igs' | \
  perl -0777 -pe 's/\{[^\{\}]*\}//igs' | \
  perl -0777 -pe 's/\{[^\{\}]*\}//igs' | \
  perl -0777 -pe 's/.*"upload_url": "([^,]+)",?.*/\1/g')

echo "   => ${UPLOAD_URL}"

for PRODUCT in $(ls ./releng/org.ietr.preesm.product/target/products/*.zip); do
  echo " * upload $PRODUCT"
  FILE=$PRODUCT
  curl -H "Authorization: token $OAUTH_TOKEN" -H "Content-Type: $(file -b --mime-type $FILE)" -X POST "$UPLOAD_URL?name=$(basename $FILE)" --upload-file $FILE > /dev/null
done

exit 0
