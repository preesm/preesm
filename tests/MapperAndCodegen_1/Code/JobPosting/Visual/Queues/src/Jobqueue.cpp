#include "jobQueue.h"

#define BUFSIZE 1024
#define PIPE_TIMEOUT 5000


void printLastMessage(){
	LPCWSTR lpMsgBuf;
	FormatMessage(
		FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL,
		GetLastError(),
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
		(LPTSTR) &lpMsgBuf, 0, NULL );// Display the string.
	::MessageBox( NULL, lpMsgBuf, _T("GetLastError"), MB_OK|MB_ICONINFORMATION );
	LocalFree( lpMsgBuf );
	::MessageBox ( NULL, _T("Le tube de communication n'a pas pu être créé"),_T("Erreur"),MB_ICONERROR |MB_OK) ;
	PostQuitMessage (0) ;
}

JobQueue::JobQueue()
{
}

JobQueue::JobQueue(string type)
{
	LPTSTR lpszPipename = LPTSTR("\\\\.\\pipe\\jobQueuePipe"); 
    DWORD dw = GetLastError();

	if(type == "master"){
		this->namedPipe = CreateNamedPipe (_T("\\\\.\\pipe\\jobQueuePipe"),
PIPE_ACCESS_OUTBOUND | FILE_FLAG_WRITE_THROUGH,
PIPE_TYPE_MESSAGE | PIPE_WAIT,
1,BUFSIZE,BUFSIZE,PIPE_TIMEOUT,NULL) ;

if (this->namedPipe == INVALID_HANDLE_VALUE)
{
	printLastMessage();
}


	}
	else{
		this->namedPipe = CreateFile (
			lpszPipename, // Nom du tube
			GENERIC_READ, // Mode d'accès (lecture/écriture)
			// GENERIC_READ si le tube a été créé avec PIPE_ACCESS_OUTBOUND
			//GENERIC_WRITE si le tube a été créé avec PIPE_ACCESS_INBOND
			// GENERIC_WRITE | GENERIC_READ si duplex
			0, // 0 pour interdire le partage du tube avec d'autres processus
			NULL, // Privilège d'accès
			OPEN_EXISTING, // OPEN_EXISTING puisqu'on ouvre un tube existant
			FILE_FLAG_WRITE_THROUGH, //FILE_FLAG_WRITE_THROUGH pour une écriture synchrone
			NULL // Pas de signification pour les tubes
		);
	}
}