#ifndef UTILS_H
#define UTILS_H

#include <xdc/std.h>
/**
 * Sets the MPAX register for the indexed segment.</br>
 * The segment is configured to be readable and writable by users and
 * supervisors
 *
 * @param[in] index
 * The index of the segment used for this translation.
 * @param[in] bAddr
 * The virtual address used to access memory
 * @param[in] rAddr
 * The real address accessed when using the virtual address
 * @param[in] segSize
 * Code for the size of the translated address range.
 * @param[in] cacheable
 * Whether the segment should be cacheable or not
 *
 * @see sprugw0c for more information on the parameters
 */
extern Bool isEnded[1];

void set_MPAX(int index, Uint32 bAddr, Uint32 rAddr, Uint8 segSize, Bool cacheable);

int endExecution();

#endif
