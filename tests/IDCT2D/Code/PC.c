   // Buffer declarations
    int TriggerMBIDCT_0[64];
    int Triggersigned_0[1];
    int IDCT2D_0_IDCT_0[2];
    int IDCT2D_0_IDCT_1[1];
    int IDCT2D_0_IDCT_2[64];
    int IDCT2D_0_IDCT_5[64];
    int IDCT2D_0_IDCT_6[64];
    int IDCT2D_0_IDCT_7[64];
    int IDCT2D_0_IDCT_9[64];
    int IDCT2D_0_IDC_10[64];
    int IDCT2D_0_IDC_11[64];
    int IDCT2D_0_IDC_13[64];
    int IDCT2D_0_clip_0[64];


    void main(void){
        // Buffer declarations
        long i ;

        for(;;){
            int *IDCT2D_0_IDC_14 =&TriggerMBIDCT_0[(0*64)];
            int *IDCT2D_0_IDCT_4 =&TriggerMBIDCT_0[(0*128)];
            int *IDCT2D_0_IDCT_3 =&IDCT2D_0_IDC_13[(0*64)];
            int *IDCT2D_0_IDC__0 =&IDCT2D_0_IDC_13[(0*128)];
            //init_in_2(IDCT2D_0_IDCT_2,64/*init_size*/);
            trigger(IDCT2D_0_IDCT_1);
            trigger_bench(TriggerMBIDCT_0,Triggersigned_0);
            readBlock(IDCT2D_0_IDC_14,IDCT2D_0_IDCT_2,IDCT2D_0_IDCT_1,IDCT2D_0_IDCT_5);
            for(i = 0; i<8 ; i ++)
            {//IDCT2D_0_IDCT2D_basic_0_IDCT1D
                int *inSub_i_IDCT2_0 =&IDCT2D_0_IDCT_6[(i*8)%64];
                int *outSub_i_IDCT_0 =&IDCT2D_0_IDCT_5[(i*8)%64];
                idct1d(outSub_i_IDCT_0,inSub_i_IDCT2_0);
            }
            transpose(IDCT2D_0_IDCT_6,IDCT2D_0_IDCT_3,IDCT2D_0_IDCT_7);
            readBlock(IDCT2D_0_IDCT_4,IDCT2D_0_IDCT_7,IDCT2D_0_IDCT_0,IDCT2D_0_IDCT_9);
            for(i = 0; i<8 ; i ++)
            {//IDCT2D_0_IDCT2D_basic_0_IDCT1D_1
                int *inSub_i_IDCT2_0 =&IDCT2D_0_IDC_10[(i*8)%64];
                int *outSub_i_IDCT_0 =&IDCT2D_0_IDCT_9[(i*8)%64];
                idct1d(outSub_i_IDCT_0,inSub_i_IDCT2_0);
            }
            transpose(IDCT2D_0_IDC_10,IDCT2D_0_IDC__0,IDCT2D_0_IDC_11);
            clip(IDCT2D_0_IDC_13,Triggersigned_0,IDCT2D_0_clip_0);
            group_bench(IDCT2D_0_clip_0);
        }

    }//computationThread

