#!/bin/sh

set -x 

if [ -z "${BUILD_ID}" ]; then
    BUILD_ID="(none)"
    VERSION="0.0.1-SNAPSHOT"
    REPOSITORY=snapshots
else
    VERSION="0.0.1.$((${BUILD_ID}))"
    REPOSITORY=releases
fi




BASEDIR=$(dirname "$0")
SCRIPT_DIR=$(cd $BASEDIR && pwd)
SUBPROJECT_DIR=$(dirname $SCRIPT_DIR)
PROJECT_DIR=$(dirname $SUBPROJECT_DIR)
BUILD_DIR=${SUBPROJECT_DIR}/build





PROJECT=mqtt-rpc-common
GROUPID=com.rsmaxwell.mqtt.rpc
ARTIFACTID=${PROJECT}
PACKAGING=zip
ZIPFILE=${ARTIFACTID}_${VERSION}.${PACKAGING}



TIMESTAMP="$(date '+%Y-%m-%d %H:%M:%S')"
GIT_COMMIT="${GIT_COMMIT:-(none)}"
GIT_BRANCH="${GIT_BRANCH:-(none)}"
GIT_URL="${GIT_URL:-(none)}"

export PROJECT
export REPOSITORY
export BUILD_ID
export VERSION
export TIMESTAMP
export GIT_COMMIT
export GIT_BRANCH
export GIT_URL



cd ${SOURCE_DIR}

tags='$PROJECT,$REPOSITORY,$VERSION,$BUILD_ID,$TIMESTAMP,$GIT_COMMIT,$GIT_BRANCH,$GIT_URL'

find . -name Version.java | while read filename; do
    echo "Updating ${filename}"
    originalfile=${SOURCE_DIR}/${filename}
    tmpfile=$(mktemp)
    cp --attributes-only --preserve ${originalfile} ${tmpfile}
    cat ${originalfile} | envsubst > ${tmpfile}
    mv ${tmpfile} ${originalfile}
done




mkdir -p ${BUILD_DIR}
cd ${BUILD_DIR}

cat > buildinfo <<EOL
BUILD_ID="${BUILD_ID}"
VERSION="${VERSION}"
REPOSITORY="${REPOSITORY}"
REPOSITORY_URL="${REPOSITORY_URL}"
PROJECT="${PROJECT}"
GROUPID="${GROUPID}"
ARTIFACTID="${ARTIFACTID}"
PACKAGING="${PACKAGING}"
ZIPFILE="${ZIPFILE}"
TIMESTAMP="${TIMESTAMP}"
GIT_COMMIT="${GIT_COMMIT}"
GIT_BRANCH="${GIT_BRANCH}"
GIT_URL="${GIT_URL}"
EOL

pwd
ls -al 
cat buildinfo

echo "subproject directory"
cd ${SUBPROJECT_DIR}
ls -al 

echo "gradle.properties"
cat gradle.properties
