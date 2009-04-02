
    // Buffer declarations
    int Triggers_0[4];
    int TriggerM_0[256];
    int IDCT2Dou_0[256];

    void main(void){
        // Buffer declarations
        long i ;
        long j ;
        long k ;

        for(;;){
            trigger_bench(TriggerM_0,Triggers_0);
            for(i = 0; i<4 ; i ++)
            {//IDCT2D
                int *inSub_i__0 =&IDCT2Dou_0[(i*64)%256];
                int *outSub_i_0 =&TriggerM_0[(i*64)%256];
                int *outSub_i_1 =&Triggers_0[(i*1)%4];
                int block_ou_0[64];
                {//IDCT2D_basic
                    int trig_clu_0[2];
                    int outLoopP_0[64];
                    init_inLoopPort_0(outLoopP_0,64/*init_size*/);
                    trigger(trig_clu_0);
                    for(j = 0; j<2 ; j ++)
                    {//cluster_0
                        int *outSub_j_0 =&trig_clu_0[(j*1)%2];
                        int out_1_li_0[64];
                        int lineOut__0[64];
                        int blockOut_0[64];
                        readBlock(outSub_i_0,outLoopP_0,outSub_j_0,out_1_li_0);
                        for(k = 0; k<8 ; k ++)
                        {//IDCT1D
                            int *inSub_k__0 =&lineOut__0[(k*8)%64];
                            int *outSub_k_0 =&out_1_li_0[(k*8)%64];
                            idct1d(outSub_k_0,inSub_k__0);
                        }
                        transpose(lineOut__0,blockOut_0);
                        memcpy(block_ou_0,blockOut_0,64*sizeof(int)/*size*/);
                        memcpy(outLoopP_0,blockOut_0,64*sizeof(int)/*size*/);
                    }
                }
                clip(block_ou_0,outSub_i_1,inSub_i__0);
            }
            group_bench(IDCT2Dou_0);
        }

    }//computationThread

