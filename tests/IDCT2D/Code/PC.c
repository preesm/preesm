

    // Buffer declarations
    int Triggersigned_0[1];
    int TriggerMBIDCT_0[64];
    int IDCT2DoutGrou_0[64];


    void main(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            trigger_bench(TriggerMBIDCT_0,Triggersigned_0);
            {//IDCT2D
                int block_out_blo_0[64];
                {//IDCT2D_basic
                    int trig_cluster__0[2];
                    int outLoopPort_0_0[64];
                    trigger(trig_cluster__0);
                    for(i = 0; i<2 ; i ++)
                    {//cluster_0
                        int *outSub_i_trig_0 =&trig_cluster__0[(i*1)%2];
                        int out_1_lineIn[64];
                        int lineOut_block_0[64];
                        readBlock(TriggerMBIDCT_0,outLoopPort_0_0,outSub_i_trig_0,out_1_lineIn);
                        for(j = 0; j<8 ; j ++)
                        {//IDCT1D
                            int *inSub_j_lineO_0 =&lineOut_block_0[(j*8)%64];
                            int *outSub_j_out__0 =&out_1_lineIn[(j*8)%64];
                            idct1d(outSub_j_out__0,inSub_j_lineO_0);
                        }
                        transpose(lineOut_block_0,block_out_blo_0,outLoopPort_0_0);
                    }
                }
                clip(block_out_blo_0,Triggersigned_0,IDCT2DoutGrou_0);
            }
            group_bench(IDCT2DoutGrou_0);
        }

    }//computationThread

