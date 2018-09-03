/*
	============================================================================
	Name        : x86.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef X86_H
#define X86_H

#define VERBOSE
	
#ifdef _WIN32
#include <windows.h>
#include <pthread.h>
#else
// For Linux
// Pthread barriers are defined in POSIX 2001 version
// For the 1990 revision compliance the defined value of _POSIX_VERSION should be 1.
// For the 1995 revision compliance the defined value of _POSIX_VERSION should be 199506L.
// For the 2001 revision compliance the defined value of _POSIX_VERSION should be 200112L.
#if ((defined _XOPEN_SOURCE && (_XOPEN_SOURCE - 0) < 600) || \
     !defined _XOPEN_SOURCE)
#define _XOPEN_SOURCE 600
#endif
#if !defined _POSIX_C_SOURCE
#if (defined _XOPEN_SOURCE && (_XOPEN_SOURCE - 0) < 700)
#define _POSIX_C_SOURCE 200112L
#elif (defined _XOPEN_SOURCE && (_XOPEN_SOURCE - 0) < 800)
#define _POSIX_C_SOURCE 200809L
#endif
#endif
#include <pthread.h>
#endif

#include <stdlib.h>
#include <stdio.h>

#include <string.h>
#include <semaphore.h>

#include "communication.h"
#include "fifo.h"
#include "dump.h"

#include "environment.h"
#include "mlp.h"
#include "actor.h"
#include "render.h"
#include "critic.h"
#include "common.h"


//TODO:: proper init
#define init_train() { \
envInit((float*)FIFO_Head_broadcastState_end__0, (float*)FIFO_Head_BroadcastStateFeat__1); \
memcpy(FIFO_Head_BroadcastStateFeat__2, FIFO_Head_BroadcastStateFeat__1, 3 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastStateFeat__3, FIFO_Head_BroadcastStateFeat__1, 3 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastStateFeat__0, FIFO_Head_BroadcastStateFeat__1, 3 * sizeof(float)); \
initAdam((float*)FIFO_Head_Update_Actor_Broad__0); \
initAdam((float*)FIFO_Head_Update_Critic_Broa__0); \
randomVectorInitializer(80, 0.f, 1.f, (float*)FIFO_Head_BroadcastWeightsAc__0); \
randomVectorInitializer(20, 0.f, 0.5f, (float*)FIFO_Head_BroadcastWeightsAc__0 + 60 * sizeof(float)); \
constantVectorInitializer(21, 0.f, (float*)FIFO_Head_BroadcastBiasActor__0); \
constantVectorInitializer(21, 0.f, (float*)FIFO_Head_BroadcastBiasCriti__0); \
randomVectorInitializer(80, 0.f, 1.f, (float*)FIFO_Head_BroadcastWeightsCr__0); \
randomVectorInitializer(20, 0.f, 0.5f, (float*)FIFO_Head_BroadcastWeightsCr__0 + 60 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastBiasCriti__1, FIFO_Head_BroadcastBiasCriti__0, 21 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastWeightsCr__1, FIFO_Head_BroadcastWeightsCr__0, 80 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastBiasCriti__2, FIFO_Head_BroadcastBiasCriti__0, 21 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastWeightsCr__2, FIFO_Head_BroadcastWeightsCr__0, 80 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastBiasActor__1, FIFO_Head_BroadcastBiasActor__0, 21 * sizeof(float)); \
memcpy(FIFO_Head_BroadcastWeightsAc__1, FIFO_Head_BroadcastWeightsAc__0, 80 * sizeof(float)); \
}

#define init_pred() {\
envInit((float*)FIFO_Head_broadcastState_end__0, (float*)FIFO_Head_Environment_end_st__0);\
actorWeightGenInit(0, (float*)FIFO_Head_Actor_MLP_action_p__0, (float*)FIFO_Head_Actor_MLP_action_p__1);\
actorWeightGenInit(1, (float*)FIFO_Head_Actor_MLP_action_p__2, (float*)FIFO_Head_Actor_MLP_action_p__3); \
}

typedef unsigned char uchar;

#endif
