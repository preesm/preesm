/*
 * Copyright (C) 2016 Kalray SA.
 *
 * All rights reserved.
 */
#include <pcie.h>

//#define VERBOSE

int
main(int argc, char **argv)
{

	mppadesc_t fd = pcie_open_device(0);
	/* check for correct number of arguments */
	if (argc < 3) {
		printf("Error, no multibinary provided to host executatble\n");
		return -1;
	}
#ifdef VERBOSE
	printf("# [HOST] %s : load %s file\n", __func__, argv[1]);
#endif
	/* load on the MPPA the k1 multi-binary */
	pcie_load_io_exec_args_mb(fd, argv[1], argv[2], NULL, 0, PCIE_LOAD_FULL);

	//pcie_load_io_exec(fd, "ddr_paging");

	pcie_queue_init(fd);
	pcie_register_console(fd, stdin, stdout);

	int status;
	pcie_queue_barrier(fd, 0, &status);

	pcie_queue_exit(fd, 0, &status);
#ifdef VERBOSE
	printf("# [HOST] MPPA exited with status %d\n", status);
#endif
	return status;
}
