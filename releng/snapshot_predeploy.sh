#!/bin/bash -eu

TARGET_GH_REPO=preesm/preesm-snapshot-site
SITE_GITHUB_BRANCH=gh-pages
NEW_SITE_FOLDER=new-site
MAX_VERSIONS_COUNT=10
SITE_NAME="Preesm Snapshots - Eclipse Update Site"

##
## adapted from
## https://github.com/jbosstools/jbosstools-build-ci/blob/jbosstools-4.4.x/util/cleanup/jbosstools-cleanup.sh#L255 
##
function regenCompositeMetadata () {
	subdirs=$1
  targetFolder=$2
  
  now=$(date +%s000)
  
  countChildren=0
	for sd in $subdirs; do
    countChildren=$((countChildren + 1))
  done
	
	echo "<?xml version='1.0' encoding='UTF-8'?><?compositeArtifactRepository version='1.0.0'?>
<repository name='${SITE_NAME}' type='org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository' version='1.0.0'>
  <properties size='2'><property name='p2.timestamp' value='${now}'/><property name='p2.compressed' value='true'/></properties>
  <children size='${countChildren}'>" > ${targetFolder}/compositeContent.xml
	for sd in $subdirs; do
		echo "    <child location='${sd}'/>" >> ${targetFolder}/compositeContent.xml
	done
	echo "</children>
</repository>
" >> ${targetFolder}/compositeContent.xml

	echo "<?xml version='1.0' encoding='UTF-8'?><?compositeArtifactRepository version='1.0.0'?>
<repository name='${SITE_NAME}' type='org.eclipse.equinox.internal.p2.artifact.repository.CompositeArtifactRepository' version='1.0.0'>
  <properties size='2'><property name='p2.timestamp' value='${now}'/><property name='p2.compressed' value='true'/></properties>
  <children size='${countChildren}'>" > ${targetFolder}/compositeArtifacts.xml
	for sd in $subdirs; do
		echo "    <child location='${sd}'/>" >> ${targetFolder}/compositeArtifacts.xml
	done
	echo "  </children>
</repository>
" >> ${targetFolder}/compositeArtifacts.xml
}

GIT_ROOT_DIR=$(cd `dirname $0` && echo `git rev-parse --show-toplevel`)

CURRENT_SITE_FOLDER=current-site



## -- fetch current site
rm -rf ${CURRENT_SITE_FOLDER}
git clone https://github.com/${TARGET_GH_REPO}.git -b ${SITE_GITHUB_BRANCH} ${CURRENT_SITE_FOLDER}

CURRENT_PUBLISHED_RELEASES=$(find ${CURRENT_SITE_FOLDER} -maxdepth 1 -regextype posix-egrep -regex "${CURRENT_SITE_FOLDER}/org.preesm-[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+\$" | rev | cut -d'/' -f 1 | rev | sort -u | head -n ${MAX_VERSIONS_COUNT})

NEW_REPO_FOLDER=releng/org.preesm.site/target/gensite/update-site

GEN_RELEASE=$(find ${NEW_REPO_FOLDER} -maxdepth 1 -regextype posix-egrep -regex "${NEW_REPO_FOLDER}/org.preesm-[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+\$" | rev | cut -d'/' -f 1 | rev)

rm -rf ${NEW_SITE_FOLDER}
mkdir ${NEW_SITE_FOLDER}
for old_release in ${CURRENT_PUBLISHED_RELEASES}; do
  echo "-- cp $old_release"
  cp -R ${CURRENT_SITE_FOLDER}/${old_release} new-site/${old_release}
done

  echo "-- cp $GEN_RELEASE"
cp -R releng/org.preesm.site/target/gensite/update-site/${GEN_RELEASE} ${NEW_SITE_FOLDER}/${GEN_RELEASE}

echo "This is an Eclipse Update site. Please read [https://preesm.github.io/get/#eclipse-update-site](https://preesm.github.io/get/#eclipse-update-site) for more information." > ${NEW_SITE_FOLDER}/index.md

## -- regenerate composite meta data
STABLE_RELEASES="${CURRENT_PUBLISHED_RELEASES} ${GEN_RELEASE}"

regenCompositeMetadata "${STABLE_RELEASES}" "${NEW_SITE_FOLDER}/"
