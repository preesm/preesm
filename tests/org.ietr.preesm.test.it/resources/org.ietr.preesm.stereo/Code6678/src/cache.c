/*
 * cache.c
 *
 *  Created on: 7 nov. 2013
 *      Author: kdesnos
 */


#include <cache.h>
#include <ti/csl/csl_xmcAux.h>
#include <ti/sysbios/hal/Hwi.h>

#define cacheOperationCode(buffer,size,call)  		\
	Uint32 key;								  		\
	Uint32 rest = size;						  		\
											  		\
    /* Process at most 65535*4 bytes at a time */	\
    while(rest>0){									\
    	Uint32 processed = (rest > 65535*4)? 		\
    							65535*4 : rest;		\
    												\
													\
		/* Disable Interrupts */				  	\
		key = Hwi_disable(); 					  	\
													\
		/*  Cleanup the prefetch buffer also.*/  	\
		CSL_XMC_invalidatePrefetchBuffer();       	\
													\
		/* Invalidate the cache. */               	\
		call((char*)buffer+(size-rest),				\
				processed, CACHE_FENCE_WAIT);		\
													\
		asm (" nop  4");                          	\
		asm (" nop  4");                          	\
		asm (" nop  4");                          	\
		asm (" nop  4");                          	\
													\
		/* Reenable Interrupts.*/                 	\
		Hwi_restore(key);							\
        rest = rest-processed;	                    \
    }												\

void cache_wbInvL1D(void* buffer,Uint32 size){
	cacheOperationCode(buffer,size,CACHE_wbInvL1d);
}

void cache_wbL1D(void* buffer, Uint32 size){
	cacheOperationCode(buffer,size,CACHE_wbL1d);
}

void cache_invL1D(void* buffer, Uint32 size){
	cacheOperationCode(buffer,size,CACHE_invL1d);
}

void cache_wbInvL2(void* buffer,Uint32 size){
	cacheOperationCode(buffer,size,CACHE_wbInvL2);
}

void cache_wbL2(void* buffer, Uint32 size){
	cacheOperationCode(buffer,size,CACHE_wbL2);
}

void cache_invL2(void* buffer, Uint32 size){
	cacheOperationCode(buffer,size,CACHE_invL2);
}


