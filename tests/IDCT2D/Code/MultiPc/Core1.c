    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    int r_explod_2[8];
    int r_explod_3[8];
    int IDCT2D__17[8];
    int IDCT2D__18[8];
    int r_explod_4[8];
    int r_explod_5[8];
    int IDCT2D__19[8];
    int IDCT2D__20[8];
    semaphore sem_0[16];

    DWORD WINAPI computationThread_Core1( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core1( LPVOID lpParam );

    DWORD WINAPI computationThread_Core1( LPVOID lpParam ){
        // Buffer declarations

        {
            semaphoreInit(sem_0, 16/*semNumber*/, Core1);
            CreateThread(NULL,8000,communicationThread_Core1,NULL,0,NULL);
            ReleaseSemaphore(sem_0[2],1,NULL); //empty
            ReleaseSemaphore(sem_0[6],1,NULL); //empty
            ReleaseSemaphore(sem_0[10],1,NULL); //empty
            ReleaseSemaphore(sem_0[14],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_0[0],INFINITE); //full
            WaitForSingleObject(sem_0[2],INFINITE); //empty
            idct1d(r_explod_2, IDCT2D__17);
            ReleaseSemaphore(sem_0[3],1,NULL); //full
            ReleaseSemaphore(sem_0[1],1,NULL); //empty
            WaitForSingleObject(sem_0[4],INFINITE); //full
            WaitForSingleObject(sem_0[6],INFINITE); //empty
            idct1d(r_explod_3, IDCT2D__18);
            ReleaseSemaphore(sem_0[7],1,NULL); //full
            ReleaseSemaphore(sem_0[5],1,NULL); //empty
            WaitForSingleObject(sem_0[8],INFINITE); //full
            WaitForSingleObject(sem_0[10],INFINITE); //empty
            idct1d(r_explod_5, IDCT2D__20);
            ReleaseSemaphore(sem_0[11],1,NULL); //full
            ReleaseSemaphore(sem_0[9],1,NULL); //empty
            WaitForSingleObject(sem_0[12],INFINITE); //full
            WaitForSingleObject(sem_0[14],INFINITE); //empty
            idct1d(r_explod_4, IDCT2D__19);
            ReleaseSemaphore(sem_0[15],1,NULL); //full
            ReleaseSemaphore(sem_0[13],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core1( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_SEND,TCP,Core1,Core0);
            Com_Init(MEDIUM_RCV,TCP,Core0,Core1);
            ReleaseSemaphore(sem_0[5],1,NULL); //empty
            ReleaseSemaphore(sem_0[1],1,NULL); //empty
            ReleaseSemaphore(sem_0[9],1,NULL); //empty
            ReleaseSemaphore(sem_0[13],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_0[5],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_3,8*sizeof(int));
            ReleaseSemaphore(sem_0[4],1,NULL); //full
            WaitForSingleObject(sem_0[1],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_2,8*sizeof(int));
            ReleaseSemaphore(sem_0[0],1,NULL); //full
            WaitForSingleObject(sem_0[3],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D__17,8*sizeof(int));
            ReleaseSemaphore(sem_0[2],1,NULL); //empty
            WaitForSingleObject(sem_0[7],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D__18,8*sizeof(int));
            ReleaseSemaphore(sem_0[6],1,NULL); //empty
            WaitForSingleObject(sem_0[9],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_5,8*sizeof(int));
            ReleaseSemaphore(sem_0[8],1,NULL); //full
            WaitForSingleObject(sem_0[13],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_4,8*sizeof(int));
            ReleaseSemaphore(sem_0[12],1,NULL); //full
            WaitForSingleObject(sem_0[11],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D__20,8*sizeof(int));
            ReleaseSemaphore(sem_0[10],1,NULL); //empty
            WaitForSingleObject(sem_0[15],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D__19,8*sizeof(int));
            ReleaseSemaphore(sem_0[14],1,NULL); //empty
        }

        return 0;
    }//communicationThread

