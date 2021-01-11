#!/bin/bash

USRHOME=${HOME}

#config
ECLIPSE_BASE_VERSION=photon
PREESM_VERSION=2.15.1
PREESM_ROOT=/opt/preesm/
PREESM_WKSPACE="${HOME}/preesm-workspace/"
PREESM_DATA="${HOME}/preesm-data/"

function check_java_version {
	JAVA8=0
	JAVA9=0
	JAVA8=`java -version 2>&1 | grep 1.8 | wc -l`
	if [ "$JAVA8" == "0" ]; then
		JAVA9=`java -version 2>&1 | grep 1.9 | wc -l`
		if [ "$JAVA9" == "0" ]; then
			cat << "EOF"

Eclipse requires Java 8 or higher. 

On debian like systems (as root) :
   $ apt-get install openjdk-8-jdk openjdk-8-source
   $ JVM=`update-java-alternatives -l | grep 1.8 | cut -d" " -f 1 | head -n 1` 
   $ update-java-alternatives -s $JVM

If you have Ubuntu 14.04, follow (as root also):
   $ apt-add-repository ppa:webupd8team/java
   $ apt-get update
   $ apt-get install oracle-java8-installer

On other Linux distributions, Windows and MacOSX systems, please
visit http://www.oracle.com/technetwork/java/javase/downloads/index.html

EOF
			exit 1
		fi
	fi
}
check_java_version

if [ `whoami` != root ]; then
  echo "Please enter root password:"
  sudo $0 || exit 1
  exit 0
fi

if [ -d "${PREESM_WKSPACE}" ]; then
  echo "Warning: this script will erase your workspace at ${PREESM_WKSPACE}."
  read -p "Do you confirm ? [NO/yes] " ANS
  LCANS=`echo "${ANS}" | tr '[:upper:]' '[:lower:]'`
  [ "${LCANS}" != "yes" ] && echo "Aborting." && exit 1
fi

USR=${SUDO_USER}
source ${HOME}/.config/user-dirs.dirs


#install dependencies
echo ""
echo "Install PREESM Dependencies"
echo ""
apt install -y p7zip-full ttf-dejavu fonts-dejavu cmake gcc g++ libsdl2-dev libsdl2-ttf-dev libpthread-stubs0-dev libevent-pthreads-2.0-5
[ "$?" != "0" ] && echo "error" && exit 1

if [ -e "${PREESM_ROOT}/version" ] && [ "`cat ${PREESM_ROOT}/version`" == "${PREESM_VERSION}" ]; then
  echo ""
  echo "Skip PREESM install since version is matching."
  echo ""
else
  echo ""
  echo "Install PREESM"
  echo ""
  #remove previous version (if present)
  rm -rf "${PREESM_ROOT}"
  #download and install release
  wget "https://github.com/preesm/preesm/releases/download/v${PREESM_VERSION}/preesm-${PREESM_VERSION}-linux.gtk.x86_64.zip" -O /tmp/preesm.zip
  #wget "http://downloads.sourceforge.net/project/preesm/Releases/${PREESM_VERSION}/preesm-${PREESM_VERSION}-linux.gtk.x86_64.zip" -O /tmp/preesm.zip
  [ "$?" != "0" ] && echo "error" && exit 1
  mkdir -p "${PREESM_ROOT}"
  unzip /tmp/preesm.zip -d "${PREESM_ROOT}"
  rm /tmp/preesm.zip
  echo "${PREESM_VERSION}" > "${PREESM_ROOT}/version"
fi

#EasyShell
ls ${PREESM_ROOT}/features/de.anbos.eclipse.easyshell.feature* &> /dev/null
if [ "$?" != "0" ]; then
  REPOS="http://anb0s.github.io/EasyShell"
  UIS=" -installIU de.anbos.eclipse.easyshell.feature.feature.group"
  ${PREESM_ROOT}/eclipse -nosplash -consolelog -clean -purgeHistory  -application org.eclipse.equinox.p2.director -followReferences -repository ${REPOS} -destination ${PREESM_ROOT} ${UIS}
  [ "$?" != "0" ] && echo "error" && exit 1
else
  echo "  >> EasyShell already installed"
fi

#TM Terminal (and eclipse default)
ls ${PREESM_ROOT}/features/org.eclipse.tm.terminal.feature* &> /dev/null
if [ "$?" != "0" ]; then
  REPOS="http://download.eclipse.org/releases/${ECLIPSE_BASE_VERSION}"
  UIS=" -installIU org.eclipse.tm.terminal.feature.feature.group"
  ${PREESM_ROOT}/eclipse -nosplash -consolelog -clean -purgeHistory  -application org.eclipse.equinox.p2.director -followReferences -repository ${REPOS} -destination ${PREESM_ROOT} ${UIS}
  [ "$?" != "0" ] && echo "error" && exit 1
else
  echo "  >> TM Terminal already installed"
fi

echo ""
echo "Add Shortcut"
echo ""
#config and add shortcuts
echo "osgi.instance.area.default=${PREESM_WKSPACE}" >> "${PREESM_ROOT}/configuration/config.ini"
ln -s "${PREESM_ROOT}/eclipse" /usr/local/bin/preesm
cat > /usr/share/applications/preesm.desktop << EOF
[Desktop Entry]
Type=Application
Version=1.0
Name=PREESM
GenericName=Parallel and Real-time Embedded Executives Scheduling Method
Comment=Eclipse based IDE for the rapid prototyping of dataflow specified applications on heterogeneous mutlicore embedded systems
Exec=${PREESM_ROOT}/eclipse
Icon=${PREESM_ROOT}/icon.xpm
Terminal=false
Categories=Development;IDE;
StartupNotify=true
EOF
cp /usr/share/applications/preesm.desktop "${XDG_DESKTOP_DIR}/PREESM.desktop"
chmod 777 "${XDG_DESKTOP_DIR}/PREESM.desktop"

echo ""
echo "Import demo project"
echo ""
#delete workspace if present
rm -rf "${PREESM_WKSPACE}"
rm -rf "${PREESM_WKSPACE}"
mkdir -p "${PREESM_WKSPACE}"

#download & extract demo project
#v_parallel: http://preesm.sourceforge.net/website/data/uploads/tutorial_zips/tutorial1_result.zip
#v_origin : http://preesm.sourceforge.net/website/data/uploads/tutorial_zips/org.ietr.preesm.sobel.zip
echo "Download project (force update if present)"
wget --no-cache https://preesm.github.io/assets/tutos/parasobel/tutorial1_result.zip -O /tmp/sobel.zip
unzip /tmp/sobel.zip -d "${PREESM_WKSPACE}/"
rm /tmp/sobel.zip
echo " * Do import"
echo ""
#import project in eclipse workspace
"${PREESM_ROOT}/eclipse" -nosplash -data "${PREESM_WKSPACE}" -application org.eclipse.cdt.managedbuilder.core.headlessbuild -import "${PREESM_WKSPACE}/org.ietr.preesm.sobel"

echo ""
echo "Download video material, if necessary"
echo ""
#add sample video
mkdir -p "${PREESM_DATA}"
if [ -e "${PREESM_DATA}/akiyo_cif.yuv" ]; then
  echo "Skip video download"
else
  echo "Download sample video"
  wget --no-cache https://preesm.github.io/assets/downloads/akiyo_cif.7z -O "${PREESM_DATA}/akiyo_cif.7z"
  (cd "${PREESM_DATA}" && 7z x akiyo_cif.7z && rm akiyo_cif.7z)
  [ -e "${PREESM_DATA}/akiyo_cif" ] && (cd "${PREESM_DATA}" && 7z x akiyo_cif && akiyo_cif)
fi
cp "${PREESM_DATA}/akiyo_cif.yuv" "${PREESM_WKSPACE}/org.ietr.preesm.sobel/Code/dat/akiyo_cif.yuv"

#add ttf font
rm -f "${PREESM_DATA}/DejaVuSans.ttf"
ln -s /usr/share/fonts/truetype/dejavu/DejaVuSans.ttf "${PREESM_DATA}/DejaVuSans.ttf"
cp "${PREESM_DATA}/DejaVuSans.ttf" "${PREESM_WKSPACE}/org.ietr.preesm.sobel/Code/dat/DejaVuSans.ttf"

#set permissions
chown -R ${USR} "${XDG_DESKTOP_DIR}/PREESM.desktop" "${PREESM_ROOT}" "${PREESM_WKSPACE}" "${PREESM_DATA}"
chmod a-w "${PREESM_DATA}"

#set update script
cat > /usr/local/bin/preesm-update << EOF
#!/bin/bash

echo "Updating PREESM..."
wget -q --no-cache http://preesm.insa-rennes.fr/downloads/preesm_setup.sh -O /tmp/preesm_setup.sh
chmod +x /tmp/preesm_setup.sh
/tmp/preesm_setup.sh
echo "PREESM Update done."
exit 0

EOF
chmod a+x /usr/local/bin/preesm-update

