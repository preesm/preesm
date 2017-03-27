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
	#include "PC_x86_TCP.h"
	#include "x86.h"
	Medium Media[MEDIA_NR][MEDIA_NR];
	semaphore sem_init[MEDIA_NR][MEDIA_NR];


	void Com_Init (int direction, int I_media_type, short SenderId, short ReceiverId)
	{


	switch(direction){
		case MEDIUM_SEND:
			//WaitForSingleObject(sem_init1,INFINITE); //full
			if(Media[SenderId][ReceiverId].medium == NULL){
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));
				
				medium->port = 10000; //5000+SenderId<<4+ReceiverId;
				medium->socket =  init_TCP_server (*medium);

				Media[SenderId][ReceiverId].medium = medium;
			}
			//ReleaseSemaphore(sem_init1,1,NULL); //empty
			break;

		case MEDIUM_RCV: 	
			//WaitForSingleObject(sem_init2,INFINITE); //full
			if(Media[ReceiverId][SenderId].medium == NULL){		
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));

				medium->port = 10000; //5000+SenderId<<4+ReceiverId;
				medium->socket =  init_TCP_client (*medium,"192.168.1.2");

				Media[ReceiverId][SenderId].medium = medium;
			}
			//ReleaseSemaphore(sem_init2,1,NULL); //empty
			break;
	}

}

int sendData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data)
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

int receiveData(int I_param, short I_senderID, short I_receiverID, void *PV_buffer, int I_size_of_data)
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


/*#ifdef _CCS // Code pour projet Code Composer Studio
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
			WaitForSingleObject(sem_init[SenderId][ReceiverId],INFINITE); //full
			if(Media[SenderId][ReceiverId].medium == NULL){
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));
				
				medium->port = 5000+SenderId<<4+ReceiverId;
				medium->socket =  init_TCP_server (*medium);

				Media[SenderId][ReceiverId].medium = medium;
			}
			ReleaseSemaphore(sem_init[SenderId][ReceiverId],1,NULL); //empty
			break;

		case MEDIUM_RCV: 	
			WaitForSingleObject(sem_init[ReceiverId][SenderId],INFINITE); //full
			if(Media[ReceiverId][SenderId].medium == NULL){		
				Media_TCP *medium = (Media_TCP *) malloc(sizeof(Media_TCP));

				medium->port = 5000+SenderId<<4+ReceiverId;
				medium->socket =  init_TCP_client (*medium,"localhost");

				Media[ReceiverId][SenderId].medium = medium;
			}
			ReleaseSemaphore(sem_init[ReceiverId][SenderId],1,NULL); //empty
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

#endif// Code pour projet Code Composer Studio*/
