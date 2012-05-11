/*

Prototypes of the functions called by Preesm

*/

#ifndef PREESM_PROTOTYPES
#define PREESM_PROTOTYPES

//#include "fonc_encoder.h"

// Init phase
void Read_YUV_init ( char *NOM_FICH, int XSIZE, int YSIZE, int NB_FRAMES );
void Display_YUV_init ( int Number, int xsize, int ysize );

// Loop phase
void noCall();
void Read_YUV ( unsigned char *Y, unsigned char *U, unsigned char *V );
void Display_YUV ( int Number, unsigned char *Y, unsigned char *Cb
   , unsigned char *Cr );

// End phase

#endif

