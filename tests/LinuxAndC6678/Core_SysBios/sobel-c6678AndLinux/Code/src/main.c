/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
#include <xdc/std.h>
#include <string.h>

/*  -----------------------------------XDC.RUNTIME module Headers    */
#include <xdc/runtime/System.h>

/*  ----------------------------------- BIOS6 module Headers         */
#include <ti/sysbios/BIOS.h>
#include <ti/sysbios/knl/Task.h>

/*  ----------------------------------- IPC module Headers           */
#include <ti/ipc/MultiProc.h>

#include "utils.h"
#include "cores.h"
#include <ti/csl/csl_cacheAux.h>
#include "communication.h"
#include <ti/ipc/Ipc.h>

Void tsk0_func(UArg arg0, UArg arg1) {

	switch (MultiProc_self()) {
#ifdef CORE0
	case 0:
		core0();
		break;
#endif

#ifdef CORE1
	case 1:
		core1();
		break;
#endif

#ifdef CORE2
	case 2:
		core2();
		break;
#endif

#ifdef CORE3
	case 3:
		core3();
		break;
#endif

#ifdef CORE4
	case 4:
		core4();
		break;
#endif

#ifdef CORE5
	case 5:
		core5();
		break;
#endif

#ifdef CORE6
	case 6:
		core6();
		break;
#endif

#ifdef CORE7
	case 7:
		core7();
		break;
#endif
	default:
		communicationInit();
		while (1) {
			busy_barrier();
		}
		//break;
	}

	System_printf("The test is complete\n");
	BIOS_exit(0);
}

Int main(Int argc, Char* argv[]) {

	// Disable caching from 0x92000000 to 0xFFFFFFFF
	if(!CACHEABLE){
		int index;
		for (index = 0x92; index <= 0xFF; index++) {
			CACHE_disableCaching(index);
		}
	}

	BIOS_start();

	return (0);
}

