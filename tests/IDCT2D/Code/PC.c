    // Buffer declarations
    int TriggerM_0[256];
    int Triggers_0[4];
    int explode__0[64];
    int explode__1[64];
    int explode__2[64];
    int explode__3[64];
    int explode__4[1];
    int explode__5[1];
    int explode__6[1];
    int explode__7[1];
    int IDCT2D_0_0[64];
    int IDCT2D_1_0[64];
    int IDCT2D_2_0[64];
    int IDCT2D_3_0[64];
    int implode__0[256];

    void main(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            trigger_bench(TriggerM_0, Triggers_0);
            {//explode_Trigger_MB
                memcpy(explode__0, &TriggerM_0[0], 64*sizeof(int)/*size*/);
                memcpy(explode__1, &TriggerM_0[64], 64*sizeof(int)/*size*/);
                memcpy(explode__2, &TriggerM_0[128], 64*sizeof(int)/*size*/);
                memcpy(explode__3, &TriggerM_0[192], 64*sizeof(int)/*size*/);
            }
            {//explode_Trigger_signed
                memcpy(explode__5, &Triggers_0[0], 1*sizeof(int)/*size*/);
                memcpy(explode__4, &Triggers_0[1], 1*sizeof(int)/*size*/);
                memcpy(explode__7, &Triggers_0[2], 1*sizeof(int)/*size*/);
                memcpy(explode__6, &Triggers_0[3], 1*sizeof(int)/*size*/);
            }
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
                clip(block_ou_0, explode__5, IDCT2D_0_0);
            }
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
                        readBlock(explode__1, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode__4, IDCT2D_1_0);
            }
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
                        readBlock(explode__2, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, explode__7, IDCT2D_2_0);
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
                clip(block_ou_0, explode__6, IDCT2D_3_0);
            }
            {//implode_Group_MB
                memcpy(&implode__0[0], IDCT2D_0_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[64], IDCT2D_1_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[128], IDCT2D_2_0, 64*sizeof(int)/*size*/);
                memcpy(&implode__0[192], IDCT2D_3_0, 64*sizeof(int)/*size*/);
            }
            group_bench(implode__0);
        }

    }//computationThread

