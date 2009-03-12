#ifndef _OS_LIB_COM
#define _OS_LIB_COM


int sendInit		(int PC_type_of_cible, int SenderId, int ReceiverId);
int sendData		(int I_param, int I_senderID, int I_receiverID, void *PV_buffer, int I_size_of_data);
int receiveData		(int I_param, int I_senderID, int I_receiverID, void *PV_buffer, int I_size_of_data);

typedef struct {
	void* medium;
}Medium;

#define MEDIA_NR 8
Medium Media[MEDIA_NR][MEDIA_NR];
semaphore sem_init[MEDIA_NR][MEDIA_NR];

#endif /* #ifndef _OS_LIB_COM*/

/* End of file OS_Com.h */