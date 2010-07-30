/*========================================================================
   
   FUNCTION: 
      Display a sequences
   
   COMPILATION:
      needs a library "vfw32.lib" 
   
   ======================================================================*/
#ifdef WIN32
#include "Display_YUV.h"
#include <stdio.h>

/*========================================================================
   
   Extern Function declaration
   
   ======================================================================*/
/*========================================================================
   
   Local Function declaration
   
   ======================================================================*/
/*========================================================================
    
   Global Variable
    
   ======================================================================*/
/* Needs to be define in the main as a Global Variable */
extern int              NumberofWindows_YUV ;
extern T_VDWINDOW_YUV   Window_YUV [];
unsigned char           *clp = NULL ;

/*========================================================================
    
   Local Constant
    
   ======================================================================*/
/*========================================================================
    
   How to initialize a Display Window
    
   ======================================================================*/
void Display_YUV_init ( int Number, int xsize, int ysize )
{
   int   i ;
   
   if ( !(clp = (unsigned char *)malloc(1024)) ) 
   {
      printf("malloc for clp failed\n");
      exit( -6);
   }
   clp += 384 ;
   for ( i = -384 ; i < 640 ; i++ ) 
      clp [i] = i < 0 ? 0 : (i > 255 ? 255 : i);
   initDisplay_YUV(Number, xsize, ysize);
   
   // Sleep for a second to give the display thread a chance to initialize and to
   // prevent a deadlock
   Sleep(1000);
}

int initDisplay_YUV ( int Number, int pels, int lines )
{
   int   errFlag = 0 ;
   
   init_dither_tab();
   errFlag |= InitDisplayWindowThread_YUV(Number, pels, lines);
   return errFlag ;
}

int InitDisplayWindowThread_YUV ( int Number, int width, int height )
{
   int   errFlag = 0 ;
   
   // ow modify the couple that need it
   Window_YUV [Number].width = width ;
   Window_YUV [Number].height = height ;
   Window_YUV [Number].biHeader.biWidth = Window_YUV [Number].width ;
   Window_YUV [Number].biHeader.biHeight = Window_YUV [Number].height ;
   Window_YUV [Number].biHeader.biSize = sizeof(BITMAPINFOHEADER);
   Window_YUV [Number].biHeader.biCompression = BI_RGB ;
   Window_YUV [Number].biHeader.biPlanes = 1 ;
   Window_YUV [Number].biHeader.biBitCount = 24 ;
   Window_YUV [Number].biHeader.biSizeImage
      = 3 * Window_YUV [Number].width * Window_YUV [Number].height ;
   Window_YUV [Number].imageIsReady = FALSE ;
   
   // Allocate the memory needed to hold the RGB 
   //and visualization information
   Window_YUV [Number].bufRGB
      = (unsigned char *)malloc(
         3 * Window_YUV [Number].width * Window_YUV [Number].height);
   
   // Create synchronization event
   Window_YUV [Number].hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
   Window_YUV [Number].hThread
      = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DisplayWinMain_YUV
         , (LPVOID)Number, 0, &Window_YUV [Number].dwThreadID);
   if ( Window_YUV [Number].hThread == NULL ) 
   {
      errFlag = 1 ;
      return errFlag ;
   }
   return errFlag ;
}

void DisplayWinMain_YUV ( int Number )
{
   int   errFlag = 0 ;
   DWORD dwStyle ;
   char  c [1];
   
   itoa(Number, c, 32);
   Window_YUV [Number].wc.style = CS_BYTEALIGNWINDOW ;
   Window_YUV [Number].wc.lpfnWndProc = MainWndProc_YUV ;
   Window_YUV [Number].wc.cbClsExtra = 0 ;
   Window_YUV [Number].wc.cbWndExtra = 0 ;
   Window_YUV [Number].wc.hInstance = 0 ;
   Window_YUV [Number].wc.hIcon = LoadIcon(NULL, IDI_APPLICATION);
   Window_YUV [Number].wc.hCursor = LoadCursor(NULL, IDC_ARROW);
   Window_YUV [Number].wc.hbrBackground = GetStockObject(WHITE_BRUSH);
   Window_YUV [Number].wc.lpszMenuName = NULL ;
   Window_YUV [Number].zoom = 1 ;
   strcpy(Window_YUV [Number].lpszAppName, "YUV Display ");
   strcat(Window_YUV [Number].lpszAppName, c);
   Window_YUV [Number].wc.lpszClassName = Window_YUV [Number].lpszAppName ;
   RegisterClass(&Window_YUV [Number].wc);
   dwStyle = WS_DLGFRAME | WS_SYSMENU | WS_MINIMIZEBOX | WS_MAXIMIZEBOX ;
   Window_YUV [Number].hWnd
      = CreateWindow(Window_YUV [Number].lpszAppName
         , Window_YUV [Number].lpszAppName, dwStyle, CW_USEDEFAULT
         , CW_USEDEFAULT, Window_YUV [Number].width + 6
         , Window_YUV [Number].height + 25, NULL, NULL, 0, NULL);
   if ( Window_YUV [Number].hWnd == NULL ) 
      ExitThread(errFlag = 1);
   ShowWindow(Window_YUV [Number].hWnd, SW_SHOWNOACTIVATE);
   UpdateWindow(Window_YUV [Number].hWnd);
   
   // Message loop for display window's thread  
   while ( GetMessage(&Window_YUV [Number].msg, NULL, 0, 0) ) 
   {
      TranslateMessage(&Window_YUV [Number].msg);
      DispatchMessage(&Window_YUV [Number].msg);
   }
   ExitThread(0);
}

/*========================================================================
    
   How to Display a picture
    
   ======================================================================*/
void Display_YUV ( int Number, unsigned char *Y, unsigned char *Cb
   , unsigned char *Cr )
{
   displayImage_YUV(Number, Y, Cb, Cr);
}

LRESULT APIENTRY MainWndProc_YUV ( HWND hWnd, UINT msg, UINT wParam, LONG lParam )
{
   LPMINMAXINFO   lpmmi ;
   int            i ;
   int            Number = 0 ;
   
   for ( i = 0 ; i < NumberofWindows_YUV ; i++ ) 
   {
      if ( hWnd == Window_YUV [i].hWnd ) 
      {
         Number = i ;
         break ;
      }
   }
   switch ( msg ) 
   {
      case VIDEO_BEGIN : 
         Window_YUV [Number].hDC = GetDC(Window_YUV [Number].hWnd);
         Window_YUV [Number].hDrawDib = DrawDibOpen();
         Window_YUV [Number].zoom = 1 ;
         Window_YUV [Number].oldzoom = 0 ;
         DrawDibBegin(Window_YUV [Number].hDrawDib, Window_YUV [Number].hDC
            , 2 * Window_YUV [Number].width, 2 * Window_YUV [Number].height
            , &Window_YUV [Number].biHeader, Window_YUV [Number].width
            , Window_YUV [Number].height, 0);
         
         // SetEvent (Window_YUV[Number].hEvent);
         Window_YUV [Number].windowDismissed = FALSE ;
         ReleaseDC(Window_YUV [Number].hWnd, Window_YUV [Number].hDC);
         break ;
      case VIDEO_DRAW_FRAME : 
         Window_YUV [Number].hDC = GetDC(Window_YUV [Number].hWnd);
         ConvertYUVtoRGB(Window_YUV [Number].src [0]
            , Window_YUV [Number].src [1], Window_YUV [Number].src [2]
            , Window_YUV [Number].bufRGB, Window_YUV [Number].width
            , Window_YUV [Number].height);
         
         // Draw the picture onto the screen
         DrawDIB_YUV(Number);
         SetEvent(Window_YUV [Number].hEvent);
         ReleaseDC(Window_YUV [Number].hWnd, Window_YUV [Number].hDC);
         break ;
      case VIDEO_END : 
         //Window_YUV[Number] has been closed.  
         //The following lines handle the cleanup.
         Window_YUV [Number].hDC = GetDC(Window_YUV [Number].hWnd);
         DrawDibEnd(Window_YUV [Number].hDrawDib);
         DrawDibClose(Window_YUV [Number].hDrawDib);
         ReleaseDC(Window_YUV [Number].hWnd, Window_YUV [Number].hDC);
         Window_YUV [Number].windowDismissed = TRUE ;
         PostQuitMessage(0);
         break ;
      case WM_CREATE : 
         PostMessage(hWnd, VIDEO_BEGIN, 0, 0);
         break ;
      case WM_SIZE : 
         switch ( wParam ) 
         {
            case SIZE_MAXIMIZED : 
               Window_YUV [Number].zoom = 2 ;
               break ;
            case SIZE_MINIMIZED : 
               Window_YUV [Number].oldzoom = Window_YUV [Number].zoom ;
               break ;
            case SIZE_RESTORED : 
               if ( Window_YUV [Number].oldzoom ) 
               {
                  Window_YUV [Number].zoom = Window_YUV [Number].oldzoom ;
                  Window_YUV [Number].oldzoom = 0 ;
               }
               else 
                  Window_YUV [Number].zoom = 1 ;
               break ;
            case SIZE_MAXHIDE : 
               break ;
            case SIZE_MAXSHOW : 
               break ;
         }
         PostMessage(hWnd, WM_PAINT, 0, 0);
         break ;
      case WM_GETMINMAXINFO : 
         lpmmi = (LPMINMAXINFO)lParam ;
         GetWindowRect(hWnd, &Window_YUV [Number].rect);
         lpmmi -> ptMaxPosition.x = Window_YUV [Number].rect.left ;
         lpmmi -> ptMaxPosition.y = Window_YUV [Number].rect.top ;
         lpmmi -> ptMaxSize.x = 2 * Window_YUV [Number].width + 6 ;
         lpmmi -> ptMaxSize.y = 2 * Window_YUV [Number].height + 25 ;
         break ;
      case WM_DESTROY : 
         
         // Window has been closed.  
         //The following lines handle the cleanup.
         DrawDibEnd(Window_YUV [Number].hDrawDib);
         ReleaseDC(Window_YUV [Number].hWnd, Window_YUV [Number].hDC);
         DrawDibClose(Window_YUV [Number].hDrawDib);
         Window_YUV [Number].windowDismissed = TRUE ;
         PostQuitMessage(0);
         break ;
      case WM_PAINT : 
         if ( Window_YUV [Number].imageIsReady ) 
         {
            Window_YUV [Number].hDC = GetDC(Window_YUV [Number].hWnd);
            DrawDIB_YUV(Number);
            ReleaseDC(Window_YUV [Number].hWnd, Window_YUV [Number].hDC);
         }
         break ;
   }
   return DefWindowProc(hWnd, msg, wParam, lParam);
}

int displayImage_YUV ( int Number, unsigned char *lum, unsigned char *Cb
   , unsigned char *Cr )
{
   int   errFlag = 0 ;
   DWORD dwRetVal ;
   
   // Wait until we have finished drawing the last frame
   if ( Window_YUV [Number].windowDismissed == FALSE ) 
   {
      Window_YUV [Number].src [0] = lum ;
      Window_YUV [Number].src [1] = Cb ;
      Window_YUV [Number].src [2] = Cr ;
      Window_YUV [Number].imageIsReady = TRUE ;
      
      // Post message to drawing thread's window to draw frame
      PostMessage(Window_YUV [Number].hWnd, VIDEO_DRAW_FRAME, (WPARAM)NULL
         , (LPARAM)NULL);
      
      // Wait until the frame has been drawn
      dwRetVal = WaitForSingleObject(Window_YUV [Number].hEvent, INFINITE);
   }
   return errFlag ;
}

int DrawDIB_YUV ( int Number )
{
   int   errFlag = 0 ;
   
   errFlag
      |= DrawDibDraw(Window_YUV [Number].hDrawDib, Window_YUV [Number].hDC, 0
         , 0, Window_YUV [Number].zoom * Window_YUV [Number].width
         , Window_YUV [Number].zoom * Window_YUV [Number].height
         , &Window_YUV [Number].biHeader, Window_YUV [Number].bufRGB, 0, 0
         , Window_YUV [Number].width, Window_YUV [Number].height, DDF_SAME_DRAW);
   return errFlag ;
}

/*========================================================================
    
   How to close a Display Window
    
   ======================================================================*/
void Display_YUV_end ( int Number )
{
   closeDisplay_YUV(Number);
}

int closeDisplay_YUV ( int Number )
{
   int   errFlag = 0 ;
   
   if ( Window_YUV [Number].hWnd ) 
   {
      PostMessage(Window_YUV [Number].hWnd, VIDEO_END, (WPARAM)NULL
         , (LPARAM)NULL);
      while ( Window_YUV [Number].windowDismissed == FALSE ) 
         ;
   }
   if ( Window_YUV [Number].hEvent ) 
      CloseHandle(Window_YUV [Number].hEvent);
   if ( Window_YUV [Number].hThread ) 
      CloseHandle(Window_YUV [Number].hThread);
   free(Window_YUV [Number].bufRGB);
   return errFlag ;
}
#endif
