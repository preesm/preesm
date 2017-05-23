/*
	============================================================================
	Name        : semaphore6678.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Set of functions ans structure to handle multicore semaphores
	              on a C6678 DSP Multicore EVM
	============================================================================
*/

#ifndef SEMAPHORE_6678_H
#define SEMAPHORE_6678_H

#include <xdc/std.h>
#include <ti/ipc/GateMP.h>

/* Notify event number that the app uses for sem_delete*/
#define EVENTID_SEM_DELETE         11

/**
 * Structure used to store all information related to a semaphore.
 * - mutex:     the GateMP associated to the semaphore and used to prevent
 *              concurrent accesses to the semaphore value.
 * - id:        Character string used to identify the GateMP associated to the
 *              semaphore. This ID must be unique.
 * - value:     Integer value associated to the semaphore.
 * - creatorID: Integer id of the core that created this semaphore. Only this
 *              core may delete the GateMP associated to this semaphore.
 */
typedef struct sem_t {
	GateMP_Handle mutex ;
	const char* id;
	int value;
	int creatorID;
} sem_t;


/**
 * This function empties the semaphoresToClean array by deleting the GateMP
 * stored in it.
 *
 * Interrupts are disabled during this function call.
 */
void sem_clean();

/**
 * Initialize a new semaphore.
 * This function creates the GateMP associated to a new semaphore and
 * initializes the semaphore value.
 * Only the core calling this function can delete the GateMP associated
 * to the semaphore. (cf. sem_destroy()).
 *
 * This function calls the sem_clean() function.
 *
 * @param sem
 * 		Pointer to the sem_t structure initialized by this function.
 * @param pshared
 * 		Parameter not used in current version. (cf. pthread for more info)
 * @param initValue
 * 		Initial value of the semaphore.
 * @param mutexID
 * 		Character string used as a unique identifier for the GateMP associated
 * 		to this semaphore. The behavior of this function with a non-unique ID
 * 		has not been tested.
 */
void sem_init(sem_t * sem, int pshared, int initValue, const char* mutexID);

/**
 * Increment the value of the semaphore.
 * This function is used to increment the value associated to the semaphore
 * passed as a parameter. The value incrementation is protected by a GateMP.
 *
 * This function calls the sem_clean() function.
 *
 * @param sem
 * 		Pointer to the sem_t structure whose value is incremented by this
 * 		function. The semaphore passed as a parameter must have been
 * 		initialized by the sem_init() function before calling sem_post().
 */
void sem_post(sem_t *sem);

/**
 * Decrement the value of the semaphore.
 * This function waits for the value of the semaphore to be greater than 0.
 * As soon as this happens, this function decrement the semaphore value and
 * returns.
 *
 * This function calls the sem_clean() function.
 *
 * @param sem
 * 		Pointer to the sem_t structure whose value is decremented by this
 * 		function. The semaphore passed as a parameter must have been
 * 		initialized by the sem_init() function before calling sem_post().
 */
void sem_wait(sem_t *sem);

/**
 * Try to decrement the value of the semaphore.
 * This function checks for the value of the semaphore to be greater than 0.
 * In such case, the value of the semaphore is decremented and the function
 * returns 0. Otherwise (value <= 0), the function returns -1.
 *
 * This function calls the sem_clean() function.
 *
 * @param sem
 * 		Pointer to the sem_t structure whose value is decremented by this
 * 		function. The semaphore passed as a parameter must have been
 * 		initialized by the sem_init() function before calling sem_post().
 * @return 0 if the value of the semaphore was greater than 0, else -1.
 */
int  sem_trywait(sem_t *sem);

/**
 * Function used to destroy a semaphore.
 * If the function is called on the same core as the one that initialized the
 * semaphore (sem_init()), the function closes and deletes the GateMP.
 * Otherwise, this function sends a notification event to the sem->creatorID
 * core. This notification will be received by the sem_destroyCallback()
 * callback function of the remote core.
 *
 * This function calls the sem_clean() function.
 *
 * @param sem
 * 		Pointer to the sem_t structure to destroy.
 */
void sem_destroy(sem_t *sem);

/**
 * Callback function called when a remote processor destroys a semaphore
 * that was initalized by the current core. Since GateMP_delete() cannot
 * be called from a callback function, this function pushes the received
 * GateMP_Handle in the semaphoresToClean() array.
 *
 * @param payload
 * 		The GateMP_Handle of the semaphore that must be deleted.
 */
Void sem_destroyCallback(UInt16 procId, UInt16 lineId, UInt32 eventId,
		UArg arg, UInt32 payload);

#endif
