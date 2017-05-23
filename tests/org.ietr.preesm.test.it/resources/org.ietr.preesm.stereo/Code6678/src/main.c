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
#include "cache.h"

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

	// Disable caching from 0x80000000 to 0xFFFFFFFF
	if(!CACHEABLE){
		int index;
		for (index = 0x80; index <= 0xFF; index++) {
			CACHE_disableCaching(index);
		}
	}

	BIOS_start();

	return (0);
}

