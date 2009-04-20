    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    int IDCT2D__21[2];
    int explode_16[1];
    int explode_17[1];
    int r_explod_6[8];
    int r_explod_7[8];
    int IDCT2D__22[8];
    int IDCT2D__23[8];
    int r_explod_8[8];
    int r_explod_9[8];
    int IDCT2D__24[8];
    int IDCT2D__25[8];
    semaphore sem_1[20];

    DWORD WINAPI computationThread_Core2( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core2( LPVOID lpParam );

    DWORD WINAPI computationThread_Core2( LPVOID lpParam ){
        // Buffer declarations

        {
            semaphoreInit(sem_1, 20/*semNumber*/, Core2);
            CreateThread(NULL,8000,communicationThread_Core2,NULL,0,NULL);
            ReleaseSemaphore(sem_1[0],1,NULL); //empty
            ReleaseSemaphore(sem_1[2],1,NULL); //empty
            ReleaseSemaphore(sem_1[6],1,NULL); //empty
            ReleaseSemaphore(sem_1[10],1,NULL); //empty
            ReleaseSemaphore(sem_1[14],1,NULL); //empty
            ReleaseSemaphore(sem_1[18],1,NULL); //empty
        }

        for(;;){
            trigger(IDCT2D__21);
            WaitForSingleObject(sem_1[0],INFINITE); //empty
            WaitForSingleObject(sem_1[2],INFINITE); //empty
            {//explode_IDCT2D_0_IDCT2D_basic_0_trigger_trig
                memcpy(explode_16, &IDCT2D__21[0], 1*sizeof(int)/*size*/);
                memcpy(explode_17, &IDCT2D__21[1], 1*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem_1[3],1,NULL); //full
            ReleaseSemaphore(sem_1[1],1,NULL); //full
            WaitForSingleObject(sem_1[4],INFINITE); //full
            WaitForSingleObject(sem_1[6],INFINITE); //empty
            idct1d(r_explod_6, IDCT2D__22);
            ReleaseSemaphore(sem_1[7],1,NULL); //full
            ReleaseSemaphore(sem_1[5],1,NULL); //empty
            WaitForSingleObject(sem_1[8],INFINITE); //full
            WaitForSingleObject(sem_1[10],INFINITE); //empty
            idct1d(r_explod_7, IDCT2D__23);
            ReleaseSemaphore(sem_1[11],1,NULL); //full
            ReleaseSemaphore(sem_1[9],1,NULL); //empty
            WaitForSingleObject(sem_1[12],INFINITE); //full
            WaitForSingleObject(sem_1[14],INFINITE); //empty
            idct1d(r_explod_8, IDCT2D__24);
            ReleaseSemaphore(sem_1[15],1,NULL); //full
            ReleaseSemaphore(sem_1[13],1,NULL); //empty
            WaitForSingleObject(sem_1[16],INFINITE); //full
            WaitForSingleObject(sem_1[18],INFINITE); //empty
            idct1d(r_explod_9, IDCT2D__25);
            ReleaseSemaphore(sem_1[19],1,NULL); //full
            ReleaseSemaphore(sem_1[17],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core2( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_RCV,TCP,Core0,Core2);
            Com_Init(MEDIUM_SEND,TCP,Core2,Core0);
            ReleaseSemaphore(sem_1[9],1,NULL); //empty
            ReleaseSemaphore(sem_1[5],1,NULL); //empty
            ReleaseSemaphore(sem_1[17],1,NULL); //empty
            ReleaseSemaphore(sem_1[13],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_1[1],INFINITE); //full
            sendData(TCP,Core2,Core0,explode_17,1*sizeof(int));
            ReleaseSemaphore(sem_1[0],1,NULL); //empty
            WaitForSingleObject(sem_1[3],INFINITE); //full
            sendData(TCP,Core2,Core0,explode_16,1*sizeof(int));
            ReleaseSemaphore(sem_1[2],1,NULL); //empty
            WaitForSingleObject(sem_1[9],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_explod_7,8*sizeof(int));
            ReleaseSemaphore(sem_1[8],1,NULL); //full
            WaitForSingleObject(sem_1[5],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_explod_6,8*sizeof(int));
            ReleaseSemaphore(sem_1[4],1,NULL); //full
            WaitForSingleObject(sem_1[7],INFINITE); //full
            sendData(TCP,Core2,Core0,IDCT2D__22,8*sizeof(int));
            ReleaseSemaphore(sem_1[6],1,NULL); //empty
            WaitForSingleObject(sem_1[11],INFINITE); //full
            sendData(TCP,Core2,Core0,IDCT2D__23,8*sizeof(int));
            ReleaseSemaphore(sem_1[10],1,NULL); //empty
            WaitForSingleObject(sem_1[17],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_explod_9,8*sizeof(int));
            ReleaseSemaphore(sem_1[16],1,NULL); //full
            WaitForSingleObject(sem_1[13],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_explod_8,8*sizeof(int));
            ReleaseSemaphore(sem_1[12],1,NULL); //full
            WaitForSingleObject(sem_1[15],INFINITE); //full
            sendData(TCP,Core2,Core0,IDCT2D__24,8*sizeof(int));
            ReleaseSemaphore(sem_1[14],1,NULL); //empty
            WaitForSingleObject(sem_1[19],INFINITE); //full
            sendData(TCP,Core2,Core0,IDCT2D__25,8*sizeof(int));
            ReleaseSemaphore(sem_1[18],1,NULL); //empty
        }

        return 0;
    }//communicationThread

