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

extern mppa_async_segment_t shared_segment;

extern pthread_barrier_t pthread_barrier __attribute__((__unused__)); 

/* value to test */
static long long sync[NB_CLUSTER];

/* event */
static mppa_async_event_t sync_evt[NB_CLUSTER];

static uintptr_t sync_remote_ptr[NB_CLUSTER];

/* DDR Shared */ 
#define SYNC_SHARED_ADDRESS (0x3000000ULL)

extern char *local_buffer __attribute__((weak));
extern long long total_get_cycles[];
extern long long total_put_cycles[];

extern int Core0_size __attribute__((weak));
extern int Core1_size __attribute__((weak));
extern int Core2_size __attribute__((weak));
extern int Core3_size __attribute__((weak));
extern int Core4_size __attribute__((weak));
extern int Core5_size __attribute__((weak));
extern int Core6_size __attribute__((weak));
extern int Core7_size __attribute__((weak));
extern int Core8_size __attribute__((weak));
extern int Core9_size __attribute__((weak));
extern int Core10_size __attribute__((weak));
extern int Core11_size __attribute__((weak));
extern int Core12_size __attribute__((weak));
extern int Core13_size __attribute__((weak));
extern int Core14_size __attribute__((weak));
extern int Core15_size __attribute__((weak));

void sendStart(int cluster)
{
	if(cluster == __k1_get_cluster_id())
	{
		__builtin_k1_afdau(&sync[cluster], 1); 	/* post locally */
	}else{
		//printf("Cluster %d post to cluster %d addr %llx\n", __k1_get_cluster_id(), cluster, (uint64_t)(uintptr_t)(sync_remote_ptr[cluster]+__k1_get_cluster_id()));
		mppa_async_postadd(mppa_async_default_segment(cluster), sync_remote_ptr[cluster]+__k1_get_cluster_id()*sizeof(long long), 1); /* atomic remote increment of sync[cluster] value */
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
	int local_buffer_size = 0;
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
	for(i=0; i<NB_CLUSTER;i++){
		/* event &sync_evt[i] unlocks when sync[i] is GT 0 (kind of remote semaphore emulation) */
		//printf("Cluster %d evalcond addr %llx\n", __k1_get_cluster_id(), (uint64_t)(uintptr_t) &sync[i]);
		mppa_async_evalcond(&sync[i], 0, MPPA_ASYNC_COND_GT, &sync_evt[i]); /* GT = Greater Than */
	}
	extern int _start_async_copy;
	uintptr_t addr = (uintptr_t)&sync[0] - (uintptr_t)&_start_async_copy;
	//printf("Cluster %d send at %llx the addr %x\n", __k1_get_cluster_id(), (uint64_t) (uintptr_t) (SYNC_SHARED_ADDRESS+__k1_get_cluster_id()*sizeof(void*)), addr );
	mppa_async_put((void*)&addr, MPPA_ASYNC_DDR_0, SYNC_SHARED_ADDRESS+__k1_get_cluster_id()*sizeof(void*), sizeof(uintptr_t), NULL);
	mppa_async_fence(MPPA_ASYNC_DDR_0, NULL);
	mppa_rpc_barrier_all();
	mppa_async_get(sync_remote_ptr, MPPA_ASYNC_DDR_0, SYNC_SHARED_ADDRESS, NB_CLUSTER*sizeof(uintptr_t), NULL);
	mppa_rpc_barrier_all();
#if 0

	for(i=0;i<__k1_get_cluster_id();i++)
		mppa_rpc_barrier_all();

	for(i=0;i<NB_CLUSTER;i++){
		printf("Cluster %d got %d addr %llx\n", __k1_get_cluster_id(), i, (uint64_t)(uintptr_t)sync_remote_ptr[i]);
	}

	for(i=__k1_get_cluster_id();i<NB_CLUSTER;i++)
		mppa_rpc_barrier_all();
#endif
#if 0
	for(i=0; i<NB_CLUSTER;i++){
		sendStart(i);
	}

	printf("done send %d\n", __k1_get_cluster_id());
	for(i=0; i<NB_CLUSTER;i++){
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

	if(addr >= DDR_START){
		uint64_t start = __k1_read_dsu_timestamp();
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, s, &offset);
		mppa_async_get(l, &shared_segment, offset, n, NULL);
		__builtin_k1_afdau(&total_get_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);
	}
	
	int i;
	/* memset */
	for(i=0;i<n;i++)
		l[i] = (char)c;
	
	if(addr >= DDR_START){
		uint64_t start = __k1_read_dsu_timestamp();
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, s, &offset);
		mppa_async_put(l, &shared_segment, offset, n, NULL);
		__builtin_k1_afdau(&total_put_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);
	}
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


	int i;
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

	/* cluster -> ddr */
	if(dst_addr >= DDR_START && src_addr < DDR_START){
		uint64_t start = __k1_read_dsu_timestamp();
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, dest, &offset);
		mppa_async_put(src, &shared_segment, offset, n, NULL);
		__builtin_k1_afdau(&total_put_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);
	}

	/* ddr -> cluster */
	if(dst_addr < DDR_START && src_addr >= DDR_START){
		uint64_t start = __k1_read_dsu_timestamp();
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, dest, &offset);
		mppa_async_get(dest, &shared_segment, offset, n, NULL);
		__builtin_k1_afdau(&total_get_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - start);
	}

	/* ddr -> ddr */
	if(dst_addr >= DDR_START && src_addr >= DDR_START){
		uint64_t startg = __k1_read_dsu_timestamp();
		//printf("=====> local_buffer %llx src %llx dst %llx\n", (uint64_t)(uintptr_t)local_buffer, (uint64_t)(uintptr_t)src, (uint64_t)(uintptr_t)dest );		
		off64_t offset = 0;
		mppa_async_offset(&shared_segment, (void*)src, &offset);
		mppa_async_get(local_buffer, &shared_segment, offset, n, NULL);
		uint64_t startp = __k1_read_dsu_timestamp();
		__builtin_k1_afdau(&total_get_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - startg);
		mppa_async_offset(&shared_segment, dest, &offset);
		mppa_async_put(local_buffer, &shared_segment, offset, n, NULL);
		__builtin_k1_afdau(&total_put_cycles[__k1_get_cpu_id()], __k1_read_dsu_timestamp() - startp);
	}

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
