    #include "x86.h"

    // Buffer declarations
    int r_explod_6[64];
    int r_explod_7[64];
    int r_explod_8[64];
    int r_explod_9[1];
    int r_explo_10[1];
    int r_explo_11[1];
    int r_IDCT2D_0[64];
    int r_IDCT2D_1[64];
    int IDCT2D_1_0[64];
    int IDCT2D_2_0[64];
    int IDCT2D_8_0[64];
    int r_IDCT2D_2[64];
    int r_IDCT2D_3[64];
    int r_IDCT2D_4[64];
    int r_IDCT2D_5[64];
    int r_IDCT2D_6[64];
    int implode__0[640];
    semaphore sem_1[26];

    DWORD WINAPI computationThread_Core2( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core2( LPVOID lpParam );

    DWORD WINAPI computationThread_Core2( LPVOID lpParam ){
        // Buffer declarations
        long i ;
        long j ;

        {
            semaphoreInit(sem_1, 26/*semNumber*/, Core2);
            CreateThread(NULL,8000,communicationThread_Core2,NULL,0,NULL);
        }

        for(;;){
            WaitForSingleObject(sem_1[0],INFINITE); //full
            WaitForSingleObject(sem_1[2],INFINITE); //full
            {//IDCT2D_8
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
                        readBlock(r_explod_8, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explo_11, IDCT2D_8_0);
            }
            ReleaseSemaphore(sem_1[3],1,NULL); //empty
            ReleaseSemaphore(sem_1[1],1,NULL); //empty
            WaitForSingleObject(sem_1[4],INFINITE); //full
            WaitForSingleObject(sem_1[6],INFINITE); //full
            {//IDCT2D_1
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
                        readBlock(r_explod_6, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explod_9, IDCT2D_1_0);
            }
            ReleaseSemaphore(sem_1[7],1,NULL); //empty
            ReleaseSemaphore(sem_1[5],1,NULL); //empty
            WaitForSingleObject(sem_1[8],INFINITE); //full
            WaitForSingleObject(sem_1[10],INFINITE); //full
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
                        readBlock(r_explod_7, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, r_explo_10, IDCT2D_2_0);
            }
            ReleaseSemaphore(sem_1[11],1,NULL); //empty
            ReleaseSemaphore(sem_1[9],1,NULL); //empty
            WaitForSingleObject(sem_1[12],INFINITE); //full
            WaitForSingleObject(sem_1[14],INFINITE); //full
            WaitForSingleObject(sem_1[16],INFINITE); //full
            WaitForSingleObject(sem_1[18],INFINITE); //full
            WaitForSingleObject(sem_1[20],INFINITE); //full
            WaitForSingleObject(sem_1[22],INFINITE); //full
            WaitForSingleObject(sem_1[24],INFINITE); //full
            {//implode_Group_MB
                memcpy(&implode__0[0], IDCT2D_1_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[64], IDCT2D_2_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[128], IDCT2D_8_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[192], r_IDCT2D_2, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[256], r_IDCT2D_3, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[320], r_IDCT2D_4, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[384], r_IDCT2D_5, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[448], r_IDCT2D_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[512], r_IDCT2D_6, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[576], r_IDCT2D_1, 64*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem_1[25],1,NULL); //empty
            ReleaseSemaphore(sem_1[23],1,NULL); //empty
            ReleaseSemaphore(sem_1[21],1,NULL); //empty
            ReleaseSemaphore(sem_1[19],1,NULL); //empty
            ReleaseSemaphore(sem_1[17],1,NULL); //empty
            ReleaseSemaphore(sem_1[15],1,NULL); //empty
            ReleaseSemaphore(sem_1[13],1,NULL); //empty
            group_bench(implode__0);
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core2( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_RCV,TCP,Core3,Core2);
            Com_Init(MEDIUM_RCV,TCP,Core0,Core2);
            Com_Init(MEDIUM_RCV,TCP,Core1,Core2);
            ReleaseSemaphore(sem_1[1],1,NULL); //empty
            ReleaseSemaphore(sem_1[9],1,NULL); //empty
            ReleaseSemaphore(sem_1[5],1,NULL); //empty
            ReleaseSemaphore(sem_1[3],1,NULL); //empty
            ReleaseSemaphore(sem_1[11],1,NULL); //empty
            ReleaseSemaphore(sem_1[7],1,NULL); //empty
            ReleaseSemaphore(sem_1[13],1,NULL); //empty
            ReleaseSemaphore(sem_1[15],1,NULL); //empty
            ReleaseSemaphore(sem_1[17],1,NULL); //empty
            ReleaseSemaphore(sem_1[19],1,NULL); //empty
            ReleaseSemaphore(sem_1[21],1,NULL); //empty
            ReleaseSemaphore(sem_1[23],1,NULL); //empty
            ReleaseSemaphore(sem_1[25],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_1[1],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_explo_11,1*sizeof(int));
            ReleaseSemaphore(sem_1[0],1,NULL); //full
            WaitForSingleObject(sem_1[9],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_explo_10,1*sizeof(int));
            ReleaseSemaphore(sem_1[8],1,NULL); //full
            WaitForSingleObject(sem_1[5],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_explod_9,1*sizeof(int));
            ReleaseSemaphore(sem_1[4],1,NULL); //full
            WaitForSingleObject(sem_1[3],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_explod_8,64*sizeof(int));
            ReleaseSemaphore(sem_1[2],1,NULL); //full
            WaitForSingleObject(sem_1[11],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_explod_7,64*sizeof(int));
            ReleaseSemaphore(sem_1[10],1,NULL); //full
            WaitForSingleObject(sem_1[7],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_explod_6,64*sizeof(int));
            ReleaseSemaphore(sem_1[6],1,NULL); //full
            WaitForSingleObject(sem_1[13],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_IDCT2D_3,64*sizeof(int));
            ReleaseSemaphore(sem_1[12],1,NULL); //full
            WaitForSingleObject(sem_1[15],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_IDCT2D_1,64*sizeof(int));
            ReleaseSemaphore(sem_1[14],1,NULL); //full
            WaitForSingleObject(sem_1[17],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_IDCT2D_5,64*sizeof(int));
            ReleaseSemaphore(sem_1[16],1,NULL); //full
            WaitForSingleObject(sem_1[19],INFINITE); //empty
            receiveData(TCP,Core3,Core2,r_IDCT2D_2,64*sizeof(int));
            ReleaseSemaphore(sem_1[18],1,NULL); //full
            WaitForSingleObject(sem_1[21],INFINITE); //empty
            receiveData(TCP,Core1,Core2,r_IDCT2D_0,64*sizeof(int));
            ReleaseSemaphore(sem_1[20],1,NULL); //full
            WaitForSingleObject(sem_1[23],INFINITE); //empty
            receiveData(TCP,Core0,Core2,r_IDCT2D_6,64*sizeof(int));
            ReleaseSemaphore(sem_1[22],1,NULL); //full
            WaitForSingleObject(sem_1[25],INFINITE); //empty
            receiveData(TCP,Core3,Core2,r_IDCT2D_4,64*sizeof(int));
            ReleaseSemaphore(sem_1[24],1,NULL); //full
        }

        return 0;
    }//communicationThread

