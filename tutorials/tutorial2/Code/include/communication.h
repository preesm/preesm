/*
	============================================================================
	Name        : communication.h
	Author      : mpelcat
	Version     :
	Copyright   : file enabling generic point-to-point communication
	Description :
	============================================================================
*/

#ifndef _OS_LIB_COM
#define _OS_LIB_COM

#define MEDIUM_SEND 0
#define MEDIUM_RCV 1
#define TCP 100

typedef struct {
	void* medium;
}Medium;

#ifdef _MVS

int sendData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data);
int receiveData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data);
void comInit (int direction, int I_media_type, short SenderId, short ReceiverId);

#endif


#endif