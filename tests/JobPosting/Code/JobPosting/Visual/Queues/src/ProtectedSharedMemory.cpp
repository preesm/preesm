#include "ProtectedSharedMemory.h"

#include <windows.h>
#include <stdio.h>
#include <conio.h>
#include <tchar.h>

TCHAR szFileName[]=TEXT("Global\\MyFileMappingObject");
TCHAR szMsg[]=TEXT("Message from first process.");
TCHAR NameOfMutexObject[]=TEXT("NameOfMutexObject");

/**
 Creating the mutex to protect writing method
*/
void ProtectedSharedMemory::createMutex(LPCWSTR name)
{
    hMutex = CreateMutex( 
        NULL,                        // default security descriptor
        FALSE,                       // mutex not owned
        name);  // object name

    if (hMutex == NULL) 
        printf("CreateMutex error: %d\n", GetLastError() ); 
    else 
        if ( GetLastError() == ERROR_ALREADY_EXISTS ) 
            printf("CreateMutex opened an existing mutex\n"); 
        else printf("CreateMutex created a new mutex.\n");
}
		
/**
 Connecting to the mutex created in another process
*/
void ProtectedSharedMemory::connectMutex(LPCWSTR name)
{
    hMutex = OpenMutex( 
        MUTEX_ALL_ACCESS,            // request full access
        FALSE,                       // handle not inheritable
        name);  // object name

    if (hMutex == NULL) 
        printf("OpenMutex error: %d\n", GetLastError() );
    else printf("OpenMutex successfully opened the mutex.\n");
}
/**
 Creating the shared file and initializing pointer to shared memory

 @param size: the size of the memory
 @param name: the name of the memory used to connect it in another process
 @return: 1 if it worked; 0 otherwise
*/
int ProtectedSharedMemory::createMem(int size, LPCWSTR name)
{
   hMapFile = CreateFileMapping(
                 INVALID_HANDLE_VALUE,		// use paging file
                 NULL,						// default security 
                 PAGE_READWRITE,			// read/write access
                 0,							// max. object size 
                 size,						// buffer size  
                 name);               // name of mapping object
 
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
                        size);           
 
   if (memoryPointer == NULL) 
   { 
      _tprintf(TEXT("Could not map view of file (%d).\n"), 
             GetLastError()); 

	   CloseHandle(hMapFile);

      return 1;
   }

   return 0;
}

/**
 Connecting to the memory created in another process

 @param size: the size of the memory
 @param name: the name of the memory used to connect it
 @return: 1 if it worked; 0 otherwise
*/
int ProtectedSharedMemory::connectMem(int size, LPCWSTR name)
{

   hMapFile = OpenFileMapping(
                   FILE_MAP_ALL_ACCESS,   // read/write access
                   FALSE,                 // do not inherit the name
                   name);               // name of mapping object 
 
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
               size);                   
 
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

/**
 Constructor 

 @param create: 1 if memory and mutex need to be created
 @param size: the size of the memory
 @param id: the id from which is derived the name of the memory
*/
ProtectedSharedMemory::ProtectedSharedMemory(int create, int size, int id)
{
    DWORD dw = GetLastError();
	memoryPointer = NULL;
	TCHAR sId[4] =  TEXT("\0\0\0"); 
	_itow_s(id,sId,10);
	TCHAR memName[50] = TEXT("Global\\JobPostingMappingObject");
	_tcscat_s(memName,sId);
	TCHAR mutName[50] = TEXT("JobPostingMutex");
	_tcscat_s(mutName,sId);

	if(create != 0){
		createMutex(mutName);
		createMem(size,memName);
		
		//write((void*)szMsg,0,100);
	}
	else{
		connectMutex(mutName);
		connectMem(size,memName);

		/*
		TCHAR message[712];
		if(read((void*)message,0,100)){
			MessageBox(NULL, (LPCTSTR)message, TEXT("Process2"), MB_OK);
		}*/
	}
}

/**
 Destructor
*/
ProtectedSharedMemory::~ProtectedSharedMemory()
{
   //_getch();
   UnmapViewOfFile(memoryPointer);
   CloseHandle(hMapFile);
}

/**
 Reading data from memory
 
 @param buffer: returned buffer
 @param offset: offset of the address where to start reading
 @param size: size of the copied data
 @return: 1 if it worked; 0 otherwise
*/
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

/**
 Writing data to memory
 
 @param buffer: input buffer
 @param offset: offset of the address where to start writing
 @param size: size of the copied data
 @return: 1 if it worked; 0 otherwise
*/
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

