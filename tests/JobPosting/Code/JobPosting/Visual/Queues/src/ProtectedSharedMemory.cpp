/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
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

