/*
	============================================================================
	Name        : communication.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "communication.h"

// note: rk_ struct and functions comes from
// https://stackoverflow.com/questions/27736618/why-are-sem-init-sem-getvalue-sem-destroy-deprecated-on-mac-os-x-and-w

struct rk_sema {
#ifdef __APPLE__
    dispatch_semaphore_t    sem;
#else
    sem_t                   sem;
#endif
};


static inline void
rk_sema_init(struct rk_sema *s, int value)
{
#ifdef __APPLE__
    dispatch_semaphore_t *sem = &s->sem;

    *sem = dispatch_semaphore_create(value);
#else
    sem_init(&s->sem, 0, value);
#endif
}

static inline void
rk_sema_wait(struct rk_sema *s)
{

#ifdef __APPLE__
    dispatch_semaphore_wait(s->sem, DISPATCH_TIME_FOREVER);
#else
    int r;

    do {
            r = sem_wait(&s->sem);
    } while (r == -1);
#endif
}

static inline void
rk_sema_post(struct rk_sema *s)
{

#ifdef __APPLE__
    dispatch_semaphore_signal(s->sem);
#else
    sem_post(&s->sem);
#endif
}


// 8 local semaphore for each core (1 useless per core)
struct rk_sema interCoreSem[MAX_NB_CORES][MAX_NB_CORES];

void communicationInit(){
	int i, j;
	for (i = 0; i < MAX_NB_CORES; i++){
		for (j = 0; j < MAX_NB_CORES; j++){
			rk_sema_init(&interCoreSem[i][j], 0);
		}
	}
}

void sendStart(int senderID, int receiverID){
	rk_sema_post(&interCoreSem[receiverID][senderID]);
}

void sendEnd(){}

void receiveStart(){}

void receiveEnd(int senderID, int receiverID){
	rk_sema_wait(&interCoreSem[receiverID][senderID]);
}
