#include <ti/sysbios/knl/Task.h>
#include <ti/sysbios/BIOS.h>

#include <xdc/runtime/System.h>

#include <ti/ipc/MultiProc.h>
/*  ----------------------------------- To get globals from .cfg Header */
#include <xdc/cfg/global.h>

/*  ----------------------------------- cache management */
#include <ti/csl/csl_xmcAux.h>
#include <ti/csl/csl_cacheAux.h>
#include <ti/csl/csl_semAux.h>

#include "utils.h"
#include "communication.h"

void set_MPAX(int index, Uint32 bAddr, Uint32 rAddr, Uint8 segSize,
		Bool cacheable) {

	if (!cacheable) {
		// TODO need to check if several MPAX are covered by this address and size
		CACHE_disableCaching(bAddr >> 12);
	}

	CSL_XMC_XMPAXH mpaxh;
	mpaxh.bAddr = bAddr;
	mpaxh.segSize = segSize;

	CSL_XMC_XMPAXL mpaxl;
	mpaxl.rAddr = rAddr;
	mpaxl.sr = 1;
	mpaxl.sw = 1;
	mpaxl.sx = 1;
	mpaxl.ur = 1;
	mpaxl.uw = 1;
	mpaxl.ux = 1;

	CSL_XMC_setXMPAXH(index, &mpaxh);
	CSL_XMC_setXMPAXL(index, &mpaxl);
}
