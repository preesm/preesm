    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    int TriggerM_0[640];
    int Triggers_0[10];
    int explode__0[64];
    int explode__1[64];
    int explode__2[64];
    int explode__3[64];
    int explode__4[64];
    int explode__5[64];
    int explode__6[64];
    int explode__7[64];
    int explode__8[64];
    int explode__9[64];
    int explode_10[1];
    int explode_11[1];
    int explode_12[1];
    int explode_13[1];
    int explode_14[1];
    int explode_15[1];
    int explode_16[1];
    int explode_17[1];
    int explode_18[1];
    int explode_19[1];
    int IDCT2D_1_0[64];
    int IDCT2D_3_0[64];
    int IDCT2D_6_0[64];
    int IDCT2D_7_0[64];
    int IDCT2D_8_0[64];
    int r_IDCT2D_0[64];
    int r_IDCT2D_1[64];
    int r_IDCT2D_2[64];
    int r_IDCT2D_3[64];
    int r_IDCT2D_4[64];
    int implode__0[640];
    semaphore sem[30];

    DWORD WINAPI computationThread_Core0( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core0( LPVOID lpParam );

    DWORD WINAPI computationThread_Core0( LPVOID lpParam ){
        // Buffer declarations
        long i ;
        long j ;

        {
            semaphoreInit(sem, 30/*semNumber*/, Core0);
            CreateThread(NULL,8000,communicationThread_Core0,NULL,0,NULL);
            ReleaseSemaphore(sem[0],1,NULL); //empty
            ReleaseSemaphore(sem[2],1,NULL); //empty
            ReleaseSemaphore(sem[4],1,NULL); //empty
            ReleaseSemaphore(sem[6],1,NULL); //empty
            ReleaseSemaphore(sem[8],1,NULL); //empty
            ReleaseSemaphore(sem[10],1,NULL); //empty
            ReleaseSemaphore(sem[12],1,NULL); //empty
            ReleaseSemaphore(sem[14],1,NULL); //empty
            ReleaseSemaphore(sem[16],1,NULL); //empty
            ReleaseSemaphore(sem[18],1,NULL); //empty
        }

        for(;;){
            trigger_bench(TriggerM_0, Triggers_0);
            WaitForSingleObject(sem[0],INFINITE); //empty
            WaitForSingleObject(sem[2],INFINITE); //empty
            WaitForSingleObject(sem[4],INFINITE); //empty
            WaitForSingleObject(sem[6],INFINITE); //empty
            WaitForSingleObject(sem[8],INFINITE); //empty
            {//explode_Trigger_MB
                memcpy(explode__5, &TriggerM_0[0], 64*sizeof(int)/*size*/);
                memcpy(explode__6, &TriggerM_0[64], 64*sizeof(int)/*size*/);
                memcpy(explode__0, &TriggerM_0[128], 64*sizeof(int)/*size*/);
                memcpy(explode__3, &TriggerM_0[192], 64*sizeof(int)/*size*/);
                memcpy(explode__8, &TriggerM_0[256], 64*sizeof(int)/*size*/);
                memcpy(explode__4, &TriggerM_0[320], 64*sizeof(int)/*size*/);
                memcpy(explode__2, &TriggerM_0[384], 64*sizeof(int)/*size*/);
                memcpy(explode__1, &TriggerM_0[448], 64*sizeof(int)/*size*/);
                memcpy(explode__7, &TriggerM_0[512], 64*sizeof(int)/*size*/);
                memcpy(explode__9, &TriggerM_0[576], 64*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[9],1,NULL); //full
            ReleaseSemaphore(sem[7],1,NULL); //full
            ReleaseSemaphore(sem[5],1,NULL); //full
            ReleaseSemaphore(sem[3],1,NULL); //full
            ReleaseSemaphore(sem[1],1,NULL); //full
            WaitForSingleObject(sem[10],INFINITE); //empty
            WaitForSingleObject(sem[12],INFINITE); //empty
            WaitForSingleObject(sem[14],INFINITE); //empty
            WaitForSingleObject(sem[16],INFINITE); //empty
            WaitForSingleObject(sem[18],INFINITE); //empty
            {//explode_Trigger_signed
                memcpy(explode_11, &Triggers_0[0], 1*sizeof(int)/*size*/);
                memcpy(explode_19, &Triggers_0[1], 1*sizeof(int)/*size*/);
                memcpy(explode_13, &Triggers_0[2], 1*sizeof(int)/*size*/);
                memcpy(explode_16, &Triggers_0[3], 1*sizeof(int)/*size*/);
                memcpy(explode_18, &Triggers_0[4], 1*sizeof(int)/*size*/);
                memcpy(explode_12, &Triggers_0[5], 1*sizeof(int)/*size*/);
                memcpy(explode_10, &Triggers_0[6], 1*sizeof(int)/*size*/);
                memcpy(explode_15, &Triggers_0[7], 1*sizeof(int)/*size*/);
                memcpy(explode_14, &Triggers_0[8], 1*sizeof(int)/*size*/);
                memcpy(explode_17, &Triggers_0[9], 1*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[19],1,NULL); //full
            ReleaseSemaphore(sem[17],1,NULL); //full
            ReleaseSemaphore(sem[15],1,NULL); //full
            ReleaseSemaphore(sem[13],1,NULL); //full
            ReleaseSemaphore(sem[11],1,NULL); //full
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
                        readBlock(explode__5, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode_11, IDCT2D_1_0);
            }
            {//IDCT2D_3
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
                        readBlock(explode__6, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode_19, IDCT2D_3_0);
            }
            {//IDCT2D_6
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
                        readBlock(explode__0, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode_13, IDCT2D_6_0);
            }
            {//IDCT2D_7
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
                        readBlock(explode__3, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode_16, IDCT2D_7_0);
            }
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
                        readBlock(explode__8, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode_18, IDCT2D_8_0);
            }
            WaitForSingleObject(sem[20],INFINITE); //full
            WaitForSingleObject(sem[22],INFINITE); //full
            WaitForSingleObject(sem[24],INFINITE); //full
            WaitForSingleObject(sem[26],INFINITE); //full
            WaitForSingleObject(sem[28],INFINITE); //full
            {//implode_Group_MB
                memcpy(&implode__0[0], IDCT2D_1_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[64], IDCT2D_3_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[128], IDCT2D_6_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[192], IDCT2D_7_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[256], IDCT2D_8_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[320], r_IDCT2D_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[384], r_IDCT2D_1, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[448], r_IDCT2D_2, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[512], r_IDCT2D_3, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[576], r_IDCT2D_4, 64*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[29],1,NULL); //empty
            ReleaseSemaphore(sem[27],1,NULL); //empty
            ReleaseSemaphore(sem[25],1,NULL); //empty
            ReleaseSemaphore(sem[23],1,NULL); //empty
            ReleaseSemaphore(sem[21],1,NULL); //empty
            group_bench(implode__0);
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core0( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_RCV,TCP,Core1,Core0);
            Com_Init(MEDIUM_SEND,TCP,Core0,Core1);
            ReleaseSemaphore(sem[21],1,NULL); //empty
            ReleaseSemaphore(sem[23],1,NULL); //empty
            ReleaseSemaphore(sem[25],1,NULL); //empty
            ReleaseSemaphore(sem[27],1,NULL); //empty
            ReleaseSemaphore(sem[29],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem[1],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__9,64*sizeof(int));
            ReleaseSemaphore(sem[0],1,NULL); //empty
            WaitForSingleObject(sem[3],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__7,64*sizeof(int));
            ReleaseSemaphore(sem[2],1,NULL); //empty
            WaitForSingleObject(sem[5],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__1,64*sizeof(int));
            ReleaseSemaphore(sem[4],1,NULL); //empty
            WaitForSingleObject(sem[7],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__2,64*sizeof(int));
            ReleaseSemaphore(sem[6],1,NULL); //empty
            WaitForSingleObject(sem[9],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__4,64*sizeof(int));
            ReleaseSemaphore(sem[8],1,NULL); //empty
            WaitForSingleObject(sem[11],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_17,1*sizeof(int));
            ReleaseSemaphore(sem[10],1,NULL); //empty
            WaitForSingleObject(sem[13],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_14,1*sizeof(int));
            ReleaseSemaphore(sem[12],1,NULL); //empty
            WaitForSingleObject(sem[15],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_15,1*sizeof(int));
            ReleaseSemaphore(sem[14],1,NULL); //empty
            WaitForSingleObject(sem[17],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_10,1*sizeof(int));
            ReleaseSemaphore(sem[16],1,NULL); //empty
            WaitForSingleObject(sem[19],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_12,1*sizeof(int));
            ReleaseSemaphore(sem[18],1,NULL); //empty
            WaitForSingleObject(sem[21],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_0,64*sizeof(int));
            ReleaseSemaphore(sem[20],1,NULL); //full
            WaitForSingleObject(sem[23],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_1,64*sizeof(int));
            ReleaseSemaphore(sem[22],1,NULL); //full
            WaitForSingleObject(sem[25],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_2,64*sizeof(int));
            ReleaseSemaphore(sem[24],1,NULL); //full
            WaitForSingleObject(sem[27],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_3,64*sizeof(int));
            ReleaseSemaphore(sem[26],1,NULL); //full
            WaitForSingleObject(sem[29],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_4,64*sizeof(int));
            ReleaseSemaphore(sem[28],1,NULL); //full
        }

        return 0;
    }//communicationThread

