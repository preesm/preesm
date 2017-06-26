/*
 * Copyright (C) 2016 Kalray SA.
 *
 * All rights reserved.
 */
#include "mOS_common_types_c.h"
#include "mOS_constants_c.h"
#include "mOS_vcore_u.h"
#include "mOS_segment_manager_u.h"
#include "stdlib.h"
#include "stdio.h"
#include "vbsp.h"
#include <mppa_rpc.h>
#include <mppa_remote.h>
#include <mppa_async.h>
#include "HAL/hal/hal_ext.h"
#include <math.h>
#include <stdlib.h>

#ifdef __nodeos__
#define CONFIGURE_DEFAULT_TASK_STACK_SIZE (1U<<12)
#define CONFIGURE_AMP_MAIN_STACK_SIZE (1U<<12)
#include <mppa/osconfig.h>
#include <omp.h>
#else
#include <utask.h>
#endif

#include <pthread.h>

#include <assert.h>

#include "preesm.h"
#include "communication.h"

/* Shared Segment ID */
mppa_async_segment_t shared_segment;

/* MPPA PREESM Thread definition */
typedef void* (*mppa_preesm_task_t)(void *args);

/* pthread_t declaration */
static pthread_t threads[NB_CORE-1] __attribute__((__unused__));

/* thread function pointers declaration */
static mppa_preesm_task_t mppa_preesm_task[NB_CLUSTER] __attribute__((__unused__)); 

/* global barrier called at each execution of ALL of the dataflow graph */
pthread_barrier_t pthread_barrier __attribute__((__unused__)); 

/* extern reference of generated code */
extern void *computationTask_Core0(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core1(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core2(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core3(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core4(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core5(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core6(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core7(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core8(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core9(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core10(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core11(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core12(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core13(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core14(void *arg) __attribute__((__unused__, weak));
extern void *computationTask_Core15(void *arg) __attribute__((__unused__, weak));

/* Total number of cycles spent in async put/get */
long long total_get_cycles[NB_OMP_CORE] = {0};
long long total_put_cycles[NB_OMP_CORE] = {0};

/* Main executed on PE0 */
int
main(void)
{
	mppa_rpc_client_init();
	mppa_async_init();
	mppa_remote_client_init();

	mppa_async_segment_clone(&shared_segment, SHARED_SEGMENT_ID, NULL, 0, NULL);

	// init comm
	communicationInit();

#ifdef VERBOSE
	printf("Hello cluster %d\n", __k1_get_cluster_id());
#endif
	/* Dirty generated threads wrapper to function pointers */ 
#if (CLUSTER_ID==0)
	mppa_preesm_task[0] = computationTask_Core0;
#else
#if (CLUSTER_ID==1)
	mppa_preesm_task[1] = computationTask_Core1;
#else
#if (CLUSTER_ID==2)
	mppa_preesm_task[2] = computationTask_Core2;
#else
#if (CLUSTER_ID==3)
	mppa_preesm_task[3] = computationTask_Core3;
#else
#if (CLUSTER_ID==4)
	mppa_preesm_task[4] = computationTask_Core4;
#else
#if (CLUSTER_ID==5)
	mppa_preesm_task[5] = computationTask_Core5;
#else
#if (CLUSTER_ID==6)
	mppa_preesm_task[6] = computationTask_Core6;
#else
#if (CLUSTER_ID==7)
	mppa_preesm_task[7] = computationTask_Core7;
#else
#if (CLUSTER_ID==8)
	mppa_preesm_task[8] = computationTask_Core8;
#else
#if (CLUSTER_ID==9)
	mppa_preesm_task[9] = computationTask_Core9;
#else
#if (CLUSTER_ID==10)
	mppa_preesm_task[10] = computationTask_Core10;
#else
#if (CLUSTER_ID==11)
	mppa_preesm_task[11] = computationTask_Core11;
#else
#if (CLUSTER_ID==12)
	mppa_preesm_task[12] = computationTask_Core12;
#else
#if (CLUSTER_ID==13)
	mppa_preesm_task[13] = computationTask_Core13;
#else
#if (CLUSTER_ID==14)
	mppa_preesm_task[14] = computationTask_Core14;
#else
#if (CLUSTER_ID==15)
	mppa_preesm_task[15] = computationTask_Core15;
#endif // CLUSTER 15
#endif // CLUSTER 14
#endif // CLUSTER 13
#endif // CLUSTER 12
#endif // CLUSTER 11
#endif // CLUSTER 10
#endif // CLUSTER 9
#endif // CLUSTER 8
#endif // CLUSTER 7
#endif // CLUSTER 6
#endif // CLUSTER 5
#endif // CLUSTER 4
#endif // CLUSTER 3
#endif // CLUSTER 2
#endif // CLUSTER 1
#endif // CLUSTER 0

#ifdef VERBOSE
	printf("Cluster %d Booted with NB_CLUSTER %d\n", __k1_get_cluster_id(), NB_CLUSTER);
#if 0
	{
		int i;
		for (i = 0; i < NB_CLUSTER; i++) {
			printf("Cluster %d PE %d Thread Address %x\n", __k1_get_cluster_id(), __k1_get_cpu_id(), mppa_preesm_task[i]);
		}
	}
#endif
#endif

	pthread_barrier_init(&pthread_barrier, NULL, NB_CORE);
	__builtin_k1_wpurge();
	__builtin_k1_fence();
	mOS_dinval();

#if 0	
	/* create threads if any */
	for (i = 0; i < NB_CLUSTER-1; i++) {
		printf("Start thread %d\n", i);
		pthread_create(&threads[i], NULL, mppa_preesm_task[i+1], NULL);
	}
#endif

	mppa_rpc_barrier_all();
	uint64_t start = __k1_read_dsu_timestamp(); /* read clock cycle */

	/* PE0 work */	
	if(mppa_preesm_task[__k1_get_cluster_id()] != 0){
		//printf("Cluster %d starts task\n", __k1_get_cluster_id());
		mppa_preesm_task[__k1_get_cluster_id()](NULL);
	}else{
		printf("Cluster %d Error on code generator wrapper\n", __k1_get_cluster_id());
	}
#if 0
	/* join other threads if any */
	for (i = 0; i < NB_CLUSTER-1; i++) {
		printf("Join thread %d\n", i);
		pthread_join(threads[i], NULL);
	}
#endif
	mppa_rpc_barrier_all();
	uint64_t end = __k1_read_dsu_timestamp(); /* read clock cycle */

#ifdef VERBOSE
	mOS_dinval();
	long long max_total_get_cycles = 0;
	long long max_total_put_cycles = 0;
	for(int i=0;i<NB_OMP_CORE;i++)
	{
		if(max_total_get_cycles < total_get_cycles[i])
			max_total_get_cycles = total_get_cycles[i];
		if(max_total_put_cycles < total_put_cycles[i])
			max_total_put_cycles = total_put_cycles[i];
	}
	printf("Cluster %d Cycles: Total %llu Get %llu Put %llu GetPut %llu Ratio Communication/Total %.2f %%\n", __k1_get_cluster_id(),
		end - start,
		max_total_get_cycles,
		max_total_put_cycles,
		max_total_get_cycles+max_total_put_cycles,
		(float)(max_total_get_cycles+max_total_put_cycles) / (float)(end-start) * 100.0f);
		
		printf("Cluster %d Total %.3f ms Graph Step %.3f ms FPS %.2f\n", 
		__k1_get_cluster_id(), 
		((double)(end - start))/((float)(__bsp_frequency/1000)),
		((double)(end - start))/((float)(__bsp_frequency/1000))/GRAPH_ITERATION, 
		1/((((double)(end - start))/((float)(__bsp_frequency/1000))/GRAPH_ITERATION)/1000.0f));
#endif

	mppa_rpc_barrier_all();
	#ifdef VERBOSE
	if(__k1_get_cluster_id() == 0)
	{
		float bw = (float)((float)(max_total_get_cycles+max_total_put_cycles) / ((float)(end-start)) * 100.0f);
		float fps = 1/((((double)(end - start))/((float)(__bsp_frequency/1000))/GRAPH_ITERATION)/1000.0f);
		float time_ms = ((double)(end - start))/((float)(__bsp_frequency/1000));
		
		#ifdef __nodeos__
		printf("\n\t NB_CLUSTER %d NB_OMP_CORE %d FPS %.2f BW %.2f %% Total run time %.2f ms\n\n", NB_CLUSTER, NB_OMP_CORE, fps, bw, time_ms);
		#else
		printf("\n\t NB_CLUSTER %d FPS %.2f BW %.2f %% Total run time %.2f ms\n\n", NB_CLUSTER, fps, bw, time_ms);
		#endif
	}
	#endif
	mppa_rpc_barrier_all();
	mppa_async_final();
	return 0;
}

