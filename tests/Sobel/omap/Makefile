#########################
### Globals Variables ###
#########################
PROGNAME=sobelPreesm

TYPE=EXE
#LIB or EXE

#####################
### Sources files ###
#####################
SRC=../Preesm/Code/src/SobelFilter.c

SRC_A9 =$(SRC) src/main_a9.c  \
	../Preesm/Code/CortexA9_1.c \
	../Preesm/Code/CortexA9_2.c \
	../Preesm/Code/src/SplitImage.c \
	../Preesm/Code/src/MergeImage.c \
	../Preesm/Code/src/SeparateY.c \
	../Preesm/Code/src/Display.c \
	../Preesm/Code/src/Camera.c

SRC_DSP=$(SRC) src/main_dsp.c ../Preesm/Code/Tesla.c 
SRC_SYS=$(SRC) src/main_sys.c ../Preesm/Code/SysM3.c 
SRC_APP=$(SRC) src/main_app.c ../Preesm/Code/AppM3.c 

LIBS=Com
LIBS_A9= -lLoad_arm -lSDL -lSDL_ttf -lfreetype -lz -lImageIO_arm
LIBS_DSP=
LIBS_SYS=
LIBS_APP=

FLAGS_A9= -DA9
FLAGS_DSP=
FLAGS_SYS=
FLAGS_APP=

include ../Makefile.global
