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
