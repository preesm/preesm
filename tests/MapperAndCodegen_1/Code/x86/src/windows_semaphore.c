#include "x86.h"

void semaphoreInit(HANDLE* semaphores, int semNumber) {
	int i;

	for(i=0;i<semNumber;i++) {
		TCHAR name[15] = {'\0'};
		TCHAR index[15] = {'\0'};
		_itot_s(i, index, sizeof(index)/sizeof(TCHAR), 10);
		strcat(name,"semaphore");
		strcat(name,index);
		semaphores[i] = CreateSemaphore (NULL,0,1,name);
		if (semaphores[i] == NULL) {
			exit(1);
		}
	}
}
