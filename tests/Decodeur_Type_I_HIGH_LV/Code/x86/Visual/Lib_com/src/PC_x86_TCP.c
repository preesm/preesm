/*******************************************************************************
 * Copyright or © or Copr. 2010 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2010)
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
/***********************************************************************************\
 *                                                                                 
 *		This file contains the different fonctions allowing to send, to receive,
 *		to initialize a TCP communication
 *                                                                                 
 *                                                                                 
 *		Cible faraday TMS320TCI6487 DSP Platform                                     
 *                                                                                 
\***********************************************************************************/


#include "../include/PC_X86_TCP.h"


int begin_socklib ( void )
{
#ifdef WIN_TCP

	WSADATA wsaData ;
	if ( WSAStartup(0x202, &wsaData) == SOCKET_ERROR ) 
	{
	return( -1);
	}
#endif
	return(0);
}

void end_socklib ( void )
{
#ifdef WIN_TCP
	WSACleanup();
#endif
}

unsigned int init_TCP_server ( Media_TCP MediaName )
{
	struct	sockaddr_in local, from ;
	SOCKET  listen_socket, msgsock ;
	int     fromlen ;
	int     socket_type = SOCK_STREAM ; // TCP

	if ( begin_socklib() == SOCKET_ERROR ) 
	{
		fprintf(stderr, "WSAStartup failed with error %d\n", WSAGetLastError());
		end_socklib();
		exit( -12);
	}

	local.sin_family = AF_INET ;
	local.sin_addr.s_addr = INADDR_ANY ;

	/* 
	 * Port MUST be in Network Byte Order
	 */
	local.sin_port = htons(MediaName.port);
	listen_socket = socket(AF_INET, socket_type, 0); // TCP socket
	if ( listen_socket == INVALID_SOCKET ) 
	{
		fprintf(stderr, "socket() failed with error %d\n", WSAGetLastError());
		end_socklib();
		exit( -6);
	}
	//
	// bind() associates a local address and port combination with the
	// socket just created. This is most useful when the application is a 
	// server that has a well-known port that clients know about in advance.
	//
	if ( bind(listen_socket, (struct sockaddr *)&local, sizeof(local)) == SOCKET_ERROR ) 
	{
		fprintf(stderr, "bind() failed with error %d\n", WSAGetLastError());
		end_socklib();
		exit( -7);
	}

	if ( listen(listen_socket, 5) == SOCKET_ERROR ) 
	{
		fprintf(stderr, "listen() failed with error %d\n", WSAGetLastError());
		end_socklib();
		exit( -8);
	}

	fromlen = sizeof(from);
	printf(" 'Listening' on port %d, protocol %s\n", MediaName.port, "TCP");
	msgsock = accept(listen_socket, (struct sockaddr *)&from, &fromlen);

	if ( msgsock == INVALID_SOCKET ) 
	{
		fprintf(stderr, "accept() error %d\n", WSAGetLastError());
		end_socklib();
		exit( -9);
	}

	printf("accepted connection from %s, port %d\n", inet_ntoa(from.sin_addr)  , htons(from.sin_port));
	return ((int)msgsock);
}

unsigned int init_TCP_client ( Media_TCP MediaName, char *processor_name )
{
	char              *server_name = NULL ;
	unsigned int      addr ;
	int               socket_type = SOCK_STREAM ;
	struct sockaddr_in server ;
	struct hostent     *hp ;
	SOCKET            conn_socket ;
	int status;
	server_name = processor_name ;
	if ( begin_socklib() == SOCKET_ERROR ) 
	{
		fprintf(stderr, "WSAStartup failed with error %d\n", WSAGetLastError());
		end_socklib();
		return -1 ;
	}
	//
	// Attempt to detect if we should call gethostbyname() or
	// gethostbyaddr()
	/*if ( isalpha(server_name [0]) ) 
	/* server address is a name */
		hp = gethostbyname(server_name);
	/*else 
	{

		/* Convert nnn.nnn address to a usable one 
		addr = inet_addr(server_name);
		hp = gethostbyaddr((char *)&addr, 4, AF_INET);
	}*/
	if ( hp == NULL ) 
	{
		fprintf(stderr, "Client: Cannot resolve address [%s]: Error %d\n" , server_name, WSAGetLastError());
		end_socklib();
		exit(-10);
	}
	//
	// Copy the resolved information into the sockaddr_in structure
	//
	memset(&server, 0, sizeof(server));
	memcpy(&server.sin_addr, hp -> h_addr, hp -> h_length);
	server.sin_family = hp -> h_addrtype ;
	server.sin_port = htons(MediaName.port);
	conn_socket = socket(AF_INET, socket_type, 0); /* Open a socket */ 
	if ( conn_socket < 0 ) 
	{
		fprintf(stderr, "Client: Error Opening socket: Error %d\n" , WSAGetLastError());
		end_socklib();
		exit(-11) ;
	}


	printf("Client connecting to: %s\n", hp -> h_name);
	status=connect(conn_socket, (struct sockaddr *)&server, sizeof(server));
	while ( (status)== SOCKET_ERROR ) 
	{
		fprintf(stderr, "connect() failed: %d\n", WSAGetLastError());
		//WSACleanup();
		closesocket(conn_socket);

		conn_socket = socket(AF_INET, socket_type, 0);
		Sleep(100);
		printf("Client connecting to: %s\n", hp -> h_name);
		status=connect(conn_socket, (struct sockaddr *)&server, sizeof(server));
		//return -1 ;
	}
	return ((int)conn_socket);
}

void Receive_TCP ( Media_TCP MediaName, unsigned int *Buffer, const int NB_bytes )
{
	int retval = 0 ;

	while ( retval != NB_bytes ) 
	{
		retval += recv(MediaName.socket, (char *)Buffer + retval, NB_bytes - retval , 0);
		//printf("Received %d bytes, data from client\n",retval);
	}
}

void Send_TCP ( Media_TCP MediaName, unsigned int *Buffer, const int NB_bytes )
{
	int retval ;

	retval = send(MediaName.socket, (char *)Buffer, NB_bytes, 0);

	//printf("Sent Data %d\n",retval);
}
