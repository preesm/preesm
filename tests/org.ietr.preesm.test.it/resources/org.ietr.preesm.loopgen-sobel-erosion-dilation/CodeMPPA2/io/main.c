/*
 * Copyright (C) 2016 Kalray SA.
 *
 * All rights reserved.
 */

#include <stdio.h>
#include <stdlib.h>
#include "mppa_boot_args.h"
#include <mppa_power.h>
#include <assert.h>
#include "mppa_bsp.h"
#include <utask.h>
#include <pcie_queue.h>
#include <mppa_rpc.h>
#include <mppa_remote.h>
#include <mppa_async.h>
#include <HAL/hal/board/boot_args.h>
#include <cluster/preesm_primitives/preesm.h>

static utask_t t;
static mppadesc_t pcie_fd = 0;

int
main(int argc __attribute__ ((unused)), char *argv[] __attribute__ ((unused)))
{
	int id;
	int j;
	int ret ;

	if(__k1_spawn_type() == __MPPA_PCI_SPAWN){
		#if 1
		long long *ptr = (void*)(uintptr_t)Shared;
		long long i;
		for(i=0;i<(long long)((1<<30ULL)/sizeof(long long));i++)
		{
			ptr[i] = -1LL;
		}
		__builtin_k1_wpurge();
		__builtin_k1_fence();
		mOS_dinval();
		#endif
	}

	if (__k1_spawn_type() == __MPPA_PCI_SPAWN) {
		pcie_fd = pcie_open(0);
			ret = pcie_queue_init(pcie_fd);
			pcie_register_console(pcie_fd, stdin, stdout);
		assert(ret == 0);
	}

#ifdef VERBOSE	
	printf("Hello IO\n");
#endif

	mppa_rpc_server_init(	1 /* rm where to run server */, 
							0 /* offset ddr */, 
							NB_CLUSTER /* nb_cluster to serve*/);
	mppa_async_server_init();
	mppa_remote_server_init(pcie_fd, NB_CLUSTER);
	
	
	for( j = 0 ; j < NB_CLUSTER ; j++ ) {

		char elf_name[30];
		sprintf(elf_name, "cluster%d_bin", j);
#ifdef VERBOSE
		printf("Load cluster %d with elf %s\n", j, elf_name);
#endif
		id = mppa_power_base_spawn(j, elf_name, NULL, NULL, MPPA_POWER_SHUFFLING_ENABLED);
		if (id < 0)
			return -2;
	}

	utask_create(&t, NULL, (void*)mppa_rpc_server_start, NULL);

	mppa_async_segment_t shared_segment;
	mppa_async_segment_create(&shared_segment, SHARED_SEGMENT_ID, (void*)(uintptr_t)Shared, 1024*1024*1024, 0, 0, NULL);

#ifdef VERBOSE
	printf("Waiting for cluster exit \n");
#endif

	int err;
	for( j = 0 ; j < NB_CLUSTER ; j++ ) {
	    mppa_power_base_waitpid (j, &err, 0);
	}

	if (__k1_spawn_type() == __MPPA_PCI_SPAWN) {
		pcie_queue_barrier(pcie_fd, 0, &ret);
		pcie_queue_exit(pcie_fd, ret, NULL);
	}
	return 0;
}
