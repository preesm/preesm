

    // Buffer declarations
    int generate_0[400];
    int generate_1[400];
    int prodMatM_0[400];



    void main(void){
        // Buffer declarations
        long i ;
        long j ;
        long k ;

        for(;;){
            generateMatrix(generate_0, 20/*size*/);
            generateMatrix(generate_1, 20/*size*/);
            {//prodMatMat
                for(i = 0; i<20 ; i ++)
                {//prodMatVect
                    int *inSub_i__0 = &prodMatM_0 [(i * 20) % 400];
                    int *outSub_i_0 = &generate_1 [(i * 20) % 400];
                    int accOut_a_0[20];
                    init_accIn(accOut_a_0, 20/*init_size*/);
                    for(j = 0; j<20 ; j ++)
                    {//prodScalVect
                        int *outSub_j_0 = &generate_0 [(j * 20) % 400];
                        int *outSub_j_1 = &outSub_i_0 [(j * 1) % 20];
                        int scalOut__0[20];
                        for(k = 0; k<20 ; k ++)
                        {//productScal
                            int *inSub_k__0 = &scalOut__0 [(k * 1) % 20];
                            int *outSub_k_0 = &outSub_j_0 [(k * 1) % 20];
                            int *outSub_k_1 = &accOut_a_0 [(k * 1) % 20];
                            int res_op1[1];
                            mult(outSub_k_0, outSub_j_1, res_op1);
                            add(res_op1, outSub_k_1, inSub_k__0);
                        }
                        for(k = 0; k<20 ; k ++)
                        {//copyData
                            int *inSub_k__0 = &accOut_a_0 [(k * 1) % 20];
                            int *inSub_k__1 = &inSub_i__0 [(k * 1) % 20];
                            int *outSub_k_0 = &scalOut__0 [(k * 1) % 20];
                            copyData(outSub_k_0, inSub_k__0, inSub_k__1);
                        }
                    }
                }
            }
            display(prodMatM_0, 400/*size*/);
        }

    }//computationThread

