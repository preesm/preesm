#!/bin/bash

function log() {
	echo "$(date +%X) $1"
}

# Do some initialization
cd "$( dirname "${BASH_SOURCE[0]}" )"

STEPBYSTEPDIR=$PWD
ARCHIVEDIR=$PWD/archives
COMMITID=$(git log -1 --pretty="%h")
log "Original state is commit $COMMITID"

# Empty the achives directory
log "Empty the achives directory $ARCHIVEDIR"
rm -r -f $ARCHIVEDIR/*

# Test preesm workflow
log "Test Preesm workflow before 1st tutorial"
$RUNSCRIPTS/preesm_run_workflow.sh $GITDIR/preesm-apps/tutorials $RUNTIMEWORKSPACE $ECLIPSERUN org.ietr.preesm.sobel Codegen.workflow 1core.scenario

# Create the original archive for the tutorial
log "Create the original archive for the tutorial"
cd .. 
zip $ARCHIVEDIR/org.ietr.preesm.sobel.zip \
	Algo/* \
	Archi/* \
	Code/* \
		Code/include/* \
		Code/lib/cmake_modules/* \
		Code/lib/ReadMe.txt \
		Code/src/* \
	Scenarios/* \
	Workflows/* .\
	.project \
	Readme.txt
	
# Apply changes from 1st tutorial
log "Apply first tutorial"
git am --signoff -k $STEPBYSTEPDIR/tuto1.patch

# Test preesm workflow
log "Test Preesm workflow after 1st tutorial"
$RUNSCRIPTS/preesm_execute_workflow.sh $RUNTIMEWORKSPACE $ECLIPSERUN org.ietr.preesm.sobel Codegen.workflow 4core.scenario

# Create the archive for the tutorial 1 result
log "Create the archive for the tutorial 1 result"
zip $ARCHIVEDIR/tutorial1_result.zip \
	Algo/* \
	Archi/* \
	Code/* \
		Code/include/* \
		Code/lib/cmake_modules/* \
		Code/lib/ReadMe.txt \
		Code/src/* \
	Scenarios/* \
	Workflows/* .\
	.project \
	Readme.txt

# Create the archive containing sobel sources
log "Create the archive containing sobel sources"
zip -j $ARCHIVEDIR/sobel_sources.zip \
	Code/include/sobel.h \
	Code/src/sobel.c
	
# Create the archive containing split/merge sources
log "Create the archive containing split/merge sources"
zip -j $ARCHIVEDIR/splitMerge_sources.zip \
	Code/include/splitMerge.h \
	Code/src/splitMerge.c

# Apply changes from DSP tutorial
log "Apply DSP tutorial"
git am --signoff -k $STEPBYSTEPDIR/tutoDSP.patch

# Test preesm workflow
log "Test Preesm workflow after DSP tutorial"
$RUNSCRIPTS/preesm_execute_workflow.sh $RUNTIMEWORKSPACE $ECLIPSERUN org.ietr.preesm.sobel Codegen.workflow 8coreC6678.scenario

# Create the archive containing c6678 sources
log "Create the archive containing c6678 sources"
zip $ARCHIVEDIR/sobel_6678_sources.zip \
	CodeC6678/image_analyzer/* \
	CodeC6678/include/* \
	CodeC6678/src/* \
	CodeC6678/yuv2dat/* \
		CodeC6678/yuv2dat/include/* \
		CodeC6678/yuv2dat/src/* \
	CodeC6678/modelPreesm.cfg

# Apply changes from Memory tutorials
log "Apply Memory tutorials (basic and advanced)"
git am --signoff -k $STEPBYSTEPDIR/tutoMemory.patch

# Test preesm workflow
log "Test Preesm workflow after Memory tutorials"
$RUNSCRIPTS/preesm_execute_workflow.sh $RUNTIMEWORKSPACE $ECLIPSERUN org.ietr.preesm.sobel Codegen.workflow 4core.scenario

# Apply Instrumented Codegen tutorials
log "Apply Instrumented Codegen tutorials"
git am --signoff -k $STEPBYSTEPDIR/tutoInstruCodegen.patch

# Test preesm workflow
log "Test Preesm workflow after Instrumented Codegen tutorial"
$RUNSCRIPTS/preesm_execute_workflow.sh $RUNTIMEWORKSPACE $ECLIPSERUN org.ietr.preesm.sobel InstrumentedCodegen.workflow 1core.scenario