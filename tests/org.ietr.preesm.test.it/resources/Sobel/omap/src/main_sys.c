/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2012)
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
/*
 *  ======== MessageQ_MPUSYS_Test_Core0.c ========
 *
 */

#include <xdc/std.h>

/*  -----------------------------------XDC.RUNTIME module Headers    */
#include <xdc/runtime/Memory.h>
#include <xdc/runtime/System.h>
#include <xdc/runtime/IHeap.h>

/*  ----------------------------------- IPC module Headers           */
#include <ti/ipc/MultiProc.h>
#include <ti/ipc/Notify.h>
#include <ti/ipc/Ipc.h>
#include <ti/ipc/GateMP.h>
#include <ti/ipc/MessageQ.h>
#include <ti/ipc/HeapBufMP.h>

/*  ----------------------------------- BIOS6 module Headers         */
#include <ti/sysbios/knl/Semaphore.h>
#include <ti/sysbios/syncs/SyncSem.h>
#include <ti/sysbios/BIOS.h>

/*  ----------------------------------- To get globals from .cfg Header */
#include <xdc/cfg/global.h>

#include <com.h>

int Semaphores_init(Semaphore_Handle *sem, int number){
	int i;
	for(i=0; i<number; i++){
		sem[i] = Semaphore_create(0, NULL, NULL);
	}
	return 0;
}

void computationThread(void);

/*
 *  ======== main ========
 */
Int main(Int argc, Char* argv[]){
    /* Set up interprocessor notifications */
    Ipc_start();
    BIOS_start();
    return (0);
}

/*
 *  ======== tsk1_func ========
 *  Receive and return messages until the die request comes.
 */
Void tsk1_func(UArg arg0, UArg arg1){
	UInt16 hostProcId = MultiProc_getId("MPU");
	while (Ipc_attach(hostProcId) < 0);
	UInt16 appProcId = MultiProc_getId("AppM3");
	while (Ipc_attach(appProcId) < 0);

	comInitCoPro();
	computationThread();
	comClear();
	
	Ipc_detach(appProcId);
	Ipc_detach(hostProcId);
}

/*
 *  @(#) ti.sdo.ipc.samples; 1, 0, 0, 0,238; 6-11-2009 16:20:18; /db/vtree/library/trees/ipc/ipc-b48x/src/
 */
