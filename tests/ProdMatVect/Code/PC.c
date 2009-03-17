

    // Buffer declarations
    int outputMatrixo_0[100];
    int outputVectoro_0[10];
    int ProdMatVectve_0[10];


    void main(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            generateMatrix(outputMatrixo_0,10/*size*/);
            generateVect(outputVectoro_0,10/*size*/);
            {//ProdMatVect
                int accOut_accIn[10];
                init_accIn(accOut_accIn,10/*init_size*/);
                for(i = 0; i<10 ; i ++)
                {//prodScalVect
                    int *outSub_i_outp_0 =&outputMatrixo_0[(i*10)%100];
                    int *outSub_i_outp_1 =&outputVectoro_0[(i*1)%10];
                    int scalOut_inData[10];
                    for(j = 0; j<10 ; j ++)
                    {//productScal
                        int *inSub_j_scalO_0 =&scalOut_inData[(j*1)%10];
                        int *outSub_j_outS_0 =&outSub_i_outp_0[(j*1)%10];
                        int *outSub_j_accO_0 =&accOut_accIn[(j*1)%10];
                        int res_op1[1];
                        mult(outSub_j_outS_0,outSub_i_outp_1,res_op1);
                        add(res_op1,outSub_j_accO_0,inSub_j_scalO_0);
                    }
                    for(j = 0; j<10 ; j ++)
                    {//copyData
                        int *inSub_j_accOu_0 =&accOut_accIn[(j*1)%10];
                        int *inSub_j_ProdM_0 =&ProdMatVectve_0[(j*1)%10];
                        int *outSub_j_scal_0 =&scalOut_inData[(j*1)%10];
                        copyData(outSub_j_scal_0,inSub_j_accOu_0,inSub_j_ProdM_0);
                    }
                }
            }
            display(ProdMatVectve_0,10/*size*/);
        }

    }//computationThread

