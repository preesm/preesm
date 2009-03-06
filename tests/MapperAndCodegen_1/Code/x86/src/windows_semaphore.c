#include "x86.h"

void semaphoreInit(HANDLE* semaphores, int semNumber) {
	int i;

	for(i=0;i<semNumber;i++) {
		semaphores[i] = CreateSemaphore (NULL,0,1,"semaphore");
		if (semaphores[i] == NULL) {
			exit(1);
		}
	}
}
