#include "x86.h"


#define CORE_NUMBER 4

extern DWORD WINAPI computationThread_Core0( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core1( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core2( LPVOID lpParam );
extern DWORD WINAPI computationThread_Core3( LPVOID lpParam );


int main(void)
{
  LPTHREAD_START_ROUTINE routine[CORE_NUMBER];

  int i,j;
  int stacksize = 8000;

  routine[0] = computationThread_Core0;
  routine[1] = computationThread_Core1;
  routine[2] = computationThread_Core2;
  routine[3] = computationThread_Core3;


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
  }


  for(i=0;i<1000000000;i++){
    i--;
  }

  return 0;
}
