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
