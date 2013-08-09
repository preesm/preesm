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

	// Because MSMC memory cannot be non-cacheable, a new entry is added to
	// the memory map as follows:
	//     name            origin    length
	// MSMCSRAM_NO_CACHE     a1000000   00300000
	// Cache is disabled for this new memory range
	// Then a new section is created for this memory range (cf .cfg). Memory
	// translation is then used to make this region fall into the MSMCSRAM.
	// "translate" 2MB (0x14) from 0xa1000000 to 0x00c100000 using the MPAX number 3
	set_MPAX(3, 0xa1000, 0x00c100, 0x14, 0);
	// "translate" 1MB (0x13) from 0xa1200000 to 0x00c3ff000 using the MPAX number 4
	set_MPAX(4, 0xa1000, 0x00c300, 0x13, 0);


	BIOS_start();

	return (0);
}

