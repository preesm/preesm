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
/* Standard headers */
#include <ti/syslink/Std.h>

/* OSAL & Utils headers */
#include <ti/syslink/utils/Trace.h>
#include <ti/syslink/utils/OsalPrint.h>
#include <ti/syslink/utils/String.h>
#include <ti/syslink/IpcHost.h>

/* Module level headers */
#include <ti/syslink/ProcMgr.h>
#include <ti/ipc/MultiProc.h>


#if defined (__cplusplus)
extern "C" {
#endif /* defined (__cplusplus) */


/** ============================================================================
 *  Globals
 *  ============================================================================
 */
#define NUM_ARGS 1
#define NULL (void*)0

#define SILENT 1

#if SILENT==0
#define PRINTF Osal_printf
#else
#define PRINTF(a,...)
#endif

/** ============================================================================
 *  Functions
 *  ============================================================================
 */
/*!
 *  @brief  Function to execute the startup for ProcMgrApp sample application
 */
Int
ProcMgrApp_startup (UInt16 procId)
{
    Int                          status = 0;
    ProcMgr_Handle               handle = NULL;
    ProcMgr_AttachParams         attachParams;
    ProcMgr_State                state;
    UInt32 resetVector = 0x00800000;

    PRINTF ("Entered ProcMgrApp_startup\n");

    status = ProcMgr_open (&handle, procId);

    if (status >= 0) {
        ProcMgr_getAttachParams (NULL, &attachParams);
        /* Default params will be used if NULL is passed. */

        attachParams.bootMode = ProcMgr_BootMode_NoBoot;
        /* set the boot mode */
        status = ProcMgr_attach (handle, &attachParams);
        if (status < 0) {
        	PRINTF ("ProcMgr_attach failed [0x%x]\n", status);
        }
        else {
            PRINTF ("ProcMgr_attach status: [0x%x]\n", status);
            state = ProcMgr_getState (handle);
            PRINTF ("After attach: ProcMgr_getState\n"
                         "    state [0x%x]\n",
                         state);

			/* Call Ipc_control for ProcMgr_BootMode_NoLoad_Pwr, ProcMgr_BootMode_NoLoad_NoPwr
			   and ProcMgr_BootMode_NoBoot modes */
			status = Ipc_control (procId,
								  Ipc_CONTROLCMD_LOADCALLBACK,
								  (Ptr)&resetVector);

            if (status < 0) {
                PRINTF ("Error in Ipc_control "
                             "Ipc_CONTROLCMD_LOADCALLBACK [0x%x]\n",
                             status);
            }
            else {
                state = ProcMgr_getState (handle);
                PRINTF ("After Ipc_loadcallback: ProcMgr_getState\n"
                             "    state [0x%x]\n",
                             state);
            }
        }

        if (status >= 0) {

            status = Ipc_control (procId,
                                  Ipc_CONTROLCMD_STARTCALLBACK,
                                  NULL);
            if (status < 0) {
                PRINTF ("Error in Ipc_control "
                             "Ipc_CONTROLCMD_STARTCALLBACK[0x%x]\n",
                             status);
            }
            else {
                state = ProcMgr_getState (handle);
                PRINTF ("After Ipc_startcallback: ProcMgr_getState\n"
                             "    state [0x%x]\n",
                             state);
            }
        }

        status = ProcMgr_close (&handle);
        PRINTF ("ProcMgr_close status: [0x%x]\n", status);
    }

    PRINTF ("Leaving ProcMgrApp_startup\n");

    return 0;
}


/*!
 *  @brief  Function to execute the shutdown for ProcMgrApp sample application
 */
Int
ProcMgrApp_shutdown (UInt16 procId)
{
    Int                          status = 0;
    ProcMgr_Handle               handle = NULL;
    ProcMgr_State                state;

    PRINTF ("Entered ProcMgrApp_shutdown\n");

    status = ProcMgr_open (&handle, procId);

    if (status >= 0) {
        status = Ipc_control (procId, Ipc_CONTROLCMD_STOPCALLBACK, NULL);
        PRINTF ("Ipc_control Ipc_CONTROLCMD_STOPCALLBACK status: [0x%x]\n",
                     status);

        status = ProcMgr_detach (handle);
        PRINTF ("ProcMgr_detach status: [0x%x]\n", status);

        state = ProcMgr_getState (handle);
        PRINTF ("After detach: ProcMgr_getState\n"
                     "    state [0x%x]\n",
                     state);

        status = ProcMgr_close (&handle);
        PRINTF ("ProcMgr_close status: [0x%x]\n", status);
    }

    PRINTF ("Leaving ProcMgrApp_shutdown\n");

    return 0;
}


#if defined (__cplusplus)
}
#endif /* defined (__cplusplus) */
