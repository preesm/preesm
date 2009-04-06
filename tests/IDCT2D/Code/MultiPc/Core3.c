   #include "x86.h"

    // Buffer declarations
    int r_explo_12[64];
    int r_explo_13[64];
    int r_explo_14[1];
    int r_explo_15[1];
    int IDCT2D_0_0[64];
    int IDCT2D_4_0[64];
    semaphore sem_2[12];

    DWORD WINAPI computationThread_Core3( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core3( LPVOID lpParam );

    DWORD WINAPI computationThread_Core3( LPVOID lpParam ){
        // Buffer declarations
        long i ;
        long j ;

        {
            semaphoreInit(sem_2, 12/*semNumber*/, Core3);
            CreateThread(NULL,8000,communicationThread_Core3,NULL,0,NULL);
            ReleaseSemaphore(sem_2[4],1,NULL); //empty
            ReleaseSemaphore(sem_2[10],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_2[0],INFINITE); //full
            WaitForSingleObject(sem_2[2],INFINITE); //full
            WaitForSingleObject(sem_2[4],INFINITE); //empty
            {//IDCT2D_0
                int block_ou_0[64];
                {//IDCT2D_basic
                    int trig_clu_0[2];
                    int outLoopP_0[64];
                    init_inLoopPort_0(outLoopP_0, 64/*init_size*/);
                    trigger(trig_clu_0);
                    for(i = 0; i<2 ; i ++)
                    {//cluster_0
                        int *outSub_i_0 = &trig_clu_0 [(i * 1) % 2];
                        int out_1_li_0[64];
                        int lineOut__0[64];
                        int blockOut_0[64];
                        readBlock(r_explo_12, outLoopP_0, outSub_i_0, out_1_li_0);
                        for(j = 0; j<8 ; j ++)
                        {//IDCT1D
                            int *inSub_j__0 = &lineOut__0 [(j * 8) % 64];
                            int *outSub_j_0 = &out_1_li_0 [(j * 8) % 64];
                            idct1d(outSub_j_0, inSub_j__0);
                        }
                        transpose(lineOut__0, blockOut_0);
                        memcpy(block_ou_0, blockOut_0, 64*sizeof(int)/*size*/);
                        memcpy(outLoopP_0, blockOut_0, 64*sizeof(int)/*size*/);
                    }
                }
                clip(block_ou_0, r_explo_14, IDCT2D_0_0);
            }
            ReleaseSemaphore(sem_2[5],1,NULL); //full
            ReleaseSemaphore(sem_2[3],1,NULL); //empty
            ReleaseSemaphore(sem_2[1],1,NULL); //empty
            WaitForSingleObject(sem_2[6],INFINITE); //full
            WaitForSingleObject(sem_2[8],INFINITE); //full
            WaitForSingleObject(sem_2[10],INFINITE); //empty
            {//IDCT2D_4
                int block_ou_0[64];
                {//IDCT2D_basic
                    int trig_clu_0[2];
                    int outLoopP_0[64];
                    init_inLoopPort_0(outLoopP_0, 64/*init_size*/);
                    trigger(trig_clu_0);
                    for(i = 0; i<2 ; i ++)
                    {//cluster_0
                        int *outSub_i_0 = &trig_clu_0 [(i * 1) % 2];
                        int out_1_li_0[64];
                        int lineOut__0[64];
                        int blockOut_0[64];
                        readBlock(r_explo_13, outLoopP_0, outSub_i_0, out_1_li_0);
                        for(j = 0; j<8 ; j ++)
                        {//IDCT1D
                            int *inSub_j__0 = &lineOut__0 [(j * 8) % 64];
                            int *outSub_j_0 = &out_1_li_0 [(j * 8) % 64];
                            idct1d(outSub_j_0, inSub_j__0);
                        }
                        transpose(lineOut__0, blockOut_0);
                        memcpy(block_ou_0, blockOut_0, 64*sizeof(int)/*size*/);
                        memcpy(outLoopP_0, blockOut_0, 64*sizeof(int)/*size*/);
                    }
                }
                clip(block_ou_0, r_explo_15, IDCT2D_4_0);
            }
            ReleaseSemaphore(sem_2[11],1,NULL); //full
            ReleaseSemaphore(sem_2[9],1,NULL); //empty
            ReleaseSemaphore(sem_2[7],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core3( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_SEND,TCP,Core3,Core2);
            Com_Init(MEDIUM_RCV,TCP,Core1,Core3);
            ReleaseSemaphore(sem_2[7],1,NULL); //empty
            ReleaseSemaphore(sem_2[1],1,NULL); //empty
            ReleaseSemaphore(sem_2[9],1,NULL); //empty
            ReleaseSemaphore(sem_2[3],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_2[7],INFINITE); //empty
            receiveData(TCP,Core1,Core3,r_explo_15,1*sizeof(int));
            ReleaseSemaphore(sem_2[6],1,NULL); //full
            WaitForSingleObject(sem_2[1],INFINITE); //empty
            receiveData(TCP,Core1,Core3,r_explo_14,1*sizeof(int));
            ReleaseSemaphore(sem_2[0],1,NULL); //full
            WaitForSingleObject(sem_2[9],INFINITE); //empty
            receiveData(TCP,Core1,Core3,r_explo_13,64*sizeof(int));
            ReleaseSemaphore(sem_2[8],1,NULL); //full
            WaitForSingleObject(sem_2[3],INFINITE); //empty
            receiveData(TCP,Core1,Core3,r_explo_12,64*sizeof(int));
            ReleaseSemaphore(sem_2[2],1,NULL); //full
            WaitForSingleObject(sem_2[5],INFINITE); //full
            sendData(TCP,Core3,Core2,IDCT2D_0_0,64*sizeof(int));
            ReleaseSemaphore(sem_2[4],1,NULL); //empty
            WaitForSingleObject(sem_2[11],INFINITE); //full
            sendData(TCP,Core3,Core2,IDCT2D_4_0,64*sizeof(int));
            ReleaseSemaphore(sem_2[10],1,NULL); //empty
        }

        return 0;
    }//communicationThread

