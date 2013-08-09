

#include "readYUV.h"
#include <xdc/runtime/System.h>
#include "akiyo.h"
#include <string.h>


/*========================================================================

   Global Variable

   ======================================================================*/

/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int xSize, int ySize) {
	System_printf("initYUV\n");
}

/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int xSize, int ySize, unsigned char *y, unsigned char *u, unsigned char *v) {
	System_printf("readYUV\n");
	memcpy(y,picture_tab,288*352*sizeof(char));
	memset(u,0,352*288*sizeof(char)/4);
	memset(v,0,352*288*sizeof(char)/4);
}
