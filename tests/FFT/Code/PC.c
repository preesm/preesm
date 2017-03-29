/*******************************************************************************
 * Copyright or © or Copr. 2009 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2009)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2010)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
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

