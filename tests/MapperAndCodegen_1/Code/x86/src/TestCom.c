/*
 ============================================================================
 Name        : TestCom.c
 Author      : mpelcat
 Version     :
 Copyright   : Calling a PREESM generated code
 Description :
 ============================================================================
 */
#include "x86.h"

DWORD WINAPI ThreadFunc( LPVOID lpParam )
{
	printf("test\n");
    return 0;
}

void test(void)
{
	LPTHREAD_START_ROUTINE routine[8];

	routine[0] = ThreadFunc;

    int stacksize = 8000;
    CreateThread(NULL,stacksize,routine[0],NULL,0,NULL);

	printf("finished\n");
}

void main(void)
{
	test();
    system("PAUSE");
}
