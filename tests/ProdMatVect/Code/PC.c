
    // Buffer declarations
    char virtual_heap[22];
    char *outVect_vectorIn = &virtual_heap [9];
    char *outMat_matrixIn = &virtual_heap [0];
    char *vectorOut_inResult = &virtual_heap [12];

    void main(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            generateMatrix(outMat_matrixIn, 9/*size*/);
            generateVect(outVect_vectorIn, 3/*size*/);
            {//ProdMatVect
                char *outLoopPort_0_inLo_0 = &virtual_heap [15];
                init_inLoopPort_0(outLoopPort_0_inLo_0, 3/*init_size*/);
                for(i = 0; i<3 ; i ++)
                {//cluster_0
                    char *outSub_i_outMat_ma_0 = &outMat_matrixIn [((i*3)%9)];
                    char *outSub_i_outVect_v_0 = &outVect_vectorIn [((i*1)%3)];
                    char *vectorOut_in = &virtual_heap [18];
                    {//prodScalVect
                        for(j = 0; j<3 ; j ++)
                        {//productScal
                            char *inSub_j_vectorOut__0 = &vectorOut_in [((j*1)%3)];
                            char *outSub_j_outSub_i__0 = &outSub_i_outMat_ma_0 [((j*1)%3)];
                            char *outSub_j_outLoopPo_0 = &outLoopPort_0_inLo_0 [((j*1)%3)];
                            char *res_op1 = &virtual_heap [21];
                            mult(outSub_j_outSub_i__0, outSub_i_outVect_v_0, res_op1);
                            add(res_op1, outSub_j_outLoopPo_0, inSub_j_vectorOut__0);
                        }
                    }
                    {//broadcastVertex
                        outLoopPort_0_inLo_0 = &vectorOut_in[0];
                        vectorOut_inResult = &vectorOut_in[0];
                    }
                }
            }
            display(vectorOut_inResult, 3/*size*/);
        }

        return 0;
    }//computationThread

