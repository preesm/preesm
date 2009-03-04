/*========================================================================
  
         FUNCTION: 
                   NAMED PIPE
   
         COMPILATION:
                   needs library "kernel32.lib" 
   
=========================================================================*/
#ifdef PIPE
#include <windows.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

HANDLE init_PIPE_server( LPTSTR pipeName )
{   
  int fConnected;
  HANDLE hPipe = CreateNamedPipe( pipeName,
				  PIPE_ACCESS_DUPLEX, 
				  PIPE_TYPE_MESSAGE | PIPE_READMODE_MESSAGE | PIPE_WAIT, PIPE_UNLIMITED_INSTANCES, 
				  128, 
				  128,
				  NMPWAIT_USE_DEFAULT_WAIT, 
				  NULL);
  
  if ( hPipe == INVALID_HANDLE_VALUE ) 
    {	
      printf("CreatePipe failed\n\n");
      return 0;
    }
  else
    {
      printf("wait for client process\n\n");
    }
  fConnected = ConnectNamedPipe(hPipe, NULL) ? TRUE : (GetLastError() == ERROR_PIPE_CONNECTED); 
  if ( fConnected )
    {
      printf("server is connected to the pipe\n\n");
    }
  return hPipe;
}


HANDLE init_PIPE_client( LPTSTR pipeName )
{
  HANDLE hPipe; 
  int status;
  do{
    status = WaitNamedPipe( pipeName, NMPWAIT_USE_DEFAULT_WAIT );
    printf("wait for server process\n\n");
    Sleep(1000);
  }while(status == 0);
  printf("client is connected to the pipe\n\n");
  hPipe = CreateFile( pipeName,
		      GENERIC_READ | GENERIC_WRITE ,
		      0 ,
		      NULL ,
		      OPEN_EXISTING ,
		      FILE_ATTRIBUTE_NORMAL ,
		      NULL ); 
  return hPipe;
}


void Read_PIPE(HANDLE pipe, unsigned int *Buffer, const int NB_bytes, DWORD positionInsidePipe )
{
  ReadFile( pipe, (char*) Buffer, NB_bytes, &positionInsidePipe, NULL );
}


void Write_PIPE(HANDLE pipe, unsigned int *Buffer, const int NB_bytes, DWORD positionInsidePipe )
{
  WriteFile( pipe, (char*) Buffer, NB_bytes, &positionInsidePipe, NULL );
}


#endif