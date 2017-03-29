/*******************************************************************************
 * Copyright or © or Copr. 2009 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2009 - 2010)
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
#ifndef _TCP_LIB
#define _TCP_LIB
/*========================================================================
   
   FUNCTION: 
      TCP/IP
   
   COMPILATION:
      needs a library "ws2_32.lib" 
   
   ======================================================================*/
/*************************/
/* TCP using Winsock 1.1 */
/*************************/
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
