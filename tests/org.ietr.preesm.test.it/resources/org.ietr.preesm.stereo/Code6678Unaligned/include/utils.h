/*
 ============================================================================
 Name        : utils.h
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CECILL-C
 Description : Utilitary methods for C6x code
 ============================================================================
 */

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

void set_MPAX(int index, Uint32 bAddr, Uint32 rAddr, Uint8 segSize,
		Bool cacheable);

int endExecution();

/**
 * This macro can be used to store a float in an unaligned float array.
 *
 * Using this macro on aligned array may decrease the performance of your
 * memory accesses by a factor 2.
 *
 * @param[out] dest
 * Pointer to the unaligned destination where to store the float.
 * @param[in] src
 * Pointer to the *ALIGNED* source float.
 */
#define STORE_FLOAT(dest,src) _mem4(dest) = *((int*)src)

/**
 * This macro can be used to store a int in an unaligned int array.
 *
 * Using this macro on aligned array may decrease the performance of your
 * memory accesses by a factor 2.
 *
 * @param[out] dest
 * Pointer to the unaligned destination where to store the int.
 * @param[out] dest
 * Pointer to the *ALIGNED* source int.
 */
#define STORE_INT(dest,src) _mem4(dest) = *src

/**
 * This macro can be used to load a float from an unaligned float array.
 *
 * Using this macro on aligned array may decrease the performance of your
 * memory accesses by a factor 2.
 *
 * @param[in] src
 * Pointer to the unaligned source float.
 */
#define LOAD_FLOAT(src) *((float*)&(_mem4(src)))

/**
 * This macro can be used to load a int from an unaligned int array.
 *
 * Using this macro on aligned array may decrease the performance of your
 * memory accesses by a factor 2.
 *
 * @param[in] src
 * Pointer to the unaligned source int.
 */
#define LOAD_INT(src) _mem4(src)

#endif
