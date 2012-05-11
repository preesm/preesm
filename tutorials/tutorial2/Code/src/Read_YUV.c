/*========================================================================
   
   FUNCTION: 
      is able to open an YUV sequences
   
   COMPILATION:
      needs "winmm.lib" (because of "timeGetTime ()" )
   
   ======================================================================*/
#include <stdio.h>
#include <stdlib.h>

/*========================================================================
   
   Extern Function declaration
   
   ======================================================================*/
/*========================================================================
   
   Local Function declaration
   
   ======================================================================*/
static int  Filesize (FILE *f) ;
static void WaitForNextDisplayMoment (void) ;
/*========================================================================
    
   Global Variable
    
   ======================================================================*/
static FILE *ptfile ;
static int  xsize ;
static int  ysize ;
static int  numberofframes = 0 ;
static int  images = 0 ;
static int  FrameRateInMilliseconds ;
static int  now, after ;
static int  time_0 = 0, time_1 = 0 ;

/*========================================================================
    
   Local Constant
    
   ======================================================================*/
/*========================================================================
    
   Read_YUV_init DEFINITION
    
   ======================================================================*/
void Read_YUV_init ( char *NOM_FICH, int XSIZE, int YSIZE, int NB_FRAMES )
{
   if ( (ptfile = fopen(NOM_FICH, "rb")) == NULL ) 
   {
      printf("Cannot open yuv_file concatenated input file '%s' for reading\n"
         , NOM_FICH);
      exit( -1);
   }
   if ( (xsize = XSIZE) == 0 ) 
   {
      printf("xsize %s invalid\n", XSIZE);
      exit( -2);
   }
   if ( (ysize = YSIZE) == 0 ) 
   {
      printf("ysize %s invalid\n", YSIZE);
      exit( -3);
   }
   if ( NB_FRAMES == 0 ) 
   {
      printf("framerate %s invalid\n", NB_FRAMES);
      exit( -5);
   }
   FrameRateInMilliseconds = 1000 / NB_FRAMES ;
   numberofframes = Filesize(ptfile) / (xsize * ysize + xsize * ysize / 2);
}

/*========================================================================
    
   Read_YUV DEFINITION
    
   ======================================================================*/
void Read_YUV ( unsigned char *Y, unsigned char *U, unsigned char *V )
{
   /***********************/
   /*  Declare variables  */
   /***********************/
   /***********************/
   /* Function's Body     */
   /***********************/
   //after = timeGetTime();
   WaitForNextDisplayMoment();
   //now = timeGetTime();
   fread(Y, sizeof(unsigned char), xsize * ysize, ptfile);
   fread(U, sizeof(unsigned char), xsize * ysize / 4, ptfile);
   fread(V, sizeof(unsigned char), xsize * ysize / 4, ptfile);
   images++ ;
   if ( images == numberofframes ) 
   {
      //time_1 = timeGetTime();
      printf("numberofframes %d temps %d\n", numberofframes, time_1 - time_0);
      //time_0 = timeGetTime();
      fseek(ptfile, 0, SEEK_SET);
      images = 0 ;
   }
}

/*========================================================================
    
   WaitForNextDisplayMoment DEFINITION
    
   ======================================================================*/
void WaitForNextDisplayMoment ( void )
{
   int   nextDisplayMoment = now + FrameRateInMilliseconds ;
   int   MillisecondsToWait = nextDisplayMoment - after ;
   
   if ( MillisecondsToWait < 0 ) 
      return ;
   
   // Sleep(MillisecondsToWait);
}

/*========================================================================
    
   Filesize DEFINITION
    
   ======================================================================*/
int Filesize ( FILE *f )
{
   long  oldfp, fsize ;
   
   oldfp = ftell(f);
   if ( oldfp < 0L ) 
      return -1 ;
   if ( 0 != fseek(f, 0, SEEK_END) ) 
      return -1 ;
   fsize = ftell(f);
   if ( fsize < 0 ) 
      return -1 ;
   if ( 0 != fseek(f, oldfp, SEEK_SET) ) 
      return -1 ;
   return fsize ;
}
