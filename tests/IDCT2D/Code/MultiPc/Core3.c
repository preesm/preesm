    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    int r_explo_10[8];
    int r_explo_11[8];
    int IDCT2D__26[8];
    int IDCT2D__27[8];
    int r_explo_12[8];
    int r_explo_13[8];
    int IDCT2D__28[8];
    int IDCT2D__29[8];
    semaphore sem_2[16];

    DWORD WINAPI computationThread_Core3( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core3( LPVOID lpParam );

    DWORD WINAPI computationThread_Core3( LPVOID lpParam ){
        // Buffer declarations

        {
            semaphoreInit(sem_2, 16/*semNumber*/, Core3);
            CreateThread(NULL,8000,communicationThread_Core3,NULL,0,NULL);
            ReleaseSemaphore(sem_2[2],1,NULL); //empty
            ReleaseSemaphore(sem_2[6],1,NULL); //empty
            ReleaseSemaphore(sem_2[10],1,NULL); //empty
            ReleaseSemaphore(sem_2[14],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_2[0],INFINITE); //full
            WaitForSingleObject(sem_2[2],INFINITE); //empty
            idct1d(r_explo_10, IDCT2D__26);
            ReleaseSemaphore(sem_2[3],1,NULL); //full
            ReleaseSemaphore(sem_2[1],1,NULL); //empty
            WaitForSingleObject(sem_2[4],INFINITE); //full
            WaitForSingleObject(sem_2[6],INFINITE); //empty
            idct1d(r_explo_11, IDCT2D__27);
            ReleaseSemaphore(sem_2[7],1,NULL); //full
            ReleaseSemaphore(sem_2[5],1,NULL); //empty
            WaitForSingleObject(sem_2[8],INFINITE); //full
            WaitForSingleObject(sem_2[10],INFINITE); //empty
            idct1d(r_explo_12, IDCT2D__28);
            ReleaseSemaphore(sem_2[11],1,NULL); //full
            ReleaseSemaphore(sem_2[9],1,NULL); //empty
            WaitForSingleObject(sem_2[12],INFINITE); //full
            WaitForSingleObject(sem_2[14],INFINITE); //empty
            idct1d(r_explo_13, IDCT2D__29);
            ReleaseSemaphore(sem_2[15],1,NULL); //full
            ReleaseSemaphore(sem_2[13],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core3( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_SEND,TCP,Core3,Core0);
            Com_Init(MEDIUM_RCV,TCP,Core0,Core3);
            ReleaseSemaphore(sem_2[5],1,NULL); //empty
            ReleaseSemaphore(sem_2[1],1,NULL); //empty
            ReleaseSemaphore(sem_2[13],1,NULL); //empty
            ReleaseSemaphore(sem_2[9],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_2[5],INFINITE); //empty
            receiveData(TCP,Core0,Core3,r_explo_11,8*sizeof(int));
            ReleaseSemaphore(sem_2[4],1,NULL); //full
            WaitForSingleObject(sem_2[1],INFINITE); //empty
            receiveData(TCP,Core0,Core3,r_explo_10,8*sizeof(int));
            ReleaseSemaphore(sem_2[0],1,NULL); //full
            WaitForSingleObject(sem_2[3],INFINITE); //full
            sendData(TCP,Core3,Core0,IDCT2D__26,8*sizeof(int));
            ReleaseSemaphore(sem_2[2],1,NULL); //empty
            WaitForSingleObject(sem_2[7],INFINITE); //full
            sendData(TCP,Core3,Core0,IDCT2D__27,8*sizeof(int));
            ReleaseSemaphore(sem_2[6],1,NULL); //empty
            WaitForSingleObject(sem_2[13],INFINITE); //empty
            receiveData(TCP,Core0,Core3,r_explo_13,8*sizeof(int));
            ReleaseSemaphore(sem_2[12],1,NULL); //full
            WaitForSingleObject(sem_2[9],INFINITE); //empty
            receiveData(TCP,Core0,Core3,r_explo_12,8*sizeof(int));
            ReleaseSemaphore(sem_2[8],1,NULL); //full
            WaitForSingleObject(sem_2[11],INFINITE); //full
            sendData(TCP,Core3,Core0,IDCT2D__28,8*sizeof(int));
            ReleaseSemaphore(sem_2[10],1,NULL); //empty
            WaitForSingleObject(sem_2[15],INFINITE); //full
            sendData(TCP,Core3,Core0,IDCT2D__29,8*sizeof(int));
            ReleaseSemaphore(sem_2[14],1,NULL); //empty
        }

        return 0;
    }//communicationThread

