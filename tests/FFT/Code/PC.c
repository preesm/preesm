    #include "../../Lib_com/include/x86.h"

    // Buffer declarations
    char dataOut_timeData[16];
    char *fftData_dataIn;
    char out_cluster_0_trig_0[8];
    char *outLoopPort_0_inLo_0;
    char data2Out_data2In_0[8];
    char data1Out_data1In[8];
    char data2Out_data2In[8];
    char dataOut_in[16];
    char data1Out_data1In_0[8];
    char weights_W[8];
    char res_in[1];

    DWORD WINAPI computationThread_PC( LPVOID lpParam );

    DWORD WINAPI computationThread_PC( LPVOID lpParam ){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            GenerateTimeSample();
            {//ComputeFFT
                init_inLoopPort_0(outLoopPort_0_inLo_0, 16/*init_size*/);
                trigger();
                for(i = 0; i<8 ; i ++)
                {//cluster_0
                    char *outSub_i_out_clust_0 = &out_cluster_0_trig_0 [((i*(1))%8)];
                    sortData(dataOut_timeData, outLoopPort_0_inLo_0, outSub_i_out_clust_0, data1Out_data1In, data2Out_data2In, weights_W, 16/*size*/);
                    for(j = 0; j<8 ; j ++)
                    {//butterflyStep
                        char *inSub_j_data1Out_d_0 = &data1Out_data1In_0 [((j*(1))%8)];
                        char *inSub_j_data2Out_d_0 = &data2Out_data2In_0 [((j*(1))%8)];
                        char *outSub_j_data1Out__0 = &data1Out_data1In [((j*(1))%8)];
                        char *outSub_j_data2Out__0 = &data2Out_data2In [((j*(1))%8)];
                        char *outSub_j_weights_W = &weights_W [((j*(1))%8)];
                        char *out_op2 = &res_in [((0*1)%1)];
                        char *out_op2_0 = &res_in [((0*1)%1)];
                        mult(outSub_j_data2Out__0, outSub_j_weights_W, res_in);
                        add(outSub_j_data1Out__0, out_op2, inSub_j_data1Out_d_0);
                        sub(outSub_j_data1Out__0, out_op2_0, inSub_j_data2Out_d_0);
                    }
                    collectData(data1Out_data1In_0, data2Out_data2In_0, dataOut_in, 16/*size*/);
                    {//brSamples
                        fftData_dataIn = &dataOut_in[0];
                        outLoopPort_0_inLo_0 = &dataOut_in[0];
                    }
                }
            }
            DisplayResult();
        }

        return 0;
    }//computationThread

