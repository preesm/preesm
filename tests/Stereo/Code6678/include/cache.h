/*
 ============================================================================
 Name        : cache.h
 Author      : kdesnos
 Version     : 1.1
 Copyright   : CECILL-C
 Description : Parameter to configure the cache.
 ============================================================================
 */

#ifndef CACHE_H
#define CACHE_H

#include <ti/csl/csl_cacheAux.h>
#include <xdc/runtime/System.h>

// SET Cacheable to 1 to activate caches
// 0 otherwise
#define CACHEABLE 0

// Activate Cache_level to L1 or L2
#define L1
//#define L2

#ifndef L2
#ifndef L1
// NO cache is activated
#define CACHE_LINE_SIZE 0
#define cache_wbInv(buffer,size)
#define cache_wb(buffer,size)
#define cache_inv(buffer,size)

#else
// L1 is activated
#define CACHE_LINE_SIZE CACHE_L1D_LINESIZE
#define cache_wbInv(buffer,size) 	cache_wbInvL1D(buffer,size)
#define cache_wb(buffer,size) 		cache_wbL1D(buffer,size)
#define cache_inv(buffer,size)		cache_invL1D(buffer,size)

#endif
#else

// L2 is activated
#define CACHE_LINE_SIZE CACHE_L2_LINESIZE
#define cache_wbInv(buffer,size) 	cache_wbInvL2(buffer,size)
#define cache_wb(buffer,size) 		cache_wbL2(buffer,size)
#define cache_inv(buffer,size)		cache_invL2(buffer,size)
#endif


// cf Advisory 7 from sprz334f

/**
 * L1D Write-back invalidate operation.
 * This function implements the Advisory 7 from sprz334f to solve cache
 * coherency problem.
 * @see CACHE_wbInvL1D for more information.
 */
void cache_wbInvL1D(void* buffer, Uint32 size);

/**
 * L1D Write-back operation.
 * This function implements the Advisory 7 from sprz334f to solve cache
 * coherency problem.
 * @see CACHE_wbL1D for more information.
 */
void cache_wbL1D(void* buffer, Uint32 size);

/*!
 * L1D Invalidate operation.
 * This function implements the Advisory 7 from sprz334f to solve cache
 * coherency problem.
 * @see CACHE_invL1D for more information.
 */
void cache_invL1D(void* buffer, Uint32 size);

/**
 * L2 Write-back invalidate operation.
 * This function implements the Advisory 7 from sprz334f to solve cache
 * coherency problem.
 * @see CACHE_wbInvL2 for more information.
 */
void cache_wbInvL2(void* buffer, Uint32 size);

/**
 * L2 Write-back operation.
 * This function implements the Advisory 7 from sprz334f to solve cache
 * coherency problem.
 * @see CACHE_wbL2 for more information.
 */
void cache_wbL2(void* buffer, Uint32 size);

/**
 * L2 invalidate operation.
 * This function implements the Advisory 7 from sprz334f to solve cache
 * coherency problem.
 * @see CACHE_invL2 for more information.
 */
void cache_invL2(void* buffer, Uint32 size);

#endif
