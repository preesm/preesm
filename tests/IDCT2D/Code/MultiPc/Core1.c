    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    int r_explod_0[64];
    int r_explod_1[64];
    int r_explod_2[64];
    int r_explod_3[64];
    int r_explod_4[64];
    int r_explod_5[1];
    int r_explod_6[1];
    int r_explod_7[1];
    int r_explod_8[1];
    int r_explod_9[1];
    int IDCT2D_0_0[64];
    int IDCT2D_2_0[64];
    int IDCT2D_4_0[64];
    int IDCT2D_5_0[64];
    int IDCT2D_9_0[64];
    semaphore sem_0[30];

    DWORD WINAPI computationThread_Core1( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core1( LPVOID lpParam );

    DWORD WINAPI computationThread_Core1( LPVOID lpParam ){
        // Buffer declarations
        long i ;
        long j ;

        {
            semaphoreInit(sem_0, 30/*semNumber*/, Core1);
            CreateThread(NULL,8000,communicationThread_Core1,NULL,0,NULL);
            ReleaseSemaphore(sem_0[4],1,NULL); //empty
            ReleaseSemaphore(sem_0[10],1,NULL); //empty
            ReleaseSemaphore(sem_0[16],1,NULL); //empty
            ReleaseSemaphore(sem_0[22],1,NULL); //empty
            ReleaseSemaphore(sem_0[28],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_0[0],INFINITE); //full
            WaitForSingleObject(sem_0[2],INFINITE); //full
            WaitForSingleObject(sem_0[4],INFINITE); //empty
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
                        readBlock(r_explod_0, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explod_5, IDCT2D_0_0);
            }
            ReleaseSemaphore(sem_0[5],1,NULL); //full
            ReleaseSemaphore(sem_0[3],1,NULL); //empty
            ReleaseSemaphore(sem_0[1],1,NULL); //empty
            WaitForSingleObject(sem_0[6],INFINITE); //full
            WaitForSingleObject(sem_0[8],INFINITE); //full
            WaitForSingleObject(sem_0[10],INFINITE); //empty
            {//IDCT2D_2
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
                        readBlock(r_explod_1, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explod_6, IDCT2D_2_0);
            }
            ReleaseSemaphore(sem_0[11],1,NULL); //full
            ReleaseSemaphore(sem_0[9],1,NULL); //empty
            ReleaseSemaphore(sem_0[7],1,NULL); //empty
            WaitForSingleObject(sem_0[12],INFINITE); //full
            WaitForSingleObject(sem_0[14],INFINITE); //full
            WaitForSingleObject(sem_0[16],INFINITE); //empty
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
                        readBlock(r_explod_2, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explod_7, IDCT2D_4_0);
            }
            ReleaseSemaphore(sem_0[17],1,NULL); //full
            ReleaseSemaphore(sem_0[15],1,NULL); //empty
            ReleaseSemaphore(sem_0[13],1,NULL); //empty
            WaitForSingleObject(sem_0[18],INFINITE); //full
            WaitForSingleObject(sem_0[20],INFINITE); //full
            WaitForSingleObject(sem_0[22],INFINITE); //empty
            {//IDCT2D_5
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
                        readBlock(r_explod_3, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explod_8, IDCT2D_5_0);
            }
            ReleaseSemaphore(sem_0[23],1,NULL); //full
            ReleaseSemaphore(sem_0[21],1,NULL); //empty
            ReleaseSemaphore(sem_0[19],1,NULL); //empty
            WaitForSingleObject(sem_0[24],INFINITE); //full
            WaitForSingleObject(sem_0[26],INFINITE); //full
            WaitForSingleObject(sem_0[28],INFINITE); //empty
            {//IDCT2D_9
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
                        readBlock(r_explod_4, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explod_9, IDCT2D_9_0);
            }
            ReleaseSemaphore(sem_0[29],1,NULL); //full
            ReleaseSemaphore(sem_0[27],1,NULL); //empty
            ReleaseSemaphore(sem_0[25],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core1( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_SEND,TCP,Core1,Core0);
            Com_Init(MEDIUM_RCV,TCP,Core0,Core1);
            ReleaseSemaphore(sem_0[25],1,NULL); //empty
            ReleaseSemaphore(sem_0[19],1,NULL); //empty
            ReleaseSemaphore(sem_0[13],1,NULL); //empty
            ReleaseSemaphore(sem_0[7],1,NULL); //empty
            ReleaseSemaphore(sem_0[1],1,NULL); //empty
            ReleaseSemaphore(sem_0[27],1,NULL); //empty
            ReleaseSemaphore(sem_0[21],1,NULL); //empty
            ReleaseSemaphore(sem_0[15],1,NULL); //empty
            ReleaseSemaphore(sem_0[9],1,NULL); //empty
            ReleaseSemaphore(sem_0[3],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_0[25],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_4,64*sizeof(int));
            ReleaseSemaphore(sem_0[24],1,NULL); //full
            WaitForSingleObject(sem_0[19],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_3,64*sizeof(int));
            ReleaseSemaphore(sem_0[18],1,NULL); //full
            WaitForSingleObject(sem_0[13],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_2,64*sizeof(int));
            ReleaseSemaphore(sem_0[12],1,NULL); //full
            WaitForSingleObject(sem_0[7],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_1,64*sizeof(int));
            ReleaseSemaphore(sem_0[6],1,NULL); //full
            WaitForSingleObject(sem_0[1],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_0,64*sizeof(int));
            ReleaseSemaphore(sem_0[0],1,NULL); //full
            WaitForSingleObject(sem_0[27],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_9,1*sizeof(int));
            ReleaseSemaphore(sem_0[26],1,NULL); //full
            WaitForSingleObject(sem_0[21],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_8,1*sizeof(int));
            ReleaseSemaphore(sem_0[20],1,NULL); //full
            WaitForSingleObject(sem_0[15],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_7,1*sizeof(int));
            ReleaseSemaphore(sem_0[14],1,NULL); //full
            WaitForSingleObject(sem_0[9],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_6,1*sizeof(int));
            ReleaseSemaphore(sem_0[8],1,NULL); //full
            WaitForSingleObject(sem_0[3],INFINITE); //empty
            receiveData(TCP,Core0,Core1,r_explod_5,1*sizeof(int));
            ReleaseSemaphore(sem_0[2],1,NULL); //full
            WaitForSingleObject(sem_0[5],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D_0_0,64*sizeof(int));
            ReleaseSemaphore(sem_0[4],1,NULL); //empty
            WaitForSingleObject(sem_0[11],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D_2_0,64*sizeof(int));
            ReleaseSemaphore(sem_0[10],1,NULL); //empty
            WaitForSingleObject(sem_0[17],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D_4_0,64*sizeof(int));
            ReleaseSemaphore(sem_0[16],1,NULL); //empty
            WaitForSingleObject(sem_0[23],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D_5_0,64*sizeof(int));
            ReleaseSemaphore(sem_0[22],1,NULL); //empty
            WaitForSingleObject(sem_0[29],INFINITE); //full
            sendData(TCP,Core1,Core0,IDCT2D_9_0,64*sizeof(int));
            ReleaseSemaphore(sem_0[28],1,NULL); //empty
        }

        return 0;
    }//communicationThread

