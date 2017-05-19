/*
 ============================================================================
 Name        : dump.c
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CECILL-C
 Description :
 ============================================================================
 */

#include <ti/csl/csl_cacheAux.h>
#include <xdc/runtime/System.h>
#include <xdc/runtime/Timestamp.h>

void dumpTime(int id, int* dumpBuffer) {
	dumpBuffer[id] = Timestamp_get32();
	CACHE_wbInvL2(dumpBuffer + id, sizeof(int), CACHE_WAIT);
}

void initNbExec(int* nbExec, int nbDump) {
	int i = 0;

	for (i = 1; i < nbDump; i++) {
		//*(nbExec+i) = 1;
		System_printf("%d;", i);
	}
	System_printf("\n");
}

void writeTime(int* dumpBuffer, int nbDump, int* nbExec) {
	int i;

	CACHE_invL2(dumpBuffer, nbDump * sizeof(int), CACHE_WAIT);

	for (i = 1; i < nbDump; i++) {
		int res;
		res = dumpBuffer[i] - dumpBuffer[i - 1];
		System_printf("%d;", res);
	}
	System_printf("\n");

}

