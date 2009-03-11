/***********************************************************************************\
*                                                                                 
*    Contain: Library generic                                                                     
*                                                                                 
*                                                                                 
*    Cible: Multiplatform (DSP,PC,LINUX,Pocket_PC)                                    
*                                                                                 
\***********************************************************************************/

#ifdef _CCS // Code pour projet Code Composer Studio
#include "C64x+_dmamsg.h"
#include "C64x+_srio.h"
#define		ERROR	0

void Init_DMAMSG(Media_DMAMSG *media);
void Close_DMAMSG(Media_DMAMSG *media);
void Receive_DMAMSG(Media_DMAMSG *media,char *Buffer,const int NB_bytes,unsigned char senderId);
void Send_DMAMSG(Media_DMAMSG *media,char *Buffer,const int NB_bytes, unsigned char receiverId);
#endif

#ifdef _MVS // Code pour projet Microsoft Visual Studio
#include "PC_x86_TCP.h"
#include "x86.h"
#endif




#define		PCI			110
#define		RIO			120
#define		EDMA		130
#define		RAM			140


int Com_Init (int direction, int PC_type_of_cible, int SenderId, int ReceiverId)
{
#ifdef _CCS // Code pour projet Code Composer Studio
	//futur implémentation

	switch(PC_type_of_cible){
			case EDMA:  {
				Media.senderId=SenderId;
				Media.receiverId=ReceiverId;
				Media.media= (Media_DMAMSG*)calloc(1,sizeof(Media_DMAMSG*));
				Init_DMAMSG(Media.media);
				break;
						}
			case RIO:   {break;}
			default: return (ERROR);
	}

#endif

#ifdef _MVS // Code for project Microsoft Visual Studio



	switch(direction){
		case MEDIUM_SEND:
			WaitForSingleObject(sem_init1,INFINITE); //full
			if(Media[SenderId][ReceiverId].medium == NULL){
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));
				
				medium->port = 5000+SenderId<<4+ReceiverId;
				medium->socket =  init_TCP_server (*medium);

				Media[SenderId][ReceiverId].medium = medium;
			}
			ReleaseSemaphore(sem_init1,1,NULL); //empty
			break;

		case MEDIUM_RCV: 	
			WaitForSingleObject(sem_init2,INFINITE); //full
			if(Media[ReceiverId][SenderId].medium == NULL){		
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));

				medium->port = 5000+SenderId<<4+ReceiverId;
				medium->socket =  init_TCP_client (*medium,"localhost");

				Media[ReceiverId][SenderId].medium = medium;
			}
			ReleaseSemaphore(sem_init2,1,NULL); //empty
			break;
		default: return (ERROR);
	}


#endif

	return 0;

}


#ifdef _MVS // Code for project Microsoft Visual Studio
int sendData(int I_param, int I_senderID, int I_receiverID, void *PV_buffer, int I_size_of_data)
{
	switch(I_param){
				case TCP:   {
					Media_TCP *medium = (Media_TCP *) Media[I_senderID][I_receiverID].medium ;
					Send_TCP (*medium, (unsigned int*)PV_buffer, I_size_of_data );
					printf("send fini\n");
					break;
							}
				case PCI:   {break;}
				default: return (ERROR);
	}


}

int receiveData(int I_param, int I_senderID, int I_receiverID, void *PV_buffer, int I_size_of_data)
{


	switch(I_param){
				case TCP:	{
					Media_TCP *medium = (Media_TCP *) Media[I_receiverID][I_senderID].medium ;

					Receive_TCP ( *medium, (unsigned int*)PV_buffer,I_size_of_data );
					printf("receive fini\n");
					break;
							}
				case PCI:   {break;}
				default: return (ERROR);
	}


}

#endif // end for Code for Microsoft Visual Studio

#ifdef _CCS // Code pour projet Code Composer Studio


int Send (int I_Param, void *PV_media, void *PV_buffer, int I_size_of_data, int I_receiverID){
	switch(I_Param){
			case EDMA:  {

				Send_DMAMSG(Media.media, (char*) PV_buffer ,I_size_of_data, I_receiverID);
				break;
						}
			case RIO:   {

				break;}
			default: return (ERROR);
	}


}

int Receive(int I_param, void *PV_media, void *PV_buffer, int I_size_of_data, int I_senderID)
{


	switch(I_param){
			case EDMA:	{
				Receive_DMAMSG(Media.media,(char*)PV_buffer,I_size_of_data,I_senderID);
				break;
						}
			case RIO:   {break;}
			default: return (ERROR);
	}


}

#endif// Code pour projet Code Composer Studio
