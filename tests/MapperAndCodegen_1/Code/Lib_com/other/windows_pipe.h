/*========================================================================

         FUNCTION:
                   NAMED PIPE

         COMPILATION:
                   needs library "kernel32.lib"

=========================================================================*/

#ifndef WINDOWS_PIPE
#define WINDOWS_PIPE

HANDLE init_PIPE_server( LPTSTR pipeName );
HANDLE init_PIPE_client( LPTSTR pipeName );
void Read_PIPE(HANDLE pipe, unsigned int *Buffer, const int NB_bytes, DWORD positionInsidePipe );
void Write_PIPE(HANDLE pipe, unsigned int *Buffer, const int NB_bytes, DWORD positionInsidePipe );

#endif
