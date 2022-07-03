

#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <assert.h>

#include "yuvRead.h"
#include "preesm.h"
#include "clock.h"

extern int preesmStopThreads;
#define FPS_INTERVAL 1
int currentFrameIndex __attribute__((unused));

/*========================================================================

   Global Variable

   ======================================================================*/
static int fd __attribute__((unused));

/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int width, int height) {
  (void) width;
  (void) height;
#ifndef __nodeos__
#ifndef DISABLE_IO
#ifdef GENERATE_FILE
  fd = open(PATH_VIDEO, O_RDONLY);
  assert(fd >= 0);
#endif
  startTiming(0);
#ifdef PREESM_VERBOSE
    printf("Opened file '%s'\n", PATH_VIDEO);
#endif
#endif
#endif
}

int counter = 0;
/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int width, int height, unsigned char *y, unsigned char *u, unsigned char *v) {
#ifndef __nodeos__
#ifndef DISABLE_IO  
  if(counter == FPS_INTERVAL){
    unsigned int time = 0;
    time = stopTiming(0);
    printf("\nMain: %d frames in %d us - %f fps\n", FPS_INTERVAL, time, FPS_INTERVAL / (float)time * 1000000);
    startTiming(0);
    counter = 0;
  }
#ifdef GENERATE_FILE
  if(read(fd, y, sizeof(char) * width * height) < 0){
    assert(-1 && "read\n");
  }
  if(read(fd, u, sizeof(char) * width * height / 4) < 0){
    assert(-1 && "read\n");
  }
  if(read(fd, v, sizeof(char) * width * height / 4) < 0){
    assert(-1 && "read\n");
  }
#endif
  counter++;
#endif
#endif
}
