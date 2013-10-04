/* Standard headers */
#include <ti/syslink/Std.h>
#include <ti/syslink/ProcMgr.h>
#include <unistd.h>

#if defined (__cplusplus)
extern "C" {
#endif /* defined (__cplusplus) */

/** ============================================================================
 *  Functions
 *  ============================================================================
 */
/*!
 *  @brief  Function to execute the startup for ProcMgrApp sample application
 */
Int ProcMgrApp_startup (UInt16 procId);


/*!
 *  @brief  Function to execute the shutdown for ProcMgrApp sample application
 */
Int ProcMgrApp_shutdown (UInt16 procId);

#if defined (__cplusplus)
}
#endif /* defined (__cplusplus) */
