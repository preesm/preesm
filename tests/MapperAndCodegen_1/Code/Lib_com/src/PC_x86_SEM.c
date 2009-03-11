#include "x86.h"

void semaphoreInit(HANDLE* semaphores, int semNumber,int ID) {
	int cpt;
	char sem_name[20];
	char *temp="sem";
	char sem_num[4];

	for(cpt=0;cpt<semNumber;cpt++) {
		sem_name[0]='\0';
		strcat(sem_name,temp);
		itoa(cpt,sem_num,10);
		strcat(sem_name,sem_num);
		itoa(ID,sem_num,10);
		strcat(sem_name,sem_num);
		semaphores[cpt] = CreateSemaphoreA(NULL,0,1,sem_name);
		if (semaphores[cpt] == NULL) {
			exit(1);
		}
	}
}
