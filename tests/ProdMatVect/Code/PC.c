    // Buffer declarations
    int generate_0[4];
    int generate_1[4];
    int prodMatM_0[4];

    void main(void){
        // Buffer declarations
        long i ;
        long j ;
        long k ;

        for(;;){
            generateMatrix(generate_0, 4/*size*/);
            generateMatrix(generate_1, 4/*size*/);
            {//prodMatMat
                for(i = 0; i<2 ; i ++)
                {//prodMatVect
                    int *inSub_i__0 = &prodMatM_0 [(i * 2) % 4];
                    int *outSub_i_0 = &generate_1 [(i * 2) % 4];
                    int outLoopP_0[2];
                    init_inLoopPort_0(outLoopP_0, 2/*init_size*/);
                    for(j = 0; j<2 ; j ++)
                    {//cluster_0
                        int *outSub_j_0 = &generate_0 [(j * 2) % 4];
                        int *outSub_j_1 = &outSub_i_0 [(j * 1) % 2];
                        int vectorOu_0[2];
                        {//prodScalVect
                            for(k = 0; k<2 ; k ++)
                            {//productScal
                                int *inSub_k__0 = &vectorOu_0 [(k * 1) % 2];
                                int *outSub_k_0 = &outSub_j_0 [(k * 1) % 2];
                                int *outSub_k_1 = &outLoopP_0 [(k * 1) % 2];
                                int res_op1[1];
                                mult(outSub_k_0, outSub_j_1, res_op1);
                                add(res_op1, outSub_k_1, inSub_k__0);
                            }
                        }
                        memcpy(outLoopP_0, vectorOu_0, 2*sizeof(int)/*size*/);
                        memcpy(inSub_i__0, vectorOu_0, 2*sizeof(int)/*size*/);
                    }
                }
            }
            display(prodMatM_0, 4/*size*/);
        }

    }//computationThread

