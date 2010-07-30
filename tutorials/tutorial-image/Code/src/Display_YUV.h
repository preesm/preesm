#ifdef WIN32
#include <windows.h>
#include <process.h>
#include <vfw.h>
#include <memory.h>

typedef struct
{
   HANDLE            hThread ;
   HANDLE            hEvent ;
   HWND              hWnd ;
   MSG               msg ;
   WNDCLASS          wc ;
   HDRAWDIB          hDrawDib ;
   HDC               hDC ;
   BITMAPINFOHEADER  biHeader ;
   char              lpszAppName [15];
   DWORD             dwThreadID ;
   BOOL              imageIsReady ;
   unsigned char     *bufRGB ;
   RECT              rect ;
   unsigned char     *src [3];
   int               width, height ;
   int               zoom, oldzoom ;
   int               windowDismissed ;
}  T_VDWINDOW_YUV ;

#define VIDEO_BEGIN (WM_USER + 0)
#define VIDEO_DRAW_FRAME (WM_USER + 1)
#define VIDEO_REDRAW_FRAME (WM_USER + 2)
#define VIDEO_END (WM_USER + 3)
void           Display_YUV_init (int Number, int xsize, int ysize) ;
int            initDisplay_YUV (int Number, int pels, int lines) ;
int            InitDisplayWindowThread_YUV (int Number, int width, int height) ;
void           DisplayWinMain_YUV (int Number) ;
void           init_dither_tab () ;

/* To display a picture */
void           Display_YUV (int Number, unsigned char *Y, unsigned char *Cb
   , unsigned char *Cr) ;
int            displayImage_YUV (int Number, unsigned char *lum
   , unsigned char *Cb, unsigned char *Cr) ;
LONG APIENTRY  MainWndProc_YUV(HWND, UINT, UINT, LONG);
int            DrawDIB_YUV (int Number) ;
void           ConvertYUVtoRGB (unsigned char *src0, unsigned char *src1
   , unsigned char *src2, unsigned char *dst_ori, int width, int height) ;

/* close the Display */
void           Display_YUV_end (int Number) ;
int            closeDisplay_YUV (int Number) ;
#endif
