    #include "../src/x86.h"

    // Buffer declarations
    int generate_0[4];
    int generate_1[4];
    int prodMatM_0[4];

    void computationThread_PC(void);

    void computationThread_PC(void){
        // Buffer declarations
        long i ;
        long j ;
        long k ;

        for(;;){
            generateMatrix(generate_0, 4/*size*/);
            generateMatrix(generate_1, 4/*size*/);
            {//prodMatMat
                int *out_matr_1 = &generate_0 [(0 * 8)];
                for(i = 0; i<2 ; i ++)
                {//prodMatVect
                    int *inSub_i__0 = &prodMatM_0 [(i * 2) % 4];
                    int *outSub_i_0 = &generate_1 [(i * 2) % 4];
                    int *outSub_i_1 = &out_matr_1 [(i * 4) % 8];
                    int accOut_a_0[2];
                    init_accIn(accOut_a_0, 2/*init_size*/);
                    for(j = 0; j<2 ; j ++)
                    {//prodScalVect
                        int *outSub_j_0 = &outSub_i_1 [(j * 2) % 4];
                        int *outSub_j_1 = &outSub_i_0 [(j * 1) % 2];
                        int scalOut__0[2];
                        for(k = 0; k<2 ; k ++)
                        {//productScal
                            int *inSub_k__0 = &scalOut__0 [(k * 1) % 2];
                            int *outSub_k_0 = &outSub_j_0 [(k * 1) % 2];
                            int *outSub_k_1 = &accOut_a_0 [(k * 1) % 2];
                            int res_op1[1];
                            mult(outSub_k_0, outSub_j_1, res_op1);
                            add(res_op1, outSub_k_1, inSub_k__0);
                        }
                        for(k = 0; k<2 ; k ++)
                        {//copyData
                            int *inSub_k__0 = &inSub_i__0 [(k * 1) % 2];
                            int *inSub_k__1 = &accOut_a_0 [(k * 1) % 2];
                            int *outSub_k_0 = &scalOut__0 [(k * 1) % 2];
                            copyData(outSub_k_0, inSub_k__1, inSub_k__0);
                        }
                    }
                }
            }
            display(prodMatM_0, 4/*size*/);
        }

    }//computationThread

