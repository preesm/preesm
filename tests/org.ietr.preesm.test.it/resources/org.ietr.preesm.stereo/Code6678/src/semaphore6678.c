/*
	============================================================================
	Name        : semaphore6678.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Set of functions ans structure to handle multicore semaphores
	              on a C6678 DSP Multicore EVM
	============================================================================
*/

#include <semaphore6678.h>
#include <cache.h>
#include <xdc/std.h>
#include <ti/ipc/SharedRegion.h>
#include <ti/ipc/GateMP.h>
#include <ti/ipc/Notify.h>
#include <ti/sysbios/hal/Hwi.h>
#include <ti/ipc/MultiProc.h>

/* Interrupt line used (0 is default) */
#define INTERRUPT_LINE  0

/**
 *  Shared region used to allocate the GateMP of the semaphores
 */
#define SEMAPHORE_SHARED_REGION_ID 0

// Local set of semaphore that must be cleaned
// (one for each core)
/**
 * Maximum number of GateMP in the semaphoresToClean array.
 */
#define MAX_NB_SEM_TO_CLEAN 100

/**
 * Current number of semaphore in the semaphoresToClean array for the local
 * core.
 */
int nbSemaphoresToClean = 0;

/**
 * This non-shared array stores the GateMP whose destruction was initiated by
 * a remote processor calling sem_destroy(). The sem_clean() function empties
 * this array.
 */
GateMP_Handle semaphoresToClean[MAX_NB_SEM_TO_CLEAN];

void sem_init(sem_t *sem, int pshared, int initValue, const char* mutexID) {
	GateMP_Params params;
	int status;
	int nbFail = 0;

	// cleanup
	sem_clean();

	// Init semaphores
	GateMP_Params_init(&params);
	params.name = (String) mutexID;
	params.regionId = SEMAPHORE_SHARED_REGION_ID;
	sem->mutex = GateMP_create(&params);

	do {
		status = GateMP_open((String) mutexID, &sem->mutex);
		if (status < 0) {
			nbFail++;
		}
	} while (status < 0);

	// Init value
	sem->value = initValue;
	sem->id = mutexID;
	sem->creatorID = MultiProc_self();

	// Write Back
	cache_wb(sem, sizeof(sem_t));
}

void sem_post(sem_t *sem) {
	GateMP_Handle handle;
	int status;
	int nbFail = 0;

	// cleanup
	sem_clean();

	// Invalidate the sem
	cache_inv(sem, sizeof(sem_t));

	// Open the Semaphore
	if (sem->creatorID != MultiProc_self()) {
		do {
			status = GateMP_open((String) sem->id, &handle);
			if (status < 0) {
				nbFail++;
			}
		} while (status < 0);
	} else {
		handle = sem->mutex;
	}

	// Post the value
	IArg key = GateMP_enter(handle);
	// invalidate again (in case tha value cas changed)
	cache_inv(sem, sizeof(sem_t));
	sem->value++;
	// WB the new value
	cache_wb(sem, sizeof(sem_t));
	GateMP_leave(handle, key);

	// Close the sem
	if (sem->creatorID != MultiProc_self()) {
		GateMP_close(&handle);
	}
}

void sem_wait(sem_t *sem) {
	int done = 0;
	IArg key;
	int status;
	int nbFail=0;
	GateMP_Handle handle;

	// cleanup
	sem_clean();

	// While the operation is not completed
	while (done == 0) {
		do {
			cache_inv(sem, sizeof(sem_t));
		} while (sem->value <= 0);

		// Open the Semaphore
		if (sem->creatorID != MultiProc_self()) {
			do {
				status = GateMP_open((String) sem->id, &handle);
				if (status < 0) {
					nbFail++;
				}
			} while (status < 0);
		} else {
			handle = sem->mutex;
		}

		key = GateMP_enter(handle);
		cache_inv(sem, sizeof(sem_t));
		if (sem->value >= 1) {
			done = 1;
			sem->value--;
			cache_wb(sem, sizeof(sem_t));
		}
		GateMP_leave(handle, key);

		// Close the sem
		if (sem->creatorID != MultiProc_self()) {
			GateMP_close(&handle);
		}
	}
}

int sem_trywait(sem_t *sem) {
	IArg key;
	int success = -1;
	int status = -1;
	int nbFail = 0;
	GateMP_Handle handle;

	// cleanup
	sem_clean();

	cache_inv(sem, sizeof(sem_t));

	// Open the Semaphore
	if (sem->creatorID != MultiProc_self()) {
		do {
			status = GateMP_open((String) sem->id, &handle);
			if (status < 0) {
				nbFail++;
			}
		} while (status < 0);
	} else {
		handle = sem->mutex;
	}

	key = GateMP_enter(handle);
	cache_inv(sem, sizeof(sem_t));
	if (sem->value >= 1) {
		sem->value--;
		cache_wb(sem, sizeof(sem_t));
		success = 0;
	}
	GateMP_leave(handle, key);

	// Close the sem
	if (sem->creatorID != MultiProc_self()) {
		GateMP_close(&handle);
	}
	return success;
}

void sem_destroy(sem_t *sem) {

	// cleanup
	sem_clean();

	cache_inv(sem, sizeof(sem_t));
	if (sem->creatorID == MultiProc_self()) {
		GateMP_Handle copyHandle;
		copyHandle = sem->mutex;
		GateMP_close(&copyHandle);
		GateMP_delete(&sem->mutex);
	} else {
		Int status = Notify_sendEvent(sem->creatorID, INTERRUPT_LINE,
				EVENTID_SEM_DELETE, (UInt32) sem->mutex, TRUE);
		if (status < 0) {
			System_abort("sendEvent failed\n");
		}
	}

}

void sem_clean() {
	if (nbSemaphoresToClean > 0) {
		/* Disable Interrupts */
		Uint32 key = Hwi_disable();
		do {
			GateMP_Handle copyHandle;
			nbSemaphoresToClean--;
			copyHandle = semaphoresToClean[nbSemaphoresToClean];
			GateMP_close(&copyHandle);
			GateMP_delete(&semaphoresToClean[nbSemaphoresToClean]);
		} while (nbSemaphoresToClean > 0);

		/* Reenable Interrupts.*/
		Hwi_restore(key);
	}
}

Void sem_destroyCallback(UInt16 procId, UInt16 lineId, UInt32 eventId, UArg arg,
		UInt32 payload) {
	// Unless there are already too many GateMP to clean
	if (nbSemaphoresToClean < MAX_NB_SEM_TO_CLEAN) {
		// Push the GateMP_Handle received as a payload in the semaphores
		// to clean array.
		semaphoresToClean[nbSemaphoresToClean] = (GateMP_Handle) payload;
		nbSemaphoresToClean++;
	} else {
		System_abort("Too many uncleaned semaphores.\n");
	}
}
