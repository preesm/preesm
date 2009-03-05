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
	printf("test");
    return 0;
}

void main(void)
{
    DWORD dwThreadId, dwThrdParam = 1;
    HANDLE hThread;

    hThread = CreateThread(
        NULL,                        // attribut de securité par defaut
        0,                           // taille de la pile par defaut
        ThreadFunc,                  // notre function
        &dwThrdParam,                // l'argument pour la fonction
        0,                           // flag de creation par defaut
        &dwThreadId);                // retourne l'id du thread

    system("PAUSE");
}
