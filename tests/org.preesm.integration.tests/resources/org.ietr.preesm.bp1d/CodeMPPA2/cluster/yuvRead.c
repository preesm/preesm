

#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "yuvRead.h"
#include "preesm.h"

int currentFrameIndex __attribute__((unused));

/*========================================================================

   Global Variable

   ======================================================================*/
static int fd __attribute__((unused));

/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int width, int height) {
#ifndef __nodeos__
#ifndef DISABLE_IO
	fd = open(PATH, O_RDONLY);
#ifdef VERBOSE
    printf("Opened file '%s'\n", PATH);
#endif
#endif
#endif
}

/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int width, int height, unsigned char *y, unsigned char *u, unsigned char *v) {
#ifndef __nodeos__
#ifndef DISABLE_IO
	read(fd, y, sizeof(char) * width * height);
	read(fd, u, sizeof(char) * width * height / 4);
	read(fd, v, sizeof(char) * width * height / 4);
#endif
#endif
}
