/*

Functions enabling the use of Preesm calls with special arguments (argc, argv for example)

*/
#include "yuvDisplay.h"
#include "preesmPrototypes.h"


// Ignored function
void noCall(){
}


void Read_YUV_init_preesm(int xsize, int ysize){
	Read_YUV_init("D:/IETR/Sequences/cif/akiyo/akiyo_cif.yuv",xsize, ysize,25);
}

