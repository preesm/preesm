    #include "x86.h"

    // Buffer declarations
    LAR ReadParamGlobalbro_0[1];
    LAR bro_01s_bro_01Code_0[1];
    LAR *bro_01Codeur_Y_0_C_0;
    LAR bro_01s_bro_01Code_1[1];
    LAR bro_01s_bro_01Code_2[1];
    LAR *bro_01Codeur_Y_0_C_1;
    LAR *bro_01Codeur_Y_0_C_2;
    LAR bro_01s_bro_01Code_3[1];
    LAR bro_01s_bro_01Code_4[1];
    LAR *bro_01Decodeur_Y_0_0;
    LAR bro_01s_bro_01Code_5[1];
    LAR bro_01s_bro_01Code_6[1];
    LAR bro_01s_bro_01Deco_0[1];
    LAR bro_01s_bro_01Code_7[1];
    LAR bro_01s_bro_01Deco_1[1];
    LAR *bro_01Codeur_Y_0_C_3;
    LAR bro_01s_bro_01Code_8[1];
    LAR bro_01s_bro_01Code_9[1];
    LAR *bro_01Codeur_Y_0_C_4;
    LAR bro_01s_bro_01Deco_2[1];
    LAR *bro_01Codeur_Y_0_C_5;
    LAR bro_01s_bro_01Cod_10[1];
    LAR bro_01s_bro_01Cod_11[1];
    LAR *bro_01Codeur_Y_0_C_6;
    unsigned char Codeur_Y_0_Codeur_16[25344];
    int Codeur_Y_0_Codeur_17[1];
    unsigned char r_Codeur_Y_0_Codeu_6[6336];
    unsigned char r_Codeur_Y_0_Codeu_7[6336];
    unsigned char Codeur_Y_0_Codeur_18[1584];
    unsigned char Codeur_Y_0_Codeur_19[1584];
    unsigned char Codeur_Y_0_Codeur_20[1584];
    unsigned char *Codeur_Y_0_Codeur_21;
    unsigned char Codeur_Y_0_Codeur_22[396];
    unsigned char r_Codeur_Y_0_Codeu_8[1584];
    unsigned char Codeur_Y_0_Codeur_23[396];
    int r_Codeur_Y_0_Codeu_9[1];
    unsigned char r_Codeur_Y_0_broad_0[25344];
    unsigned char Codeur_Y_0_Codage_34[25344];
    int Codeur_Y_0_Codage_35[1];
    unsigned char Codeur_Y_0_Codage_36[25344];
    int Codeur_Y_0_Codage_37[1];
    int Codeur_Y_0_Codage_38[1];
    unsigned char Codeur_Y_0_Codage_39[25344];
    unsigned char r_Codeur_Y_0_Codag_3[26280];
    int Codeur_Y_0_Codage_40[1];
    unsigned char Codeur_Y_0_Codage_41[3168];
    unsigned char Codeur_Y_0_Codage_42[3168];
    int Codeur_Y_0_Codage_43[1];
    int Codeur_Y_0_Codage_44[1];
    unsigned char Codeur_Y_0_Codage_45[3168];
    int r_Codeur_Y_0_Codag_4[1];
    unsigned char r_Codeur_Y_0_Codag_5[6336];
    unsigned char Codeur_Y_0_Codage_46[25344];
    unsigned char Codeur_Y_0_Codage_47[1040];
    int Codeur_Y_0_Codage_48[1];
    int Codeur_Y_0_Codage_49[1];
    unsigned char Codeur_Y_0_Codage_50[6336];
    int Decodeur_Y_0_Deco_34[1];
    unsigned char Decodeur_Y_0_Deco_35[25344];
    int Decodeur_Y_0_Deco_36[1];
    int Decodeur_Y_0_Deco_37[1];
    unsigned char Decodeur_Y_0_Deco_38[25344];
    unsigned char Decodeur_Y_0_Deco_39[25344];
    unsigned char Decodeur_Y_0_Deco_40[3168];
    int Decodeur_Y_0_Deco_41[1];
    unsigned char Decodeur_Y_0_Deco_42[3168];
    int Decodeur_Y_0_Deco_43[1];
    unsigned char Decodeur_Y_0_Deco_44[3168];
    int Decodeur_Y_0_Deco_45[1];
    unsigned char Decodeur_Y_0_Deco_46[25344];
    unsigned char Decodeur_Y_0_Bros__0[25344];
    unsigned char *Decodeur_Y_0_BroDe_0;
    unsigned char Decodeur_Y_0_Bros__1[25344];
    int r_Codeur_Y_0_Codag_6[1];
    int r_Decodeur_Y_0_Dec_2[1];
    unsigned char r_Decodeur_Y_0_Dec_3[792];
    int r_Decodeur_Y_0_Dec_4[4];
    unsigned char Decodeur_Y_0_Deco_47[6336];
    int Decodeur_Y_0_Deco_48[1];
    semaphore sem_0[70];

    DWORD WINAPI computationThread_x86_2( LPVOID lpParam );
    DWORD WINAPI communicationThread_x86_2( LPVOID lpParam );

    DWORD WINAPI computationThread_x86_2( LPVOID lpParam ){
        // Buffer declarations

        {
            semaphoreInit(sem_0, 70/*semNumber*/, x86_2);
            CreateThread(NULL,8000,communicationThread_x86_2,NULL,0,NULL);
            Read_LAR_Param_no_args(ReadParamGlobalbro_0);
            bidon();
            Display_Y_init_nocomment(1/*Number*/, 176/*xsize*/, 144/*ysize*/);
            bidon();
            bidon();
            bidon();
            ReleaseSemaphore(sem_0[0],1,NULL); //empty
            ReleaseSemaphore(sem_0[2],1,NULL); //empty
            ReleaseSemaphore(sem_0[4],1,NULL); //empty
            ReleaseSemaphore(sem_0[6],1,NULL); //empty
            ReleaseSemaphore(sem_0[8],1,NULL); //empty
            ReleaseSemaphore(sem_0[10],1,NULL); //empty
            ReleaseSemaphore(sem_0[12],1,NULL); //empty
            ReleaseSemaphore(sem_0[14],1,NULL); //empty
            ReleaseSemaphore(sem_0[16],1,NULL); //empty
            ReleaseSemaphore(sem_0[18],1,NULL); //empty
            ReleaseSemaphore(sem_0[20],1,NULL); //empty
            ReleaseSemaphore(sem_0[22],1,NULL); //empty
            ReleaseSemaphore(sem_0[24],1,NULL); //empty
            ReleaseSemaphore(sem_0[26],1,NULL); //empty
            ReleaseSemaphore(sem_0[28],1,NULL); //empty
            ReleaseSemaphore(sem_0[30],1,NULL); //empty
            ReleaseSemaphore(sem_0[36],1,NULL); //empty
            ReleaseSemaphore(sem_0[38],1,NULL); //empty
            ReleaseSemaphore(sem_0[40],1,NULL); //empty
            ReleaseSemaphore(sem_0[44],1,NULL); //empty
            ReleaseSemaphore(sem_0[50],1,NULL); //empty
            ReleaseSemaphore(sem_0[54],1,NULL); //empty
            ReleaseSemaphore(sem_0[56],1,NULL); //empty
            ReleaseSemaphore(sem_0[58],1,NULL); //empty
            ReleaseSemaphore(sem_0[62],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_0[0],INFINITE); //empty
            WaitForSingleObject(sem_0[2],INFINITE); //empty
            WaitForSingleObject(sem_0[4],INFINITE); //empty
            WaitForSingleObject(sem_0[6],INFINITE); //empty
            WaitForSingleObject(sem_0[8],INFINITE); //empty
            WaitForSingleObject(sem_0[10],INFINITE); //empty
            WaitForSingleObject(sem_0[12],INFINITE); //empty
            WaitForSingleObject(sem_0[14],INFINITE); //empty
            WaitForSingleObject(sem_0[16],INFINITE); //empty
            WaitForSingleObject(sem_0[18],INFINITE); //empty
            WaitForSingleObject(sem_0[20],INFINITE); //empty
            WaitForSingleObject(sem_0[22],INFINITE); //empty
            WaitForSingleObject(sem_0[24],INFINITE); //empty
            WaitForSingleObject(sem_0[26],INFINITE); //empty
            WaitForSingleObject(sem_0[28],INFINITE); //empty
            {//bro_01
                bro_01Codeur_Y_0_C_4 = &ReadParamGlobalbro_0[0];
                bro_01Codeur_Y_0_C_0 = &ReadParamGlobalbro_0[0];
                bro_01Codeur_Y_0_C_3 = &ReadParamGlobalbro_0[0];
                bro_01Codeur_Y_0_C_6 = &ReadParamGlobalbro_0[0];
                bro_01Codeur_Y_0_C_2 = &ReadParamGlobalbro_0[0];
                bro_01Codeur_Y_0_C_1 = &ReadParamGlobalbro_0[0];
                bro_01Codeur_Y_0_C_5 = &ReadParamGlobalbro_0[0];
                bro_01Decodeur_Y_0_0 = &ReadParamGlobalbro_0[0];
                memcpy(bro_01s_bro_01Cod_11, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Cod_10, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_6, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_5, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_7, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_0, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_2, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_4, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_9, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_1, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_8, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Code_3, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Deco_0, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Deco_1, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
                memcpy(bro_01s_bro_01Deco_2, ReadParamGlobalbro_0, 1*sizeof(LAR)/*size*/);
            }
            ReleaseSemaphore(sem_0[29],1,NULL); //full
            ReleaseSemaphore(sem_0[27],1,NULL); //full
            ReleaseSemaphore(sem_0[25],1,NULL); //full
            ReleaseSemaphore(sem_0[23],1,NULL); //full
            ReleaseSemaphore(sem_0[21],1,NULL); //full
            ReleaseSemaphore(sem_0[19],1,NULL); //full
            ReleaseSemaphore(sem_0[17],1,NULL); //full
            ReleaseSemaphore(sem_0[15],1,NULL); //full
            ReleaseSemaphore(sem_0[13],1,NULL); //full
            ReleaseSemaphore(sem_0[11],1,NULL); //full
            ReleaseSemaphore(sem_0[9],1,NULL); //full
            ReleaseSemaphore(sem_0[7],1,NULL); //full
            ReleaseSemaphore(sem_0[5],1,NULL); //full
            ReleaseSemaphore(sem_0[3],1,NULL); //full
            ReleaseSemaphore(sem_0[1],1,NULL); //full
            WaitForSingleObject(sem_0[30],INFINITE); //empty
            Init_im_taille(bro_01Codeur_Y_0_C_4, 1/*ratio*/, Codeur_Y_0_Codeur_17, Codeur_Y_0_Codeur_16);
            ReleaseSemaphore(sem_0[31],1,NULL); //full
            WaitForSingleObject(sem_0[32],INFINITE); //full
            Max2x2(bro_01Codeur_Y_0_C_3, 3/*k*/, 1/*ratio*/, r_Codeur_Y_0_Codeu_6, Codeur_Y_0_Codeur_18);
            ReleaseSemaphore(sem_0[33],1,NULL); //empty
            WaitForSingleObject(sem_0[34],INFINITE); //full
            WaitForSingleObject(sem_0[36],INFINITE); //empty
            Min2x2(bro_01Codeur_Y_0_C_0, 3/*k*/, 1/*ratio*/, r_Codeur_Y_0_Codeu_7, Codeur_Y_0_Codeur_19);
            ReleaseSemaphore(sem_0[37],1,NULL); //full
            ReleaseSemaphore(sem_0[35],1,NULL); //empty
            WaitForSingleObject(sem_0[38],INFINITE); //empty
            {//Codeur_Y_0_Codeur_im_Y_0_bro_3
                Codeur_Y_0_Codeur_21 = &Codeur_Y_0_Codeur_18[0];
                memcpy(Codeur_Y_0_Codeur_20, Codeur_Y_0_Codeur_18, 1584*sizeof(unsigned char)/*size*/);
            }
            ReleaseSemaphore(sem_0[39],1,NULL); //full
            WaitForSingleObject(sem_0[40],INFINITE); //empty
            Max2x2(bro_01Codeur_Y_0_C_6, 4/*k*/, 1/*ratio*/, Codeur_Y_0_Codeur_21, Codeur_Y_0_Codeur_22);
            ReleaseSemaphore(sem_0[41],1,NULL); //full
            WaitForSingleObject(sem_0[42],INFINITE); //full
            WaitForSingleObject(sem_0[44],INFINITE); //empty
            Min2x2(bro_01Codeur_Y_0_C_2, 4/*k*/, 1/*ratio*/, r_Codeur_Y_0_Codeu_8, Codeur_Y_0_Codeur_23);
            ReleaseSemaphore(sem_0[45],1,NULL); //full
            ReleaseSemaphore(sem_0[43],1,NULL); //empty
            WaitForSingleObject(sem_0[46],INFINITE); //full
            Code_taille_quadtree_16(bro_01Codeur_Y_0_C_5, 1/*ratio*/, r_Codeur_Y_0_broad_0, Codeur_Y_0_Codage_39, Codeur_Y_0_Codage_35, Codeur_Y_0_Codage_34, Codeur_Y_0_Codage_37, Codeur_Y_0_Codage_36, Codeur_Y_0_Codage_38);
            ReleaseSemaphore(sem_0[47],1,NULL); //empty
            Bin_to_Uchar(Codeur_Y_0_Codage_39, Codeur_Y_0_Codage_35, Codeur_Y_0_Codage_41, Codeur_Y_0_Codage_40);
            Bin_to_Uchar(Codeur_Y_0_Codage_34, Codeur_Y_0_Codage_37, Codeur_Y_0_Codage_42, Codeur_Y_0_Codage_43);
            Bin_to_Uchar(Codeur_Y_0_Codage_36, Codeur_Y_0_Codage_38, Codeur_Y_0_Codage_45, Codeur_Y_0_Codage_44);
            WaitForSingleObject(sem_0[48],INFINITE); //full
            Create_im_quant(bro_01Codeur_Y_0_C_1, 1/*ratio*/, r_Codeur_Y_0_Codag_3, Codeur_Y_0_Codage_46);
            ReleaseSemaphore(sem_0[49],1,NULL); //empty
            WaitForSingleObject(sem_0[50],INFINITE); //empty
            Concat_stream_quad_16(Codeur_Y_0_Codage_41, Codeur_Y_0_Codage_40, Codeur_Y_0_Codage_42, Codeur_Y_0_Codage_43, Codeur_Y_0_Codage_45, Codeur_Y_0_Codage_44, Codeur_Y_0_Codage_47, Codeur_Y_0_Codage_48);
            ReleaseSemaphore(sem_0[51],1,NULL); //full
            WaitForSingleObject(sem_0[52],INFINITE); //full
            WaitForSingleObject(sem_0[54],INFINITE); //empty
            Bin_to_Uchar(r_Codeur_Y_0_Codag_5, r_Codeur_Y_0_Codag_4, Codeur_Y_0_Codage_50, Codeur_Y_0_Codage_49);
            ReleaseSemaphore(sem_0[55],1,NULL); //full
            ReleaseSemaphore(sem_0[53],1,NULL); //empty
            Explode_stream_quad_16(Codeur_Y_0_Codage_47, Decodeur_Y_0_Deco_39, Decodeur_Y_0_Deco_37, Decodeur_Y_0_Deco_35, Decodeur_Y_0_Deco_36, Decodeur_Y_0_Deco_38, Decodeur_Y_0_Deco_34);
            Display_Y(1/*Number*/, Codeur_Y_0_Codage_46);
            Uchar_to_Bin(Decodeur_Y_0_Deco_39, Decodeur_Y_0_Deco_37, Decodeur_Y_0_Deco_40, Decodeur_Y_0_Deco_41);
            Uchar_to_Bin(Decodeur_Y_0_Deco_35, Decodeur_Y_0_Deco_36, Decodeur_Y_0_Deco_42, Decodeur_Y_0_Deco_43);
            Uchar_to_Bin(Decodeur_Y_0_Deco_38, Decodeur_Y_0_Deco_34, Decodeur_Y_0_Deco_44, Decodeur_Y_0_Deco_45);
            Decode_taille_quadtree_16(bro_01Decodeur_Y_0_0, 1/*ratio*/, Decodeur_Y_0_Deco_40, Decodeur_Y_0_Deco_42, Decodeur_Y_0_Deco_44, Decodeur_Y_0_Deco_46);
            WaitForSingleObject(sem_0[56],INFINITE); //empty
            WaitForSingleObject(sem_0[58],INFINITE); //empty
            {//Decodeur_Y_0_Bro
                Decodeur_Y_0_BroDe_0 = &Decodeur_Y_0_Deco_46[0];
                memcpy(Decodeur_Y_0_Bros__1, Decodeur_Y_0_Deco_46, 25344*sizeof(unsigned char)/*size*/);
                memcpy(Decodeur_Y_0_Bros__0, Decodeur_Y_0_Deco_46, 25344*sizeof(unsigned char)/*size*/);
            }
            ReleaseSemaphore(sem_0[59],1,NULL); //full
            ReleaseSemaphore(sem_0[57],1,NULL); //full
            WaitForSingleObject(sem_0[60],INFINITE); //full
            WaitForSingleObject(sem_0[62],INFINITE); //empty
            Uchar_to_Bin(r_Decodeur_Y_0_Dec_3, r_Decodeur_Y_0_Dec_2, Decodeur_Y_0_Deco_47, Decodeur_Y_0_Deco_48);
            ReleaseSemaphore(sem_0[63],1,NULL); //full
            ReleaseSemaphore(sem_0[61],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_x86_2( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_RCV,TCP,x86_1,x86_2);
            Com_Init(MEDIUM_SEND,TCP,x86_2,x86_1);
            ReleaseSemaphore(sem_0[64],1,NULL); //empty
            ReleaseSemaphore(sem_0[49],1,NULL); //empty
            ReleaseSemaphore(sem_0[53],1,NULL); //empty
            ReleaseSemaphore(sem_0[66],1,NULL); //empty
            ReleaseSemaphore(sem_0[68],1,NULL); //empty
            ReleaseSemaphore(sem_0[61],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem_0[31],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codeur_17,1*sizeof(int));
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codeur_16,25344*sizeof(unsigned char));
            ReleaseSemaphore(sem_0[30],1,NULL); //empty
            WaitForSingleObject(sem_0[37],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codeur_19,1584*sizeof(unsigned char));
            ReleaseSemaphore(sem_0[36],1,NULL); //empty
            WaitForSingleObject(sem_0[41],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codeur_22,396*sizeof(unsigned char));
            ReleaseSemaphore(sem_0[40],1,NULL); //empty
            WaitForSingleObject(sem_0[45],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codeur_23,396*sizeof(unsigned char));
            ReleaseSemaphore(sem_0[44],1,NULL); //empty
            WaitForSingleObject(sem_0[64],INFINITE); //empty
            receiveData(TCP,x86_1,x86_2,r_Codeur_Y_0_Codeu_9,1*sizeof(int));
            ReleaseSemaphore(sem_0[65],1,NULL); //full
            WaitForSingleObject(sem_0[49],INFINITE); //empty
            receiveData(TCP,x86_1,x86_2,r_Codeur_Y_0_Codag_3,26280*sizeof(unsigned char));
            ReleaseSemaphore(sem_0[48],1,NULL); //full
            WaitForSingleObject(sem_0[51],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codage_48,1*sizeof(int));
            ReleaseSemaphore(sem_0[50],1,NULL); //empty
            WaitForSingleObject(sem_0[53],INFINITE); //empty
            receiveData(TCP,x86_1,x86_2,r_Codeur_Y_0_Codag_5,6336*sizeof(unsigned char));
            receiveData(TCP,x86_1,x86_2,r_Codeur_Y_0_Codag_4,1*sizeof(int));
            ReleaseSemaphore(sem_0[52],1,NULL); //full
            WaitForSingleObject(sem_0[55],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codage_50,6336*sizeof(unsigned char));
            sendData(TCP,x86_2,x86_1,Codeur_Y_0_Codage_49,1*sizeof(int));
            ReleaseSemaphore(sem_0[54],1,NULL); //empty
            WaitForSingleObject(sem_0[66],INFINITE); //empty
            receiveData(TCP,x86_1,x86_2,r_Codeur_Y_0_Codag_6,1*sizeof(int));
            ReleaseSemaphore(sem_0[67],1,NULL); //full
            WaitForSingleObject(sem_0[68],INFINITE); //empty
            receiveData(TCP,x86_1,x86_2,r_Decodeur_Y_0_Dec_4,4*sizeof(int));
            ReleaseSemaphore(sem_0[69],1,NULL); //full
            WaitForSingleObject(sem_0[61],INFINITE); //empty
            receiveData(TCP,x86_1,x86_2,r_Decodeur_Y_0_Dec_3,792*sizeof(unsigned char));
            receiveData(TCP,x86_1,x86_2,r_Decodeur_Y_0_Dec_2,1*sizeof(int));
            ReleaseSemaphore(sem_0[60],1,NULL); //full
            WaitForSingleObject(sem_0[63],INFINITE); //full
            sendData(TCP,x86_2,x86_1,Decodeur_Y_0_Deco_47,6336*sizeof(unsigned char));
            sendData(TCP,x86_2,x86_1,Decodeur_Y_0_Deco_48,1*sizeof(int));
            ReleaseSemaphore(sem_0[62],1,NULL); //empty
        }

        return 0;
    }//communicationThread

