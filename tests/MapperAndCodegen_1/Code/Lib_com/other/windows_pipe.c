/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
/*========================================================================

         FUNCTION:
                   NAMED PIPE

         COMPILATION:
                   needs library "kernel32.lib"

=========================================================================*/
#include <windows.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

HANDLE init_PIPE_server( LPTSTR pipeName )
{
  int fConnected;
  /*HANDLE hPipe = CreateNamedPipe( pipeName,
				  PIPE_ACCESS_DUPLEX,
				  PIPE_TYPE_MESSAGE | PIPE_READMODE_MESSAGE | PIPE_WAIT, PIPE_UNLIMITED_INSTANCES,
				  10000,
				  10000,
				  NMPWAIT_USE_DEFAULT_WAIT,
				  NULL);*/


  HANDLE hPipe = CreateNamedPipe ( pipeName,
                                          PIPE_ACCESS_DUPLEX, // read/write access
                                          PIPE_TYPE_MESSAGE | // message type pipe
                                          PIPE_READMODE_MESSAGE | // message-read mode
                                          PIPE_WAIT, // blocking mode
                                          PIPE_UNLIMITED_INSTANCES, // max. instances
                                          100000, // output buffer size
                                          100000, // input buffer size
                                          100000, // client time-out
                                          NULL); // no security

  if ( hPipe == INVALID_HANDLE_VALUE )
    {
      printf("CreatePipe failed: %s\n\n",pipeName);
      return 0;
    }
  else
    {
      printf("wait for client process: %s\n\n",pipeName);
    }
  fConnected = ConnectNamedPipe(hPipe, NULL) ? TRUE : (GetLastError() == ERROR_PIPE_CONNECTED);
  if ( fConnected )
    {
      printf("server is connected to the pipe: %s\n\n",pipeName);
    }
  return hPipe;
}


HANDLE init_PIPE_client( LPTSTR pipeName )
{
  HANDLE hPipe;
  int status;
  do{
    status = WaitNamedPipe( pipeName, 100000 );
    printf("wait for server process: %s\n\n",pipeName);
    Sleep(1000);
  }while(status == 0);
  printf("client is connected to the pipe: %s\n\n",pipeName);
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

