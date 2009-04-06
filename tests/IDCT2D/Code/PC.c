    #include "../src/x86.h"

    // Buffer declarations
    int Triggers_0[1];
    int TriggerM_0[64];
    int IDCT2Dou_0[64];

    void computationThread_PC(void);

    void computationThread_PC(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            trigger_bench(TriggerM_0, Triggers_0);
            {//IDCT2D
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
                        readBlock(TriggerM_0, outLoopP_0, outSub_i_0, out_1_li_0);
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
                clip(block_ou_0, Triggers_0, IDCT2Dou_0);
            }
            group_bench(IDCT2Dou_0);
        }

    }//computationThread

