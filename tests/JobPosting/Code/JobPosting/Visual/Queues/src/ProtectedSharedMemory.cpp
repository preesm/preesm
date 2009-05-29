#include "ProtectedSharedMemory.h"

#include <windows.h>
#include <stdio.h>
#include <conio.h>
#include <tchar.h>

#define BUF_SIZE 100000
#define PIPE_TIMEOUT 5000

TCHAR szFileName[]=TEXT("Global\\MyFileMappingObject");
TCHAR szMsg[]=TEXT("Message from first process.");
TCHAR szMutexName[]=TEXT("NameOfMutexObject");


void ProtectedSharedMemory::createMutex()
{
    HANDLE hMutex; 

    hMutex = CreateMutex( 
        NULL,                        // default security descriptor
        FALSE,                       // mutex not owned
        TEXT("NameOfMutexObject"));  // object name

    if (hMutex == NULL) 
        printf("CreateMutex error: %d\n", GetLastError() ); 
    else 
        if ( GetLastError() == ERROR_ALREADY_EXISTS ) 
            printf("CreateMutex opened an existing mutex\n"); 
        else printf("CreateMutex created a new mutex.\n");
}

void ProtectedSharedMemory::connectMutex()
{
    HANDLE hMutex; 

    hMutex = OpenMutex( 
        MUTEX_ALL_ACCESS,            // request full access
        FALSE,                       // handle not inheritable
        TEXT("NameOfMutexObject"));  // object name

    if (hMutex == NULL) 
        printf("OpenMutex error: %d\n", GetLastError() );
    else printf("OpenMutex successfully opened the mutex.\n");
}

int ProtectedSharedMemory::createMem()
{

   this->hMapFile = CreateFileMapping(
                 INVALID_HANDLE_VALUE,    // use paging file
                 NULL,                    // default security 
                 PAGE_READWRITE,          // read/write access
                 0,                       // max. object size 
                 BUF_SIZE,                // buffer size  
                 szFileName);                 // name of mapping object
 
   if (hMapFile == NULL) 
   { 
      _tprintf(TEXT("Could not create file mapping object (%d).\n"), 
             GetLastError());
      return 1;
   }
   memoryPointer = (LPTSTR) MapViewOfFile(hMapFile,   // handle to map object
                        FILE_MAP_ALL_ACCESS, // read/write permission
                        0,                   
                        0,                   
                        BUF_SIZE);           
 
   if (memoryPointer == NULL) 
   { 
      _tprintf(TEXT("Could not map view of file (%d).\n"), 
             GetLastError()); 

	   CloseHandle(hMapFile);

      return 1;
   }

   return 0;
}

int ProtectedSharedMemory::connectMem()
{

   hMapFile = OpenFileMapping(
                   FILE_MAP_ALL_ACCESS,   // read/write access
                   FALSE,                 // do not inherit the name
                   szFileName);               // name of mapping object 
 
   if (hMapFile == NULL) 
   { 
      _tprintf(TEXT("Could not open file mapping object (%d).\n"), 
             GetLastError());
      return 1;
   } 
 
   memoryPointer = (LPTSTR) MapViewOfFile(hMapFile, // handle to map object
               FILE_MAP_ALL_ACCESS,  // read/write permission
               0,                    
               0,                    
               BUF_SIZE);                   
 
   if (memoryPointer == NULL) 
   { 
      _tprintf(TEXT("Could not map view of file (%d).\n"), 
             GetLastError()); 

	  CloseHandle(hMapFile);

	  memoryPointer = NULL;
      return 1;
   }
 
   return 0;
}

ProtectedSharedMemory::ProtectedSharedMemory(int create)
{
	LPTSTR lpszPipename = _T("\\\\.\\pipe\\ProtectedSharedMemory"); 
    DWORD dw = GetLastError();
	memoryPointer = NULL;

	if(create == 1){
		createMutex();
		createMem();
		write((void*)szMsg,0,10);
	}
	else{
		connectMutex();
		connectMem();
		read(NULL,2,0);
	}
}

ProtectedSharedMemory::~ProtectedSharedMemory()
{
   _getch();
   UnmapViewOfFile(memoryPointer);
   CloseHandle(hMapFile);
}

void ProtectedSharedMemory::read(void* buffer, int offset, int size)
{
	char* charMemoryPointer =  (char*)(memoryPointer);
	charMemoryPointer += offset;

	if(memoryPointer != NULL){
		MessageBox(NULL, (LPCTSTR)(charMemoryPointer), TEXT("Process2"), MB_OK);
	}
}

void ProtectedSharedMemory::write(void* buffer, int offset, int size)
{
	char* charMemoryPointer =  (char*)(memoryPointer);
	charMemoryPointer += offset;

	if(memoryPointer != NULL){
		CopyMemory((PVOID)(charMemoryPointer), buffer, size);
	}
}

