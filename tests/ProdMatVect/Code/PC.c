    #include "../src/x86.h"

    // Buffer declarations
    int outputMa_0[9];
    int outputVe_0[3];
    int ProdMatV_0[3];

    void computationThread_PC(void);

    void computationThread_PC(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            generateMatrix(outputMa_0, 3/*size*/);
            generateVect(outputVe_0, 3/*size*/);
            {//ProdMatVect
                char outLoopP_0[3];
                char cluster__0[9];
                init_inLoopPort_0(outLoopP_0, 3/*init_size*/);
                for(i = 0; i<3 ; i ++)
                {//cluster_0
                    char *inSub_i__0 = &cluster__0 [(i * 3) % 9];
                    int *outSub_i_0 = &outputMa_0 [(i * 3) % 9];
                    int *outSub_i_1 = &outputVe_0 [(i * 1) % 3];
                    char vectorOu_0[3];
                    {//prodScalVect
                        for(j = 0; j<3 ; j ++)
                        {//productScal
                            char *inSub_j__0 = &vectorOu_0 [(j * 1) % 3];
                            int *outSub_j_0 = &outSub_i_0 [(j * 1) % 3];
                            char *outSub_j_1 = &outLoopP_0 [(j * 1) % 3];
                            int res_op1[1];
                            mult(outSub_j_0, outSub_i_1, res_op1);
                            add(res_op1, outSub_j_1, inSub_j__0);
                        }
                    }
                }
            }
            display(ProdMatV_0, 3/*size*/);
        }

    }//computationThread

