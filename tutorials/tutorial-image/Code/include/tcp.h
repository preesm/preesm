/*
	============================================================================
	Name        : tcp.h
	Author      : mpelcat
	Version     :
	Copyright   : TCP/IP communication using Winsock 1.1
	Description :
	============================================================================
*/

#ifndef _TCP_LIB
#define _TCP_LIB

#include <stdio.h>

#ifdef POCKET_PC
	#define _INC_WINDOWS
#endif

#ifdef WIN_TCP
	#include <winsock2.h>
	#include <stdlib.h>
	#include <string.h>
#else 
	#include <sys/socket.h>
	#include <netinet/in.h>
	#include <arpa/inet.h>
	#include <sys/time.h>
	#include <netdb.h>
	#define Sleep sleep
	#define closesocket close
	#define INVALID_SOCKET -1
	#define SOCKET_ERROR -1
	typedef int SOCKET ;

	char *WSAGetLastError ()
	{
	  return ("Unsupported");
	}
#endif


typedef struct
{
	unsigned int    socket ;
	unsigned short  port ;
} Media_TCP ;

int           begin_socklib (void) ;
void          end_socklib (void) ;
void          Receive_TCP (Media_TCP MediaName, unsigned int *Buffer, const int NB_bytes) ;
void          Send_TCP (Media_TCP MediaName, unsigned int *Buffer, const int NB_bytes) ;
unsigned int  init_TCP_server (Media_TCP MediaName) ;
unsigned int  init_TCP_client ( Media_TCP MediaName, char *processor_name );

#endif /* #ifndef _TCP_LIB*/

/* End of file PC_x86_TCP.h */
