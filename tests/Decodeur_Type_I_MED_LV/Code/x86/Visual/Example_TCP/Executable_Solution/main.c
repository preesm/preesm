#include "x86_setup.h"
#include "MED_LV.h"




extern DWORD WINAPI computationThread_Core1( LPVOID lpParam );
/*extern DWORD WINAPI computationThread_Core1( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core2( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core3( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core4( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core5( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core6( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core7( LPVOID lpParam );*/

int _argc;
char** _argv;

typedef struct
{
	unsigned int    socket ;
	unsigned short  port ;
} Media_TCP ;

int main(int argc, char* argv[])
{


	LPTHREAD_START_ROUTINE routine[CORE_NUMBER];
	
	int i,j;
	int stacksize = 8000;

	_argc = argc;
	_argv = argv;


	routine[0] = computationThread_Core1;
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

	/*
	{
		HANDLE semaphore = CreateSemaphore (NULL,0,1,"semaphore");
		WaitForSingleObject(semaphore,INFINITE);
	}*/

	for(i=0;i<1000000000;i++){
		i--;
	}

	return 0;
}