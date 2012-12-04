/*
	============================================================================
	Name        : communication.c
	Author      : mpelcat
	Version     :
	Copyright   : file enabling generic point-to-point communication
	Description :
	============================================================================
*/

#ifdef _MVS // Code for Microsoft Visual Studio
/* 
*	This part of the library contain the differents fonctions used by the generating of code.
*	cf. the x86.xslt appropriate.
*/
#include "tcp.h"
#include "x86.h"

Medium Media[CORE_NUMBER][CORE_NUMBER];

void comInits(short myId){
	int i;

	for(i=0; i<CORE_NUMBER; i++){
		if(i < myId){
			comInit (MEDIUM_RCV, i, myId);
			comInit (MEDIUM_SEND, myId, i);
		}
		else if(i > myId){
			comInit (MEDIUM_SEND, myId, i);
			comInit (MEDIUM_RCV, i, myId);
		}
	}

	for(i=0; i<CORE_NUMBER; i++){
		if(i > myId){
		}
	}
}

void comInit (int direction, short SenderId, short ReceiverId){
	switch(direction){
		case MEDIUM_SEND:
			if(Media[SenderId][ReceiverId].medium == NULL){
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));
				
				medium->port = 10000;
				medium->socket =  init_TCP_server (*medium);

				Media[SenderId][ReceiverId].medium = medium;
			}
			break;

		case MEDIUM_RCV:
			if(Media[ReceiverId][SenderId].medium == NULL){		
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));

				medium->port = 10000;
				medium->socket =  init_TCP_client (*medium,"localhost");

				Media[ReceiverId][SenderId].medium = medium;
			}
			break;
	}
}

int sendData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data){
	switch(I_param){
				case TCP:   {
					Media_TCP *medium = (Media_TCP *) Media[I_senderID][I_receiverID].medium ;
					Send_TCP (*medium, (unsigned int*)PV_buffer, I_size_of_data );
					printf("send OK\n");
					break;
							}
				default: return (ERROR);
	}

	return ERROR;
}

int receiveData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data){
	switch(I_param){
				case TCP:	{
					Media_TCP *medium = (Media_TCP *) Media[I_receiverID][I_senderID].medium ;

					Receive_TCP ( *medium, (unsigned int*)PV_buffer,I_size_of_data );
					printf("receive OK\n");
					break;
							}
				default: return (ERROR);
	}

	return ERROR;
}

#endif // end for Code for Microsoft Visual Studio
