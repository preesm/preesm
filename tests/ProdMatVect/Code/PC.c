/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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
    int *out_ScalIn;
    int outVect_in[3];
    char vectorOut_accIn[3];
    char *vectorOut_accIn_2;
    int outMat_in[9];
    char vectorFinal_in_0[9];
    int *out_ScalIn_0;
    int *out_ScalIn_1;
    char out_inResult[3];
    char vectorOut_accIn_1[3];
    char vectorFinal_in_1[3];
    int *out_vector1In;
    int *out_vector1In_0;
    char vectorOut_accIn_0[3];
    char vectorFinal_in[3];
    int *out_vector1In_1;
    char cluster_0_in_in[3];
    char outLoopPort_0_inLo_0[1];
    char cluster_0_in_in[3];
    char outLoopPort_0_inLo_0[1];
    char outLoopPort_0_inLo_0[1];
    char cluster_0_in_in[3];
    char out_scal2[1];
    char out_scal2[1];
    char out_scal2[1];
    char res_op1[1];
    char res_op1[1];
    char res_op1[1];

    DWORD WINAPI computationThread_PC( LPVOID lpParam );

    DWORD WINAPI computationThread_PC( LPVOID lpParam ){
        // Buffer declarations
        long i ;

        {
            init_ProdMatVect_0_prodScalVect_init_accIn(vectorOut_accIn);
        }

        for(;;){
            vectorOut_accIn_2 = &vectorOut_accIn[0];
            generateMatrix(outMat_in, 9/*size*/);
            generateVect(outVect_in, 3/*size*/);
            {//ProdMatVect_0_explode_vectorIn
                out_ScalIn = &outVect_in[0];
                out_ScalIn_1 = &outVect_in[3];
                out_ScalIn_0 = &outVect_in[2];
            }
            {//ProdMatVect_0_explode_matrixIn
                out_vector1In = &outMat_in[0];
                out_vector1In_0 = &outMat_in[9];
                out_vector1In_1 = &outMat_in[6];
            }
            {//ProdMatVect_0_prodScalVect
                init_inLoopPort_0(outLoopPort_0_inLo_0, 1/*init_size*/);
                for(i = 0; i<3 ; i ++)
                {//cluster_0
                    char *inSub_i_cluster_0__0 = &cluster_0_in_in [((i*(1))%3)];
                    int *outSub_i_out_vecto_0 = &out_vector1In [((i*(1))%0)];
                    char *outSub_i_vectorOut_0 = &vectorOut_accIn [((i*(1))%3)];
                    int *outSub_i_out_ScalIn = &out_ScalIn [((i*(1))%0)];
                    mux();
                    {//productScal
                        {//brScal
                            memcpy(outLoopPort_0_inLo_0, out_scal2, 1*sizeof(char)/*size*/);
                        }
                        mult(outSub_i_out_vecto_0, res_op1);
                        add(res_op1, outSub_i_vectorOut_0, inSub_i_cluster_0__0);
                    }
                }
                for(i = 0; i<3 ; i ++)
                {//brScal
                    char *inSub_i_vectorOut__0 = &vectorOut_accIn_0 [((i*(1))%3)];
                    char *inSub_i_vectorFina_0 = &vectorFinal_in [((i*(1))%3)];
                    char *outSub_i_cluster_0_0 = &cluster_0_in_in [((i*(1))%3)];
                    brScal();
                }
            }
            {//ProdMatVect_0_prodScalVect_1
                init_inLoopPort_0(outLoopPort_0_inLo_0, 1/*init_size*/);
                for(i = 0; i<3 ; i ++)
                {//cluster_0
                    char *inSub_i_cluster_0__0 = &cluster_0_in_in [((i*(1))%3)];
                    int *outSub_i_out_vecto_0 = &out_vector1In_0 [((i*(1))%0)];
                    char *outSub_i_vectorOut_0 = &vectorOut_accIn_0 [((i*(1))%3)];
                    int *outSub_i_out_ScalI_0 = &out_ScalIn_1 [((i*(1))%0)];
                    mux();
                    {//productScal
                        {//brScal
                            memcpy(outLoopPort_0_inLo_0, out_scal2, 1*sizeof(char)/*size*/);
                        }
                        mult(outSub_i_out_vecto_0, res_op1);
                        add(res_op1, outSub_i_vectorOut_0, inSub_i_cluster_0__0);
                    }
                }
                for(i = 0; i<3 ; i ++)
                {//brScal
                    char *inSub_i_vectorOut__0 = &vectorOut_accIn_1 [((i*(1))%3)];
                    char *inSub_i_vectorFina_0 = &vectorFinal_in_0 [((i*(1))%9)];
                    char *outSub_i_cluster_0_0 = &cluster_0_in_in [((i*(1))%3)];
                    brScal();
                }
            }
            {//ProdMatVect_0_prodScalVect_2
                init_inLoopPort_0(outLoopPort_0_inLo_0, 1/*init_size*/);
                for(i = 0; i<3 ; i ++)
                {//cluster_0
                    char *inSub_i_cluster_0__0 = &cluster_0_in_in [((i*(1))%3)];
                    int *outSub_i_out_vecto_0 = &out_vector1In_1 [((i*(1))%0)];
                    char *outSub_i_vectorOut_0 = &vectorOut_accIn_1 [((i*(1))%3)];
                    int *outSub_i_out_ScalI_0 = &out_ScalIn_0 [((i*(1))%0)];
                    mux();
                    {//productScal
                        {//brScal
                            memcpy(outLoopPort_0_inLo_0, out_scal2, 1*sizeof(char)/*size*/);
                        }
                        mult(outSub_i_out_vecto_0, res_op1);
                        add(res_op1, outSub_i_vectorOut_0, inSub_i_cluster_0__0);
                    }
                }
                for(i = 0; i<3 ; i ++)
                {//brScal
                    char *inSub_i_vectorOut__0 = &vectorOut_accIn_2 [((i*(1))%0)];
                    char *inSub_i_vectorFina_0 = &vectorFinal_in_1 [((i*(1))%3)];
                    char *outSub_i_cluster_0_0 = &cluster_0_in_in [((i*(1))%3)];
                    brScal();
                }
            }
            {//ProdMatVect_0_roundBuffer_vectorOut
                memcpy(out_inResult, vectorFinal_in_1, 3*sizeof(char)/*size*/);
            }
            display(out_inResult, 3/*size*/);
        }

        return 0;
    }//computationThread

