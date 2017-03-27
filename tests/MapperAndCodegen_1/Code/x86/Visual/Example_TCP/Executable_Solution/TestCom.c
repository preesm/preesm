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
#include "../../../../Lib_com/include/x86.h"

/*
DWORD WINAPI ThreadFunc( LPVOID lpParam )
{
printf("test\n");
return 0;
}

void test(void)
{
LPTHREAD_START_ROUTINE routine[8];

routine[0] = ThreadFunc;

int stacksize = 8000;
CreateThread(NULL,stacksize,routine[0],NULL,0,NULL);

printf("finished\n");
}*/


extern DWORD WINAPI computationThread_x86( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core1( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core2( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core3( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core4( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core5( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core6( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core7( LPVOID lpParam );

int main(void)
{
	LPTHREAD_START_ROUTINE routine[CORE_NUMBER];

	int i,j;
	int stacksize = 8000;

	routine[0] = computationThread_x86;
	/*routine[1] = computationThread_Core1;
	routine[2] = computationThread_Core2;
	routine[3] = computationThread_Core3;
	routine[4] = computationThread_Core4;
	routine[5] = computationThread_Core5;
	routine[6] = computationThread_Core6;
	routine[7] = computationThread_Core7;*/

	for(i=0;i<MEDIA_NR;i++){
		for(j=0;j<MEDIA_NR;j++){
			char sem_name[20]="sem_init";
			char sem_num[4];

			if(i!=j){
				memset(&Media[i][j],0,sizeof(Medium));
				_itoa_s(i,sem_num,2,10);
				strcat_s(sem_name,20,sem_num);
				strcat_s(sem_name,20,"_");
				_itoa_s(j,sem_num,2,10);
				strcat_s(sem_name,20,sem_num);
				sem_init[i][j] = CreateSemaphoreA(NULL,1,1,sem_name);
			}
		}
	}



	for(i=0;i<CORE_NUMBER;i++){
		HANDLE thread = CreateThread(NULL,stacksize,routine[i],NULL,0,NULL);
		SetThreadAffinityMask(thread,1<<i);
		/*SetThreadPriority(
		thread,
		THREAD_PRIORITY_HIGHEST
		);*/

	}

	//test();
	//HANDLE semaphore = CreateSemaphore (NULL,0,1,"semaphore");
	//WaitForSingleObject(semaphore,INFINITE);
	//WaitForSingleObject(semaphore,INFINITE);

	for(i=0;i<1000000000;i++){
		i--;
	}

	return 0;
}
