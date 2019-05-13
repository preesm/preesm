/*
	============================================================================
	Name        : communication.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : 
	============================================================================
*/

#include <semaphore.h>
#include <preesm.h>
#include <mppa_rpc.h>
#include <mppa_async.h>
#include <HAL/hal/hal_ext.h>
#include <mOS_vcore_u.h>

#include "communication.h"

extern mppa_async_segment_t shared_segment;

extern pthread_barrier_t pthread_barrier __attribute__((__unused__)); 

/* value to test */
mppa_async_segment_t sync_segments[PREESM_NB_CLUSTERS + PREESM_IO_USED];
static long long sync[PREESM_NB_CLUSTERS + PREESM_IO_USED];

/* event */
static mppa_async_event_t sync_evt[PREESM_NB_CLUSTERS + PREESM_IO_USED];

static uintptr_t sync_remote_ptr[PREESM_NB_CLUSTERS + PREESM_IO_USED];

extern int local_buffer_size __attribute__((weak));

/* DDR Shared */ 
#define SYNC_SHARED_ADDRESS (0x3000000ULL)

extern char *local_buffer __attribute__((weak));

void sendStart(int cluster)
{

	int sender = __k1_get_cluster_id();
	int receiver = cluster;
	int receiverID = cluster;
	if(receiverID == PREESM_NB_CLUSTERS){
		receiverID = MPPA_ASYNC_SERVER_IO0;
	}
	#ifdef __k1io__
		sender = PREESM_NB_CLUSTERS;
	#endif
	if(receiver == sender)
	{
		__builtin_k1_afdau(&sync[cluster], 1); 	/* post locally */
	}else{
		//printf("Cluster %d post to cluster %d offset %llu\n", sender, receiver, (off64_t)(sender * sizeof(long long)));
		mppa_async_postadd(&sync_segments[receiver], (off64_t)(sender * sizeof(long long)), 1); /* atomic remote increment of sync[cluster] value */
	}
}

void sendEnd(){}

void receiveStart(){}

void receiveEnd(int cluster)
{
	mppa_async_event_wait(&sync_evt[cluster]);	/* unlock when sync[cluster] is GT than 0 */
	__builtin_k1_afdau(&sync[cluster], -1); 	/* rearm condition (consume token) */
	//pthread_barrier_wait(&pthread_barrier);
}

#ifdef __nodeos__
#include <omp.h>
#endif

void communicationInit() {
	#ifdef __nodeos__
	omp_set_num_threads(NB_OMP_CORE);
	#endif
	#if 0
	switch (__k1_get_cluster_id()){
		case 0:	local_buffer_size = Core0_size; break;
		case 1:	local_buffer_size = Core1_size; break;
		case 2:	local_buffer_size = Core2_size; break;
		case 3:	local_buffer_size = Core3_size; break;
		case 4:	local_buffer_size = Core4_size; break;
		case 5:	local_buffer_size = Core5_size; break;
		case 6:	local_buffer_size = Core6_size; break;
		case 7:	local_buffer_size = Core7_size; break;
		case 8:	local_buffer_size = Core8_size; break;
		case 9:	local_buffer_size = Core9_size; break;
		case 10: local_buffer_size = Core10_size; break;
		case 11: local_buffer_size = Core11_size; break;
		case 12: local_buffer_size = Core12_size; break;
		case 13: local_buffer_size = Core13_size; break;
		case 14: local_buffer_size = Core14_size; break;
		case 15: local_buffer_size = Core15_size; break;
		default: break;
	}
	#endif
	if(&local_buffer_size == 0)
	{
		printf("Cluster %d please set at codegen the local_buffer_size variable (scratchpad buffer)\n", __k1_get_cluster_id());
	}

	local_buffer = (char*)malloc(local_buffer_size);

	if(local_buffer == NULL){
		printf("Cluster %d Failed to alloc local_buffer of size %d !!\n", __k1_get_cluster_id(), local_buffer_size);
		assert(0);
	}else{
		#ifdef VERBOSE
		printf("Cluster %d Local buffer ok %llx size %d\n", __k1_get_cluster_id(),(uint64_t)(uintptr_t)local_buffer, local_buffer_size);
		#endif
	}

	/* init token */
	int i;
	for(i=0; i<PREESM_NB_CLUSTERS + PREESM_IO_USED;i++){
		/* event &sync_evt[i] unlocks when sync[i] is GT 0 (kind of remote semaphore emulation) */
		//printf("Cluster %d evalcond addr %llx\n", __k1_get_cluster_id(), (uint64_t)(uintptr_t) &sync[i]);
		mppa_async_evalcond(&sync[i], 0, MPPA_ASYNC_COND_GT, &sync_evt[i]); /* GT = Greater Than */
	}
	//extern int _start_async_copy;
	uintptr_t addr = (uintptr_t)&sync[0] /*- (uintptr_t)&_start_async_copy*/;
	//printf("Cluster %d send at %llx the addr %x\n", __k1_get_cluster_id(), (uint64_t) (uintptr_t) (SYNC_SHARED_ADDRESS+__k1_get_cluster_id()*sizeof(void*)), addr );

	int sender;
	#ifndef __k1io__
		sender = __k1_get_cluster_id();
	#else
		sender = PREESM_NB_CLUSTERS;
	#endif

	int ret = 0;
	ret = mppa_async_segment_create(&sync_segments[sender], SYNC_BASE_SEGMENT_ID + sender, (void*)(uintptr_t)&sync[0], (PREESM_NB_CLUSTERS + PREESM_IO_USED) * sizeof(long long), 0, 0, NULL);
	if(ret!=0){
		printf("Error mppa_async_segment_create\n");
	}
	mppa_async_put((void*)&addr, MPPA_ASYNC_DDR_0, SYNC_SHARED_ADDRESS+sender*sizeof(void*), sizeof(uintptr_t), NULL);
	mppa_async_fence(MPPA_ASYNC_DDR_0, NULL);

	#ifndef __k1io__
		#if PREESM_IO_USED == 1
		if(sender == 0){
			mppa_rpc_barrier(1,2);
		}
		#endif
		mppa_rpc_barrier_all();
		#if PREESM_IO_USED == 1
		if(sender == 0){
			mppa_rpc_barrier(1,2);
		}
		#endif
	#else
		mppa_rpc_barrier(1,2);
		mppa_rpc_barrier(1,2);
	#endif

	mppa_async_get(sync_remote_ptr, MPPA_ASYNC_DDR_0, SYNC_SHARED_ADDRESS, (PREESM_NB_CLUSTERS + PREESM_IO_USED)*sizeof(uintptr_t), NULL);
	int copySyncs = 0;
	for(copySyncs = 0; copySyncs < PREESM_NB_CLUSTERS + PREESM_IO_USED; copySyncs++){
		if(copySyncs != sender){
			ret = mppa_async_segment_clone(&sync_segments[copySyncs], SYNC_BASE_SEGMENT_ID + copySyncs, NULL, 0, NULL);
			if(ret!=0){
				printf("Error mppa_async_segment_clone\n");
			}
		}
	}
	#ifndef __k1io__
	mppa_rpc_barrier_all();
	#endif
#if 0

	#ifndef __k1io__
	for(i=0;i<__k1_get_cluster_id();i++)
		mppa_rpc_barrier_all();
	#endif

	for(i=0;i<PREESM_NB_CLUSTERS + PREESM_IO_USED;i++){
		printf("Cluster %d got %d addr %llx\n", sender, i, (uint64_t)(uintptr_t)sync_remote_ptr[i]);
	}

	#ifndef __k1io__
	for(i=__k1_get_cluster_id();i<PREESM_NB_CLUSTERS + PREESM_IO_USED;i++)
		mppa_rpc_barrier_all();
	#endif
#endif
#if 0
	for(i=0; i<PREESM_NB_CLUSTERS + PREESM_IO_USED;i++){
		sendStart(i);
	}

	printf("done send %d\n", __k1_get_cluster_id());
	for(i=0; i<PREESM_NB_CLUSTERS + PREESM_IO_USED;i++){
		receiveEnd(i);
	}

	mppa_rpc_barrier_all();
	printf("All cluster %d sync ok\n", __k1_get_cluster_id());
#endif
}

#define DDR_START (0x80000000UL)
#if 1

static long long memset_calls;
static long long memcpy_calls;
#define LOG_MEMCPY 8
#if 0
static uintptr_t memcpy_cur_dst[LOG_MEMCPY];
static uintptr_t memcpy_cur_src[LOG_MEMCPY];
#endif

void *__real_memset(void *s, int c, size_t n){
	uintptr_t addr = (uintptr_t)s;
	char *l = (addr >= DDR_START) ? local_buffer : s;

	__builtin_k1_wpurge();
	__builtin_k1_fence();
	mOS_dinval();

	#ifndef __k1io__
	if(addr >= DDR_START){
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, s, &offset);
		mppa_async_get(l, &shared_segment, offset, n, NULL);
	}
	#endif
	unsigned int i;
	/* memset */
	for(i=0;i<n;i++)
		l[i] = (char)c;
	
	#ifndef __k1io__
	if(addr >= DDR_START){
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, s, &offset);
		mppa_async_put(l, &shared_segment, offset, n, NULL);
	}
	#endif
	__builtin_k1_wpurge();
	__builtin_k1_fence();
	mOS_dinval();
	return s;
}

void *
__wrap_memset (void *s, int c, size_t n)
{
	//printf("mppa memset addr %llx val %d size %d\n", (uint64_t)(uintptr_t)s, c, n);
	memset_calls++;
	return __real_memset (s, c, n);
}

void *__real_memcpy(void *dest, const void *src, size_t n){
	uintptr_t dst_addr = (uintptr_t)dest;
	uintptr_t src_addr = (uintptr_t)src;

	__builtin_k1_wpurge();
	__builtin_k1_fence();
	mOS_dinval();


	unsigned int i;
#if 0
#define START_DISPLAY 5
	if(memcpy_calls > START_DISPLAY)
		printf("Cluster %d IN dst_addr %llx src_addr %llx\n", __k1_get_cluster_id(), (uint64_t)dst_addr, (uint64_t)src_addr);
	for(i=1;i<LOG_MEMCPY;i++){
		memcpy_cur_dst[i] = memcpy_cur_dst[i-1];
		memcpy_cur_src[i] = memcpy_cur_src[i-1];
	}
	memcpy_cur_dst[0] = dst_addr;
	memcpy_cur_src[0] = src_addr;
#endif

	/* local memcpy */
	if(dst_addr < DDR_START && src_addr < DDR_START){
		for(i=0 ; i<n ; i++)
			((char*)dest)[i] = ((char*)src)[i];
	}

	#ifndef __k1io__
	/* cluster -> ddr */
	if(dst_addr >= DDR_START && src_addr < DDR_START){
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, dest, &offset);
		mppa_async_put(src, &shared_segment, offset, n, NULL);
	}

	/* ddr -> cluster */
	if(dst_addr < DDR_START && src_addr >= DDR_START){
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, dest, &offset);
		mppa_async_get(dest, &shared_segment, offset, n, NULL);
	}

	/* ddr -> ddr */
	if(dst_addr >= DDR_START && src_addr >= DDR_START){
		//printf("=====> local_buffer %llx src %llx dst %llx\n", (uint64_t)(uintptr_t)local_buffer, (uint64_t)(uintptr_t)src, (uint64_t)(uintptr_t)dest );		
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, (void*)src, &offset);
		mppa_async_get(local_buffer, &shared_segment, offset, n, NULL);
		mppa_async_offset(&shared_segment, dest, &offset);
		mppa_async_put(local_buffer, &shared_segment, offset, n, NULL);
	}
	#endif

#if 0
	if(memcpy_calls > START_DISPLAY)
		printf("Cluster %d OUT dst_addr %llx src_addr %llx\n", __k1_get_cluster_id(), (uint64_t)dst_addr, (uint64_t)src_addr);
#endif
	__builtin_k1_wpurge();
	__builtin_k1_fence();
	mOS_dinval();
	return dest;
}

void *__wrap_memcpy(void *dest, const void *src, size_t n){
	//printf("mppa memcpy dest %llx src %llx size %d\n", (uint64_t)(uintptr_t)dest, (uint64_t)(uintptr_t)src, n);
	memcpy_calls++;
	return __real_memcpy(dest, src, n);
}

#endif
