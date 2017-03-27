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

extern void * malloc (int);



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