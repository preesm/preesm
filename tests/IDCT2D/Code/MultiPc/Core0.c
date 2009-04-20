    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    int TriggerM_0[64];
    int Triggers_0[1];
    int IDCT2D_0_0[64];
    int broadcas_0[1];
    int IDCT2D_0_1[64];
    int IDCT2D_0_2[64];
    int r_explod_0[1];
    int r_explod_1[1];
    int IDCT2D_0_3[64];
    int explode__0[8];
    int explode__1[8];
    int explode__2[8];
    int explode__3[8];
    int explode__4[8];
    int explode__5[8];
    int explode__6[8];
    int explode__7[8];
    int IDCT2D_0_4[8];
    int IDCT2D_0_5[8];
    int r_IDCT2D_0[8];
    int r_IDCT2D_1[8];
    int r_IDCT2D_2[8];
    int r_IDCT2D_3[8];
    int r_IDCT2D_4[8];
    int r_IDCT2D_5[8];
    int implode__0[64];
    int IDCT2D_0_6[64];
    int IDCT2D_0_7[64];
    int IDCT2D_0_8[64];
    int IDCT2D_0_9[64];
    int explode__8[8];
    int explode__9[8];
    int explode_10[8];
    int explode_11[8];
    int explode_12[8];
    int explode_13[8];
    int explode_14[8];
    int explode_15[8];
    int IDCT2D__10[8];
    int IDCT2D__11[8];
    int r_IDCT2D_6[8];
    int r_IDCT2D_7[8];
    int r_IDCT2D_8[8];
    int r_IDCT2D_9[8];
    int r_IDCT2_10[8];
    int r_IDCT2_11[8];
    int implode__1[64];
    int IDCT2D__12[64];
    int IDCT2D__13[64];
    int IDCT2D__14[64];
    int IDCT2D__15[64];
    int IDCT2D__16[64];
    semaphore sem[52];

    DWORD WINAPI computationThread_Core0( LPVOID lpParam );
    DWORD WINAPI communicationThread_Core0( LPVOID lpParam );

    DWORD WINAPI computationThread_Core0( LPVOID lpParam ){
        // Buffer declarations

        {
            semaphoreInit(sem, 52/*semNumber*/, Core0);
            CreateThread(NULL,8000,communicationThread_Core0,NULL,0,NULL);
            ReleaseSemaphore(sem[2],1,NULL); //empty
            ReleaseSemaphore(sem[4],1,NULL); //empty
            ReleaseSemaphore(sem[6],1,NULL); //empty
            ReleaseSemaphore(sem[8],1,NULL); //empty
            ReleaseSemaphore(sem[10],1,NULL); //empty
            ReleaseSemaphore(sem[12],1,NULL); //empty
            ReleaseSemaphore(sem[28],1,NULL); //empty
            ReleaseSemaphore(sem[30],1,NULL); //empty
            ReleaseSemaphore(sem[32],1,NULL); //empty
            ReleaseSemaphore(sem[34],1,NULL); //empty
            ReleaseSemaphore(sem[36],1,NULL); //empty
            ReleaseSemaphore(sem[38],1,NULL); //empty
        }

        for(;;){
            init_in_2(IDCT2D_0_0, 64/*init_size*/);
            trigger_bench(TriggerM_0, Triggers_0);
            {//IDCT2D_0_IDCT2D_basic_0_broadcast_blockIn
                memcpy(IDCT2D_0_2, TriggerM_0, 64*sizeof(int)/*size*/);
                memcpy(IDCT2D_0_1, TriggerM_0, 64*sizeof(int)/*size*/);
            }
            {//broadcast_signed
                memcpy(broadcas_0, Triggers_0, 1*sizeof(int)/*size*/);
            }
            WaitForSingleObject(sem[0],INFINITE); //full
            readBlock(IDCT2D_0_2, IDCT2D_0_0, r_explod_0, IDCT2D_0_3);
            ReleaseSemaphore(sem[1],1,NULL); //empty
            WaitForSingleObject(sem[2],INFINITE); //empty
            WaitForSingleObject(sem[4],INFINITE); //empty
            WaitForSingleObject(sem[6],INFINITE); //empty
            WaitForSingleObject(sem[8],INFINITE); //empty
            WaitForSingleObject(sem[10],INFINITE); //empty
            WaitForSingleObject(sem[12],INFINITE); //empty
            {//explode_IDCT2D_0_IDCT2D_basic_0_readBlock_0_out_1
                memcpy(explode__2, &IDCT2D_0_3[0], 8*sizeof(int)/*size*/);
                memcpy(explode__3, &IDCT2D_0_3[8], 8*sizeof(int)/*size*/);
                memcpy(explode__0, &IDCT2D_0_3[16], 8*sizeof(int)/*size*/);
                memcpy(explode__7, &IDCT2D_0_3[24], 8*sizeof(int)/*size*/);
                memcpy(explode__4, &IDCT2D_0_3[32], 8*sizeof(int)/*size*/);
                memcpy(explode__5, &IDCT2D_0_3[40], 8*sizeof(int)/*size*/);
                memcpy(explode__6, &IDCT2D_0_3[48], 8*sizeof(int)/*size*/);
                memcpy(explode__1, &IDCT2D_0_3[56], 8*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[13],1,NULL); //full
            ReleaseSemaphore(sem[11],1,NULL); //full
            ReleaseSemaphore(sem[9],1,NULL); //full
            ReleaseSemaphore(sem[7],1,NULL); //full
            ReleaseSemaphore(sem[5],1,NULL); //full
            ReleaseSemaphore(sem[3],1,NULL); //full
            idct1d(explode__2, IDCT2D_0_4);
            idct1d(explode__3, IDCT2D_0_5);
            WaitForSingleObject(sem[14],INFINITE); //full
            WaitForSingleObject(sem[16],INFINITE); //full
            WaitForSingleObject(sem[18],INFINITE); //full
            WaitForSingleObject(sem[20],INFINITE); //full
            WaitForSingleObject(sem[22],INFINITE); //full
            WaitForSingleObject(sem[24],INFINITE); //full
            {//implode_IDCT2D_0_IDCT2D_basic_0_Transpose_0_blockIn
                memcpy(&implode__0[0], IDCT2D_0_4, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[8], IDCT2D_0_5, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[16], r_IDCT2D_0, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[24], r_IDCT2D_1, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[32], r_IDCT2D_2, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[40], r_IDCT2D_3, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[48], r_IDCT2D_4, 8*sizeof(int)/*size*/);
                memcpy(&implode__0[56], r_IDCT2D_5, 8*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[25],1,NULL); //empty
            ReleaseSemaphore(sem[23],1,NULL); //empty
            ReleaseSemaphore(sem[21],1,NULL); //empty
            ReleaseSemaphore(sem[19],1,NULL); //empty
            ReleaseSemaphore(sem[17],1,NULL); //empty
            ReleaseSemaphore(sem[15],1,NULL); //empty
            transpose(implode__0, IDCT2D_0_6);
            {//IDCT2D_0_IDCT2D_basic_0_broadcast_block_0
                memcpy(IDCT2D_0_7, IDCT2D_0_6, 64*sizeof(int)/*size*/);
                memcpy(IDCT2D_0_8, IDCT2D_0_6, 64*sizeof(int)/*size*/);
            }
            WaitForSingleObject(sem[26],INFINITE); //full
            readBlock(IDCT2D_0_1, IDCT2D_0_7, r_explod_1, IDCT2D_0_9);
            ReleaseSemaphore(sem[27],1,NULL); //empty
            WaitForSingleObject(sem[28],INFINITE); //empty
            WaitForSingleObject(sem[30],INFINITE); //empty
            WaitForSingleObject(sem[32],INFINITE); //empty
            WaitForSingleObject(sem[34],INFINITE); //empty
            WaitForSingleObject(sem[36],INFINITE); //empty
            WaitForSingleObject(sem[38],INFINITE); //empty
            {//explode_IDCT2D_0_IDCT2D_basic_0_readBlock_1_out_1
                memcpy(explode__8, &IDCT2D_0_9[0], 8*sizeof(int)/*size*/);
                memcpy(explode_10, &IDCT2D_0_9[8], 8*sizeof(int)/*size*/);
                memcpy(explode_13, &IDCT2D_0_9[16], 8*sizeof(int)/*size*/);
                memcpy(explode_12, &IDCT2D_0_9[24], 8*sizeof(int)/*size*/);
                memcpy(explode_11, &IDCT2D_0_9[32], 8*sizeof(int)/*size*/);
                memcpy(explode_15, &IDCT2D_0_9[40], 8*sizeof(int)/*size*/);
                memcpy(explode__9, &IDCT2D_0_9[48], 8*sizeof(int)/*size*/);
                memcpy(explode_14, &IDCT2D_0_9[56], 8*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[39],1,NULL); //full
            ReleaseSemaphore(sem[37],1,NULL); //full
            ReleaseSemaphore(sem[35],1,NULL); //full
            ReleaseSemaphore(sem[33],1,NULL); //full
            ReleaseSemaphore(sem[31],1,NULL); //full
            ReleaseSemaphore(sem[29],1,NULL); //full
            idct1d(explode_10, IDCT2D__11);
            idct1d(explode__8, IDCT2D__10);
            WaitForSingleObject(sem[40],INFINITE); //full
            WaitForSingleObject(sem[42],INFINITE); //full
            WaitForSingleObject(sem[44],INFINITE); //full
            WaitForSingleObject(sem[46],INFINITE); //full
            WaitForSingleObject(sem[48],INFINITE); //full
            WaitForSingleObject(sem[50],INFINITE); //full
            {//implode_IDCT2D_0_IDCT2D_basic_0_Transpose_1_blockIn
                memcpy(&implode__1[0], IDCT2D__10, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[8], IDCT2D__11, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[16], r_IDCT2D_6, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[24], r_IDCT2D_7, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[32], r_IDCT2D_8, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[40], r_IDCT2D_9, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[48], r_IDCT2_10, 8*sizeof(int)/*size*/);
                memcpy(&implode__1[56], r_IDCT2_11, 8*sizeof(int)/*size*/);
            }
            ReleaseSemaphore(sem[51],1,NULL); //empty
            ReleaseSemaphore(sem[49],1,NULL); //empty
            ReleaseSemaphore(sem[47],1,NULL); //empty
            ReleaseSemaphore(sem[45],1,NULL); //empty
            ReleaseSemaphore(sem[43],1,NULL); //empty
            ReleaseSemaphore(sem[41],1,NULL); //empty
            transpose(implode__1, IDCT2D__12);
            {//IDCT2D_0_IDCT2D_basic_0_broadcast_block_1
                memcpy(IDCT2D__13, IDCT2D__12, 64*sizeof(int)/*size*/);
                memcpy(IDCT2D__14, IDCT2D__12, 64*sizeof(int)/*size*/);
            }
            {//IDCT2D_0_IDCT2D_basic_0_roundBuffer_block_out
                memcpy(&IDCT2D__15[0], IDCT2D_0_8, 64*sizeof(int)/*size*/);
                memcpy(&IDCT2D__15[64], IDCT2D__13, 64*sizeof(int)/*size*/);
            }
            clip(IDCT2D__15, broadcas_0, IDCT2D__16);
            group_bench(IDCT2D__16);
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_Core0( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_RCV,TCP,Core1,Core0);
            Com_Init(MEDIUM_RCV,TCP,Core3,Core0);
            Com_Init(MEDIUM_SEND,TCP,Core0,Core1);
            Com_Init(MEDIUM_SEND,TCP,Core0,Core3);
            Com_Init(MEDIUM_SEND,TCP,Core0,Core2);
            Com_Init(MEDIUM_RCV,TCP,Core2,Core0);
            ReleaseSemaphore(sem[27],1,NULL); //empty
            ReleaseSemaphore(sem[1],1,NULL); //empty
            ReleaseSemaphore(sem[15],1,NULL); //empty
            ReleaseSemaphore(sem[17],1,NULL); //empty
            ReleaseSemaphore(sem[19],1,NULL); //empty
            ReleaseSemaphore(sem[21],1,NULL); //empty
            ReleaseSemaphore(sem[23],1,NULL); //empty
            ReleaseSemaphore(sem[25],1,NULL); //empty
            ReleaseSemaphore(sem[41],1,NULL); //empty
            ReleaseSemaphore(sem[43],1,NULL); //empty
            ReleaseSemaphore(sem[45],1,NULL); //empty
            ReleaseSemaphore(sem[47],1,NULL); //empty
            ReleaseSemaphore(sem[49],1,NULL); //empty
            ReleaseSemaphore(sem[51],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem[27],INFINITE); //empty
            receiveData(TCP,Core2,Core0,r_explod_1,1*sizeof(int));
            ReleaseSemaphore(sem[26],1,NULL); //full
            WaitForSingleObject(sem[1],INFINITE); //empty
            receiveData(TCP,Core2,Core0,r_explod_0,1*sizeof(int));
            ReleaseSemaphore(sem[0],1,NULL); //full
            WaitForSingleObject(sem[3],INFINITE); //full
            sendData(TCP,Core0,Core2,explode__1,8*sizeof(int));
            ReleaseSemaphore(sem[2],1,NULL); //empty
            WaitForSingleObject(sem[5],INFINITE); //full
            sendData(TCP,Core0,Core3,explode__6,8*sizeof(int));
            ReleaseSemaphore(sem[4],1,NULL); //empty
            WaitForSingleObject(sem[7],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__5,8*sizeof(int));
            ReleaseSemaphore(sem[6],1,NULL); //empty
            WaitForSingleObject(sem[9],INFINITE); //full
            sendData(TCP,Core0,Core2,explode__4,8*sizeof(int));
            ReleaseSemaphore(sem[8],1,NULL); //empty
            WaitForSingleObject(sem[11],INFINITE); //full
            sendData(TCP,Core0,Core1,explode__7,8*sizeof(int));
            ReleaseSemaphore(sem[10],1,NULL); //empty
            WaitForSingleObject(sem[13],INFINITE); //full
            sendData(TCP,Core0,Core3,explode__0,8*sizeof(int));
            ReleaseSemaphore(sem[12],1,NULL); //empty
            WaitForSingleObject(sem[15],INFINITE); //empty
            receiveData(TCP,Core3,Core0,r_IDCT2D_0,8*sizeof(int));
            ReleaseSemaphore(sem[14],1,NULL); //full
            WaitForSingleObject(sem[17],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_1,8*sizeof(int));
            ReleaseSemaphore(sem[16],1,NULL); //full
            WaitForSingleObject(sem[19],INFINITE); //empty
            receiveData(TCP,Core2,Core0,r_IDCT2D_2,8*sizeof(int));
            ReleaseSemaphore(sem[18],1,NULL); //full
            WaitForSingleObject(sem[21],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_3,8*sizeof(int));
            ReleaseSemaphore(sem[20],1,NULL); //full
            WaitForSingleObject(sem[23],INFINITE); //empty
            receiveData(TCP,Core3,Core0,r_IDCT2D_4,8*sizeof(int));
            ReleaseSemaphore(sem[22],1,NULL); //full
            WaitForSingleObject(sem[25],INFINITE); //empty
            receiveData(TCP,Core2,Core0,r_IDCT2D_5,8*sizeof(int));
            ReleaseSemaphore(sem[24],1,NULL); //full
            WaitForSingleObject(sem[29],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_14,8*sizeof(int));
            ReleaseSemaphore(sem[28],1,NULL); //empty
            WaitForSingleObject(sem[31],INFINITE); //full
            sendData(TCP,Core0,Core3,explode__9,8*sizeof(int));
            ReleaseSemaphore(sem[30],1,NULL); //empty
            WaitForSingleObject(sem[33],INFINITE); //full
            sendData(TCP,Core0,Core2,explode_15,8*sizeof(int));
            ReleaseSemaphore(sem[32],1,NULL); //empty
            WaitForSingleObject(sem[35],INFINITE); //full
            sendData(TCP,Core0,Core2,explode_11,8*sizeof(int));
            ReleaseSemaphore(sem[34],1,NULL); //empty
            WaitForSingleObject(sem[37],INFINITE); //full
            sendData(TCP,Core0,Core3,explode_12,8*sizeof(int));
            ReleaseSemaphore(sem[36],1,NULL); //empty
            WaitForSingleObject(sem[39],INFINITE); //full
            sendData(TCP,Core0,Core1,explode_13,8*sizeof(int));
            ReleaseSemaphore(sem[38],1,NULL); //empty
            WaitForSingleObject(sem[41],INFINITE); //empty
            receiveData(TCP,Core3,Core0,r_IDCT2D_7,8*sizeof(int));
            ReleaseSemaphore(sem[40],1,NULL); //full
            WaitForSingleObject(sem[43],INFINITE); //empty
            receiveData(TCP,Core2,Core0,r_IDCT2D_8,8*sizeof(int));
            ReleaseSemaphore(sem[42],1,NULL); //full
            WaitForSingleObject(sem[45],INFINITE); //empty
            receiveData(TCP,Core2,Core0,r_IDCT2D_9,8*sizeof(int));
            ReleaseSemaphore(sem[44],1,NULL); //full
            WaitForSingleObject(sem[47],INFINITE); //empty
            receiveData(TCP,Core3,Core0,r_IDCT2_10,8*sizeof(int));
            ReleaseSemaphore(sem[46],1,NULL); //full
            WaitForSingleObject(sem[49],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2_11,8*sizeof(int));
            ReleaseSemaphore(sem[48],1,NULL); //full
            WaitForSingleObject(sem[51],INFINITE); //empty
            receiveData(TCP,Core1,Core0,r_IDCT2D_6,8*sizeof(int));
            ReleaseSemaphore(sem[50],1,NULL); //full
        }

        return 0;
    }//communicationThread

