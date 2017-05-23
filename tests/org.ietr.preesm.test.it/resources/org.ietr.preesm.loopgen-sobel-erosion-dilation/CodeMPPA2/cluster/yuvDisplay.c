
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "yuvDisplay.h"
#include "preesm.h"


static int fd_out __attribute__((unused));
static int nb_frame __attribute__((unused))= 0;

/**
* Initializes a display frame. Be careful, once a window size has been chosen,
* all videos must share the same window size
*
* @param id display unique identifier
* @param width
* @param height
*/
void yuvDisplayInit (int id, int width, int height)
{
	//System_printf("yuvDisplayInit\n");
#ifndef __nodeos__
#ifndef DISABLE_IO
#ifdef VERBOSE
	fd_out = open("mppa_output.yuv", O_CREAT | O_WRONLY, S_IRUSR | S_IWUSR);
#endif
#endif
#endif
}
/**
* Display one YUV frame
*
* @param id display unique identifier
* @param y luma
* @param u chroma U
* @param v chroma V
*/
void yuvDisplay(int id, unsigned char *y, unsigned char *u, unsigned char *v)
{
#ifndef __nodeos__
#ifndef DISABLE_IO
#define Y_SIZE (VIDEO_WIDTH*VIDEO_HEIGHT)
#define UV_SIZE (Y_SIZE/4)
	//System_printf("yuvDisplay\n");
#ifdef VERBOSE
	write(fd_out, y, Y_SIZE);
	write(fd_out, u, UV_SIZE);
	write(fd_out, v, UV_SIZE);
	nb_frame++;
	if(nb_frame >= GRAPH_ITERATION)
		close(fd_out);
#else
	write(1, y, Y_SIZE);  /* pipe to sdtout */
	write(1, u, UV_SIZE); /* pipe to sdtout */
	write(1, v, UV_SIZE); /* pipe to sdtout */
#endif
#endif
#endif
}
void yuvRefreshDisplay(int id)
{
}

void yuvFinalize(int id)
{

}
