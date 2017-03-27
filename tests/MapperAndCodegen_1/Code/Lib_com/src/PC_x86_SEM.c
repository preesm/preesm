#include "x86.h"

void semaphoreInit(HANDLE* semaphores, int semNumber,int ID) {
	int cpt;
	char sem_name[20];
	char *temp="sem";
	char sem_num[7];

	for(cpt=0;cpt<semNumber;cpt++) {
		sem_name[0]='\0';
		strcat_s(sem_name,20,temp);
		_itoa_s(cpt,sem_num,5,10);
		strcat_s(sem_name,20,sem_num);
		_itoa_s(ID,sem_num,5,10);
		strcat_s(sem_name,20,sem_num);
		semaphores[cpt] = CreateSemaphoreA(NULL,0,1,sem_name);
		if (semaphores[cpt] == NULL) {
			exit(1);
		}
	}
}
