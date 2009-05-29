#include "ProtectedSharedMemory.h"

#include <windows.h>
#include <stdio.h>
#include <conio.h>
#include <tchar.h>

TCHAR szFileName[]=TEXT("Global\\MyFileMappingObject");
TCHAR szMsg[]=TEXT("Message from first process.");
TCHAR szMutexName[]=TEXT("NameOfMutexObject");


void ProtectedSharedMemory::createMutex()
{
    hMutex = CreateMutex( 
        NULL,                        // default security descriptor
        FALSE,                       // mutex not owned
        TEXT("IETRJobPosting"));  // object name

    if (hMutex == NULL) 
        printf("CreateMutex error: %d\n", GetLastError() ); 
    else 
        if ( GetLastError() == ERROR_ALREADY_EXISTS ) 
            printf("CreateMutex opened an existing mutex\n"); 
        else printf("CreateMutex created a new mutex.\n");
}

void ProtectedSharedMemory::connectMutex()
{
    hMutex = OpenMutex( 
        MUTEX_ALL_ACCESS,            // request full access
        FALSE,                       // handle not inheritable
        TEXT("IETRJobPosting"));  // object name

    if (hMutex == NULL) 
        printf("OpenMutex error: %d\n", GetLastError() );
    else printf("OpenMutex successfully opened the mutex.\n");
}

int ProtectedSharedMemory::createMem(int size)
{

   this->hMapFile = CreateFileMapping(
                 INVALID_HANDLE_VALUE,		// use paging file
                 NULL,						// default security 
                 PAGE_READWRITE,			// read/write access
                 0,							// max. object size 
                 size,						// buffer size  
                 szFileName);               // name of mapping object
 
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

ProtectedSharedMemory::ProtectedSharedMemory(int size)
{
	LPTSTR lpszPipename = _T("\\\\.\\pipe\\ProtectedSharedMemory"); 
    DWORD dw = GetLastError();
	memoryPointer = NULL;

	if(size != 0){
		createMutex();
		createMem(size);
		write((void*)szMsg,0,10);
	}
	else{
		connectMutex();
		connectMem();
		TCHAR message[712];
		if(read((void*)message,0,8)){
			MessageBox(NULL, (LPCTSTR)message, TEXT("Process2"), MB_OK);
		}
	}
}

ProtectedSharedMemory::~ProtectedSharedMemory()
{
   //_getch();
   UnmapViewOfFile(memoryPointer);
   CloseHandle(hMapFile);
}

int ProtectedSharedMemory::read(void* buffer, int offset, int size)
{
	char* charMemoryPointer =  (char*)(memoryPointer);
	charMemoryPointer += offset;

	if(memoryPointer != NULL){
		CopyMemory(buffer, (PVOID)charMemoryPointer, size);
		return 1;
	}

	return 0;
}

void ProtectedSharedMemory::write(void* buffer, int offset, int size)
{
	char* charMemoryPointer =  (char*)(memoryPointer);
	charMemoryPointer += offset;

	if(memoryPointer != NULL){
		DWORD tutu = WaitForSingleObject(hMutex, INFINITE);

		// access the resource
		CopyMemory((PVOID)(charMemoryPointer), buffer, size);

		ReleaseMutex(hMutex);
	}
}

