

#include "readYUV.h"
#include <xdc/runtime/System.h>
#include "akiyo.h"
#include <string.h>
#include <xdc/runtime/Timestamp.h>
#include <xdc/runtime/Types.h>


/*========================================================================

   Global Variable

   ======================================================================*/

/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int xSize, int ySize) {
	//System_printf("initYUV\n");
}

/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int xSize, int ySize, unsigned char *y, unsigned char *u, unsigned char *v) {

	static int i = 0;
	static unsigned int time = 0;
	unsigned int now;

	if(i==0){
	now = Timestamp_get32();
	unsigned int delta = (now-time)/500;
	float fps = 1000000000.0 / (float)delta;
	System_printf("fps: %f\n", fps);
	time = Timestamp_get32();
	}
	i = (i +1) %500;
	memcpy(y,picture_tab,288*352*sizeof(char));
	memset(u,0,352*288*sizeof(char)/4);
	memset(v,0,352*288*sizeof(char)/4);
}
