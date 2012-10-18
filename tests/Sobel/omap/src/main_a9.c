#include <stdio.h>
#include <Std.h>
#include <loadRemote.h>
#include <com.h>
#include <pthread.h>
#include <imageIO.h>

void *computationThread_CortexA9_1(void *arg);
void *computationThread_CortexA9_2(void *arg);

// Initializing several semaphores
int sems_init(sem_t *sem, int number){
	int i;

	for(i=0; i<number; i++){
		if(sem_init(&sem[i], 0, 0) == -1){
			printf("error sem_init\n");
			return -1;
		}
	}
	return 0;
}

int main (){
	remoteProc *dsp, *sys, *app;

	loadRemote_init();
	dsp = loadRemote_connect("Tesla");
	sys = loadRemote_connect("SysM3");
	app = loadRemote_connect("AppM3");

	loadRemote_load(dsp, "./sobelPreesm_dsp.xe64T");
	loadRemote_start(dsp);

	loadRemote_load(sys, "./sobelPreesm_sys.xem3");
	loadRemote_start(sys);

	loadRemote_load(app, "./sobelPreesm_app.xem3");
	loadRemote_start(app);

	printf ("\n### Init Finished ###\n\n");	

	initCamera();
	printf("Cam Ok!\n");
	
	initDisplay();
	printf("Screen Ok!\n");
	
	comInitA9(25000);

	printf ("Com initialized\n");	

	pthread_t thread_2;
	pthread_create(&thread_2, NULL, computationThread_CortexA9_2, NULL);

	computationThread_CortexA9_1(NULL);
	
	pthread_join(thread_2, NULL);
	
	comClear();

	cleanCamera();

	cleanDisplay();
	
	printf ("\n### Start Cleaning ###\n\n");

	loadRemote_stop(dsp);
	loadRemote_stop(app);
	loadRemote_stop(sys);

	loadRemote_close(dsp);
	loadRemote_close(app);
	loadRemote_close(sys);

	loadRemote_clean();

	return 0;
}
