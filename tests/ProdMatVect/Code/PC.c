

    // Buffer declarations
    int outputMa_0[6400];
    int outputVe_0[80];
    int ProdMatV_0[80];

    void main(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            generateMatrix(outputMa_0, 80/*size*/);
            generateVect(outputVe_0, 80/*size*/);
            {//ProdMatVect
                int accOut_a_0[80];
                init_accIn(accOut_a_0, 80/*init_size*/);
                for(i = 0; i<80 ; i ++)
                {//prodScalVect
                    int *outSub_i_0 = &outputMa_0 [(i * 80) % 6400];
                    int *outSub_i_1 = &outputVe_0 [(i * 1) % 80];
                    int scalOut__0[80];
                    for(j = 0; j<80 ; j ++)
                    {//productScal
                        int *inSub_j__0 = &scalOut__0 [(j * 1) % 80];
                        int *outSub_j_0 = &outSub_i_0 [(j * 1) % 80];
                        int *outSub_j_1 = &accOut_a_0 [(j * 1) % 80];
                        int res_op1[1];
                        mult(outSub_j_0, outSub_i_1, res_op1);
                        add(res_op1, outSub_j_1, inSub_j__0);
                    }
                    for(j = 0; j<80 ; j ++)
                    {//copyData
                        int *inSub_j__0 = &accOut_a_0 [(j * 1) % 80];
                        int *inSub_j__1 = &ProdMatV_0 [(j * 1) % 80];
                        int *outSub_j_0 = &scalOut__0 [(j * 1) % 80];
                        copyData(outSub_j_0, inSub_j__0, inSub_j__1);
                    }
                }
            }
            display(ProdMatV_0, 80/*size*/);
        }

    }//computationThread

