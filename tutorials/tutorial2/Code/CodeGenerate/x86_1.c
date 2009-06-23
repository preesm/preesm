    #include "x86.h"

    // Buffer declarations
    semaphore sem[34];
    LAR out_Global_4[1];
    int indice_level1_Tail_0[1];
    LAR out_Global[1];
    unsigned char Tab_bin_buffer_bin2[3168];
    int Taille_buffer_o_po_1[1];
    LAR Global_[1];
    unsigned char im_o_[6336];
    LAR out_Global_10[1];
    int taille_signe_4_Tai_0[1];
    LAR *out_Global_13;
    LAR out_Global_14[1];
    unsigned char *_;
    unsigned char Tab_bin_buffer_bin3[3168];
    LAR out_Global_0[1];
    unsigned char buff_signe_4_Tab_U_0[792];
    unsigned char buffer_level1_Tab__1[25344];
    int Taille_stream_Erro_0[1];
    LAR out_Global_5[1];
    LAR out_Global_6[1];
    LAR *out_Global_7;
    int taille_max_o_[1];
    int Taille_buffer_o_Ta_0[1];
    int indice_level2_Tail_1[1];
    LAR out_Global_9[1];
    unsigned char im_o_im_min_kxk[396];
    LAR *out_Global_11;
    LAR *out_in_1;
    int Taille_buffer_o_po_2[1];
    int taille_signe_4_Tai_1[1];
    int taille_error_16_Ta_0[1];
    int indice_level1_Tail_1[1];
    LAR out_Global_18[1];
    unsigned char buff_error_16_TabR_0[398];
    unsigned char _im_tailles_i_0[25344];
    int indice_level3_Tail_1[1];
    LAR *out_Global_3;
    unsigned char _im_tailles_i_1[25344];
    unsigned char Tab_bin_buff_strea_0[6336];
    unsigned char buffer_level3_Tab__1[25344];
    unsigned char _im_tailles_i[25344];
    LAR out_Global_8[1];
    unsigned char Tab_Uchar_buff_str_0[6336];
    unsigned char buffer_level2_Tab__1[25344];
    unsigned char *_im_i_1;
    LAR *_in_0;
    LAR out_Global_19[1];
    unsigned char im_o__1[1584];
    int Coef_Quant_[4];
    int indice_level2_Tail_0[1];
    LAR out_Global_17[1];
    unsigned char buffer_level1_Tab__0[25344];
    unsigned char TabUchar_buff_stre_0[398];
    LAR out_Global_16[1];
    LAR *out_Global_12;
    unsigned char _im_i_0[6336];
    unsigned char stream_quad_stream_0[1040];
    unsigned char im_o__0[1584];
    int Taille_buffer_o_po_0[1];
    LAR out_in_0[1];
    int indice_level3_Tail_0[1];
    unsigned char Tab_bin_buffer_bin1[3168];
    int Taille_buffer_o_Ta_2[1];
    unsigned char buffer_level2_Tab__0[25344];
    int Taille_flux_quad_[1];
    unsigned char *_im_i;
    unsigned char im_o_im_max_kxk[396];
    LAR *_in;
    LAR *out_in;
    unsigned char im_Quant_o_Y[25344];
    int Taille_buffer_o_Ta_4[1];
    unsigned char im_Quant_bord_o_im_0[26280];
    LAR out_Global_20[1];
    int Taille_buffer_o_Ta_1[1];
    unsigned char im_tailles_reconst_0[25344];
    unsigned char _im_i_2[1584];
    int Taille_buffer_o_Ta_3[1];
    unsigned char buffer_level3_Tab__0[25344];
    LAR out_Global_2[1];
    unsigned char buff_signe_4_Tab_b_0[6336];
    unsigned char _im_min_kxk[1584];
    unsigned char Tab_Uchar_buffer_b_0[3168];
    LAR *out_in_2;
    unsigned char Tab_Uchar_buffer_b_1[3168];
    int TailleUchar_Taille_0[1];
    unsigned char _im_max_kxk[6336];
    LAR *out_Global_1;
    unsigned char Tab_Uchar_buffer_b_2[3168];
    LAR out_Global_15[1];

    DWORD WINAPI computationThread_x86_1( LPVOID lpParam );
    DWORD WINAPI communicationThread_x86_1( LPVOID lpParam );

    DWORD WINAPI computationThread_x86_1( LPVOID lpParam ){
        // Buffer declarations

        {
            semaphoreInit(sem, 34/*semNumber*/, x86_1);
            CreateThread(NULL,8000,communicationThread_x86_1,NULL,0,NULL);
            Read_LAR_Param_no_args(Global_);
            bidon();
            Display_Y_init_nocomment(1/*Number*/, 176/*xsize*/, 144/*ysize*/);
            bidon();
            bidon();
            bidon();
            ReleaseSemaphore(sem[2],1,NULL); //empty
            ReleaseSemaphore(sem[4],1,NULL); //empty
            ReleaseSemaphore(sem[6],1,NULL); //empty
            ReleaseSemaphore(sem[10],1,NULL); //empty
            ReleaseSemaphore(sem[14],1,NULL); //empty
            ReleaseSemaphore(sem[16],1,NULL); //empty
            ReleaseSemaphore(sem[22],1,NULL); //empty
            ReleaseSemaphore(sem[26],1,NULL); //empty
        }

        for(;;){
            {//bro_01
                _in_0 = &Global_[0];
                _in = &Global_[0];
            }
            {//Codeur_Y_0_broadcast_Global
                out_in_1 = &_in_0[0];
                out_in = &_in_0[0];
                out_Global_1 = &_in_0[0];
                memcpy(out_Global_0, _in_0, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global, _in_0, 1*sizeof(LAR)/*size*/);
                memcpy(out_in_0, _in_0, 1*sizeof(LAR)/*size*/);
            }
            {//Decodeur_Y_0_broadcast_Global
                out_in_2 = &_in[0];
                out_Global_3 = &_in[0];
                memcpy(out_Global_2, _in, 1*sizeof(LAR)/*size*/);
            }
            {//Codeur_Y_0_Codage_entrop_err_Y_0_broadcast_Global
                memcpy(out_Global_17, out_in, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_16, out_in, 1*sizeof(LAR)/*size*/);
            }
            {//Codeur_Y_0_Codeur_im_Y_0_broadcast_Global
                out_Global_12 = &out_in_1[0];
                out_Global_7 = &out_in_1[0];
                out_Global_11 = &out_in_1[0];
                out_Global_13 = &out_in_1[0];
                memcpy(out_Global_15, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_10, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_14, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_8, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_9, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_6, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_4, out_in_1, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_5, out_in_1, 1*sizeof(LAR)/*size*/);
            }
            {//Decodeur_Y_0_Decodage_DPCM_Y_0_broadcast_Global
                memcpy(out_Global_19, out_in_2, 1*sizeof(LAR)/*size*/);
                memcpy(out_Global_18, out_in_2, 1*sizeof(LAR)/*size*/);
            }
            WaitForSingleObject(sem[0],INFINITE); //full
            {//Codeur_Y_0_Codeur_im_Y_0_bro_2
                _im_i = &im_o_[0];
                memcpy(_im_max_kxk, im_o_, 6336*sizeof(unsigned char)/*size*/);
            }
            ReleaseSemaphore(sem[1],1,NULL); //empty
            WaitForSingleObject(sem[2],INFINITE); //empty
            Max2x2(out_Global_7, 3/*k*/, 1/*ratio*/, _im_i, im_o__0);
            ReleaseSemaphore(sem[3],1,NULL); //full
            Min2x2(out_Global_12, 3/*k*/, 1/*ratio*/, _im_i_0, im_o__1);
            {//Codeur_Y_0_Codeur_im_Y_0_bro_5
                _im_i_1 = &im_o__1[0];
                memcpy(_im_min_kxk, im_o__1, 1584*sizeof(unsigned char)/*size*/);
            }
            WaitForSingleObject(sem[4],INFINITE); //empty
            Max2x2(out_Global_11, 4/*k*/, 1/*ratio*/, _im_i_2, im_o_im_max_kxk);
            ReleaseSemaphore(sem[5],1,NULL); //full
            WaitForSingleObject(sem[6],INFINITE); //empty
            Min2x2(out_Global_13, 4/*k*/, 1/*ratio*/, _im_i_1, im_o_im_min_kxk);
            ReleaseSemaphore(sem[7],1,NULL); //full
            Code_taille_quadtree_16(out_Global_1, 1/*ratio*/, _im_tailles_i, buffer_level1_Tab__0, indice_level1_Tail_0, buffer_level2_Tab__0, indice_level2_Tail_0, buffer_level3_Tab__0, indice_level3_Tail_0);
            Bin_to_Uchar(buffer_level1_Tab__0, indice_level1_Tail_0, Tab_Uchar_buffer_b_0, Taille_buffer_o_Ta_0);
            Bin_to_Uchar(buffer_level2_Tab__0, indice_level2_Tail_0, Tab_Uchar_buffer_b_1, Taille_buffer_o_Ta_1);
            Bin_to_Uchar(buffer_level3_Tab__0, indice_level3_Tail_0, Tab_Uchar_buffer_b_2, Taille_buffer_o_Ta_2);
            WaitForSingleObject(sem[8],INFINITE); //full
            Create_im_quant(out_Global_20, 1/*ratio*/, im_Quant_bord_o_im_0, im_Quant_o_Y);
            ReleaseSemaphore(sem[9],1,NULL); //empty
            WaitForSingleObject(sem[10],INFINITE); //empty
            Concat_stream_quad_16(Tab_Uchar_buffer_b_0, Taille_buffer_o_Ta_0, Tab_Uchar_buffer_b_1, Taille_buffer_o_Ta_1, Tab_Uchar_buffer_b_2, Taille_buffer_o_Ta_2, stream_quad_stream_0, Taille_flux_quad_);
            ReleaseSemaphore(sem[11],1,NULL); //full
            WaitForSingleObject(sem[12],INFINITE); //full
            WaitForSingleObject(sem[14],INFINITE); //empty
            Bin_to_Uchar(buff_signe_4_Tab_b_0, taille_signe_4_Tai_0, Tab_Uchar_buff_str_0, Taille_buffer_o_Ta_3);
            ReleaseSemaphore(sem[15],1,NULL); //full
            ReleaseSemaphore(sem[13],1,NULL); //empty
            WaitForSingleObject(sem[16],INFINITE); //empty
            Explode_stream_quad_16(stream_quad_stream_0, buffer_level1_Tab__1, indice_level1_Tail_1, buffer_level2_Tab__1, indice_level2_Tail_1, buffer_level3_Tab__1, indice_level3_Tail_1);
            ReleaseSemaphore(sem[17],1,NULL); //full
            Display_Y(1/*Number*/, im_Quant_o_Y);
            Uchar_to_Bin(buffer_level1_Tab__1, indice_level1_Tail_1, Tab_bin_buffer_bin1, Taille_buffer_o_po_0);
            Uchar_to_Bin(buffer_level2_Tab__1, indice_level2_Tail_1, Tab_bin_buffer_bin2, Taille_buffer_o_po_1);
            WaitForSingleObject(sem[18],INFINITE); //full
            Decode_taille_quadtree_16(out_Global_3, 1/*ratio*/, Tab_bin_buffer_bin1, Tab_bin_buffer_bin2, Tab_bin_buffer_bin3, im_tailles_reconst_0);
            ReleaseSemaphore(sem[19],1,NULL); //empty
            {//Decodeur_Y_0_Bro
                _ = &im_tailles_reconst_0[0];
                memcpy(_im_tailles_i_1, im_tailles_reconst_0, 25344*sizeof(unsigned char)/*size*/);
                memcpy(_im_tailles_i_0, im_tailles_reconst_0, 25344*sizeof(unsigned char)/*size*/);
            }
            WaitForSingleObject(sem[20],INFINITE); //full
            WaitForSingleObject(sem[22],INFINITE); //empty
            Decode_Rice_Code(176/*xPR*/, 144/*yPR*/, 1/*SizeLevel*/, 1/*TailleOctet*/, buff_error_16_TabR_0, taille_error_16_Ta_0, TabUchar_buff_stre_0, TailleUchar_Taille_0);
            ReleaseSemaphore(sem[23],1,NULL); //full
            ReleaseSemaphore(sem[21],1,NULL); //empty
            WaitForSingleObject(sem[24],INFINITE); //full
            WaitForSingleObject(sem[26],INFINITE); //empty
            Uchar_to_Bin(buff_signe_4_Tab_U_0, taille_signe_4_Tai_1, Tab_bin_buff_strea_0, Taille_buffer_o_Ta_4);
            ReleaseSemaphore(sem[27],1,NULL); //full
            ReleaseSemaphore(sem[25],1,NULL); //empty
        }

        return 0;
    }//computationThread

    DWORD WINAPI communicationThread_x86_1( LPVOID lpParam ){
        // Buffer declarations

        {
            Com_Init(MEDIUM_SEND,TCP,x86_1,x86_2);
            Com_Init(MEDIUM_RCV,TCP,x86_2,x86_1);
            ReleaseSemaphore(sem[1],1,NULL); //empty
            ReleaseSemaphore(sem[28],1,NULL); //empty
            ReleaseSemaphore(sem[9],1,NULL); //empty
            ReleaseSemaphore(sem[13],1,NULL); //empty
            ReleaseSemaphore(sem[30],1,NULL); //empty
            ReleaseSemaphore(sem[19],1,NULL); //empty
            ReleaseSemaphore(sem[32],1,NULL); //empty
            ReleaseSemaphore(sem[25],1,NULL); //empty
            ReleaseSemaphore(sem[21],1,NULL); //empty
        }

        for(;;){
            WaitForSingleObject(sem[1],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,im_o_,6336*sizeof(unsigned char));
            ReleaseSemaphore(sem[0],1,NULL); //full
            WaitForSingleObject(sem[3],INFINITE); //full
            sendData(TCP,x86_1,x86_2,im_o__0,1584*sizeof(unsigned char));
            ReleaseSemaphore(sem[2],1,NULL); //empty
            WaitForSingleObject(sem[5],INFINITE); //full
            sendData(TCP,x86_1,x86_2,im_o_im_max_kxk,396*sizeof(unsigned char));
            ReleaseSemaphore(sem[4],1,NULL); //empty
            WaitForSingleObject(sem[7],INFINITE); //full
            sendData(TCP,x86_1,x86_2,im_o_im_min_kxk,396*sizeof(unsigned char));
            ReleaseSemaphore(sem[6],1,NULL); //empty
            WaitForSingleObject(sem[28],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,taille_max_o_,1*sizeof(int));
            ReleaseSemaphore(sem[29],1,NULL); //full
            WaitForSingleObject(sem[9],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,im_Quant_bord_o_im_0,26280*sizeof(unsigned char));
            ReleaseSemaphore(sem[8],1,NULL); //full
            WaitForSingleObject(sem[11],INFINITE); //full
            sendData(TCP,x86_1,x86_2,Taille_flux_quad_,1*sizeof(int));
            ReleaseSemaphore(sem[10],1,NULL); //empty
            WaitForSingleObject(sem[13],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,buff_signe_4_Tab_b_0,6336*sizeof(unsigned char));
            receiveData(TCP,x86_2,x86_1,taille_signe_4_Tai_0,1*sizeof(int));
            ReleaseSemaphore(sem[12],1,NULL); //full
            WaitForSingleObject(sem[15],INFINITE); //full
            sendData(TCP,x86_1,x86_2,Tab_Uchar_buff_str_0,6336*sizeof(unsigned char));
            sendData(TCP,x86_1,x86_2,Taille_buffer_o_Ta_3,1*sizeof(int));
            ReleaseSemaphore(sem[14],1,NULL); //empty
            WaitForSingleObject(sem[17],INFINITE); //full
            sendData(TCP,x86_1,x86_2,buffer_level3_Tab__1,25344*sizeof(unsigned char));
            sendData(TCP,x86_1,x86_2,indice_level3_Tail_1,1*sizeof(int));
            ReleaseSemaphore(sem[16],1,NULL); //empty
            WaitForSingleObject(sem[30],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,Taille_stream_Erro_0,1*sizeof(int));
            ReleaseSemaphore(sem[31],1,NULL); //full
            WaitForSingleObject(sem[19],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,Tab_bin_buffer_bin3,3168*sizeof(unsigned char));
            receiveData(TCP,x86_2,x86_1,Taille_buffer_o_po_2,1*sizeof(int));
            ReleaseSemaphore(sem[18],1,NULL); //full
            WaitForSingleObject(sem[32],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,Coef_Quant_,4*sizeof(int));
            ReleaseSemaphore(sem[33],1,NULL); //full
            WaitForSingleObject(sem[25],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,buff_signe_4_Tab_U_0,792*sizeof(unsigned char));
            receiveData(TCP,x86_2,x86_1,taille_signe_4_Tai_1,1*sizeof(int));
            ReleaseSemaphore(sem[24],1,NULL); //full
            WaitForSingleObject(sem[21],INFINITE); //empty
            receiveData(TCP,x86_2,x86_1,buff_error_16_TabR_0,398*sizeof(unsigned char));
            receiveData(TCP,x86_2,x86_1,taille_error_16_Ta_0,1*sizeof(int));
            ReleaseSemaphore(sem[20],1,NULL); //full
            WaitForSingleObject(sem[23],INFINITE); //full
            sendData(TCP,x86_1,x86_2,TabUchar_buff_stre_0,398*sizeof(unsigned char));
            sendData(TCP,x86_1,x86_2,TailleUchar_Taille_0,1*sizeof(int));
            ReleaseSemaphore(sem[22],1,NULL); //empty
            WaitForSingleObject(sem[27],INFINITE); //full
            sendData(TCP,x86_1,x86_2,Tab_bin_buff_strea_0,6336*sizeof(unsigned char));
            sendData(TCP,x86_1,x86_2,Taille_buffer_o_Ta_4,1*sizeof(int));
            ReleaseSemaphore(sem[26],1,NULL); //empty
        }

        return 0;
    }//communicationThread

