/*
	============================================================================
	Name        : cache.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Parameter to configure the cache.
	============================================================================
*/

#ifndef CACHE_H
#define CACHE_H

#include <ti/csl/csl_cacheAux.h>

// SET Cacheable to 1 to activate caches
// 0 otherwise
#define CACHEABLE 0

// Activate Cache_level to L1 or L2
//#define L1
//#define L2

#ifndef L2
#ifndef L1
// NO cache is activated
#define cache_wbInv(buffer,size)
#define cache_wb(buffer,size)
#define cache_inv(buffer,size)

#else
// L1 is activated
#define cache_wbInv(buffer,size) 	CACHE_wbInvL1d(buffer,size, CACHE_WAIT)
#define cache_wb(buffer,size) 		CACHE_wbL1d(buffer,size, CACHE_WAIT)
#define cache_inv(buffer,size)		CACHE_invL1d(buffer,size, CACHE_WAIT)

#endif
#else

// L2 is activated
#define cache_wbInv(buffer,size) 	CACHE_wbInvL2(buffer,size, CACHE_WAIT)
#define cache_wb(buffer,size) 		CACHE_wbL2(buffer,size, CACHE_WAIT)
#define cache_inv(buffer,size)		CACHE_invL2(buffer,size, CACHE_WAIT)
#endif

#endif
