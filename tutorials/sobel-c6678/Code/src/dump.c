/*
	============================================================================
	Name        : dump.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include <xdc/runtime/System.h>
#include <xdc/runtime/Timestamp.h>

void dumpTime(int id, int* dumpBuffer){
    dumpBuffer[id] = Timestamp_get32();
}

void initNbExec(int* nbExec, int nbDump){
    int i = 0;

    for(i=1; i<nbDump; i++){
        *(nbExec+i) = 1;
        System_printf("%d;",i);
    }
    System_printf("\n");
}

void writeTime(int* dumpBuffer, int nbDump, int* nbExec){
    int i ;

	for(i=1;i< nbDump;i++){
		int res;
		res = dumpBuffer[i]-dumpBuffer[i-1];
		System_printf("%d;",res);
	}
	System_printf("\n");


}

