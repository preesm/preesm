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
/***********************************************************************************\
*                                                                                 
*    Contain: Library generic                                                                     
*                                                                                 
*                                                                                 
*    Cible: Multiplatform (DSP,PC,LINUX,Pocket_PC)                                    
*                                                                                 
\***********************************************************************************/



#ifdef _MVS // Code pour projet Microsoft Visual Studio
/* 
*	This part of the library contain the differents fonctions used by the generating of code.
*	cf. the x86.xslt apporiate.
*
*	These differents fonctions allow to switch the medium for use the right bus of communication
*	(TCP).
*	Each of them are composed of the 'switch case' to realised this swiching according to the Medium_Type.
*/
	#include "../include/PC_x86_TCP.h"
	#include "windows.h"
	#include "../include/OS_Com.h"

#define TCP_BASE_ADDRESS 49152
#define TCP_SND 0
#define TCP_RCV 1
#define TCP_BASE_ADDRESS 49152

#define MAX_CORE_NUMBER 16

Medium Media[MAX_CORE_NUMBER][MAX_CORE_NUMBER][2];

	
// Mutex to protect matlab coms
TCHAR comSendMutName[50] = TEXT("tcpSendMutex");
TCHAR comRcvMutName[50] = TEXT("tcpReceiveMutex");

/**
 Creating the mutex to protect matlab coms
*/
HANDLE createComMutex(LPCWSTR name)
{
    HANDLE hMutex = CreateMutex( 
        NULL,                        // default security descriptor
        FALSE,                       // mutex not owned
        name);  // object name

    if (hMutex == NULL) 
        printf("CreateMutex error: %d\n", GetLastError() ); 
    else 
        if ( GetLastError() == ERROR_ALREADY_EXISTS ) 
            printf("CreateMutex opened an existing mutex\n"); 
        else printf("CreateMutex created a new mutex.\n");

	return hMutex;
}


void Com_Init (int direction, int I_media_type, short SenderId, short ReceiverId)
{
/*
	HANDLE hMutex;
	
	if(direction == MEDIUM_SEND){
		hMutex = createComMutex(comSendMutName);
	}
	else{
		hMutex = createComMutex(comRcvMutName);
	}
	WaitForSingleObject(hMutex, INFINITE);
*/
	switch(direction){
		case MEDIUM_SEND:
			if(Media[SenderId][ReceiverId][TCP_SND].medium == NULL){
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));
				
				medium->port = TCP_BASE_ADDRESS + ((SenderId<<4) + (ReceiverId));
				medium->socket =  init_TCP_server (*medium);

				Media[SenderId][ReceiverId][TCP_SND].medium = medium;
			}
			break;

		case MEDIUM_RCV: 	
			if(Media[ReceiverId][SenderId][TCP_RCV].medium == NULL){		
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));

				medium->port = TCP_BASE_ADDRESS + ((SenderId<<4) + (ReceiverId));
				medium->socket =  init_TCP_client (*medium,"localhost");//"192.168.1.2"

				Media[ReceiverId][SenderId][TCP_RCV].medium = medium;
			}
			break;
	}

	//ReleaseMutex(hMutex);
}

int sendData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data)
{
	switch(I_param){
				case TCP:   {
					Media_TCP *medium = (Media_TCP *) Media[I_senderID][I_receiverID][TCP_SND].medium ;
					Send_TCP (*medium, (unsigned int*)PV_buffer, I_size_of_data );
					//printf("data sent\n");
					break;
							}
				case PCI:   {break;}
				default: return (ERROR);
	}


}

int receiveData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data)
{


	switch(I_param){
				case TCP:	{
					Media_TCP *medium = (Media_TCP *) Media[I_receiverID][I_senderID][TCP_RCV].medium ;

					Receive_TCP ( *medium, (unsigned int*)PV_buffer,I_size_of_data );
					//printf("data received\n");
					break;
							}
				case PCI:   {break;}
				default: return (ERROR);
	}


}

#endif // end for Code for Microsoft Visual Studio
