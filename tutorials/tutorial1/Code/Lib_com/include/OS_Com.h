/*#ifndef _OS_LIB_COM
#define _OS_LIB_COM



typedef struct {
	void* medium;
}Medium;

#define MEDIA_NR 4
Medium Media[MEDIA_NR][MEDIA_NR];
semaphore sem_init[MEDIA_NR][MEDIA_NR];

#endif /* #ifndef _OS_LIB_COM*/
#ifndef _OS_LIB_COM
#define _OS_LIB_COM

#define		TCP		100
#define		PCI		101
#define		RIO		102
#define		EDMA3	103
#define		RAM		104
#define     DRB		105

#define     MEDIUM_SEND		0
#define     MEDIUM_RCV		1

#define     SEND		0
#define     RECEIVE		1


typedef struct {
	void* medium;
}Medium;

#ifdef _MVS

int sendInit		(int PC_type_of_cible, int SenderId, int ReceiverId);
int sendData		(int I_param, int I_senderID, int I_receiverID, void *PV_buffer, int I_size_of_data);
int receiveData		(int I_param, int I_senderID, int I_receiverID, void *PV_buffer, int I_size_of_data);


semaphore sem_init1;
semaphore sem_init2;

extern Medium Media[MEDIA_NR][MEDIA_NR];
extern semaphore sem_init[MEDIA_NR][MEDIA_NR];

#endif


#endif /* #ifndef _OS_LIB_COM*/

/* End of file OS_Com.h */
/* End of file OS_Com.h */