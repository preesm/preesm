/*
 ============================================================================
 Name        : TestCom.c
 Author      : mpelcat
 Version     :
 Copyright   : Calling a PREESM generated code
 Description :
 ============================================================================
 */
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


extern DWORD WINAPI computationThread_Core0( LPVOID lpParam );
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

	int i;
    int stacksize = 8000;

	routine[0] = computationThread_Core0;
	routine[1] = computationThread_Core1;
	routine[2] = computationThread_Core2;
	routine[3] = computationThread_Core3;
	routine[4] = computationThread_Core4;
	routine[5] = computationThread_Core5;
	routine[6] = computationThread_Core6;
	routine[7] = computationThread_Core7;

	/*for(i=0;i<MEDIA_NR;i++){
		memset(&Media[i][],0,sizeof(Medium));
	}*/

	sem_init1 =CreateSemaphoreA(NULL,1,1,"sem_init1");
	sem_init2 =CreateSemaphoreA(NULL,1,1,"sem_init2");


	for(i=0;i<CORE_NUMBER;i++){
		HANDLE thread = CreateThread(NULL,stacksize,routine[i],NULL,0,NULL);
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
