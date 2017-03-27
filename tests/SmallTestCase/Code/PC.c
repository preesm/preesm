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
    #include "../src/x86.h"

    // Buffer declarations
    char Firstout_0[16];
    char explode__0[4];
    char explode__1[4];
    char explode__2[4];
    char explode__3[4];
    char Second_0_0[4];
    char Second_1_0[4];
    char Second_2_0[4];
    char Second_3_0[4];
    char implode__0[16];

    void computationThread_PC(void);

    void computationThread_PC(void){
        // Buffer declarations
        long i ;

        for(;;){
            first_dummy(Firstout_0,3/*firstParam*/);
            {
                char outLoopP_0[1];
                for(i = 0; i<4 ; i ++)
                {
                    char *sub_i_Se_0 =&Second_0_0[i*1];
                    char *sub_i_ex_0 =&explode__3[i*1];
                    char dataOut__0[1];
                    mux(sub_i_ex_0,outLoopP_0,dataOut__0);
                    process_tokens(dataOut__0,sub_i_Se_0,outLoopP_0);
                }
            }
            {
                char outLoopP_0[1];
                for(i = 0; i<4 ; i ++)
                {
                    char *sub_i_Se_0 =&Second_1_0[i*1];
                    char *sub_i_ex_0 =&explode__1[i*1];
                    char dataOut__0[1];
                    mux(sub_i_ex_0,outLoopP_0,dataOut__0);
                    process_tokens(dataOut__0,sub_i_Se_0,outLoopP_0);
                }
            }
            {
                char outLoopP_0[1];
                for(i = 0; i<4 ; i ++)
                {
                    char *sub_i_Se_0 =&Second_2_0[i*1];
                    char *sub_i_ex_0 =&explode__0[i*1];
                    char dataOut__0[1];
                    mux(sub_i_ex_0,outLoopP_0,dataOut__0);
                    process_tokens(dataOut__0,sub_i_Se_0,outLoopP_0);
                }
            }
            {
                char outLoopP_0[1];
                for(i = 0; i<4 ; i ++)
                {
                    char *sub_i_Se_0 =&Second_3_0[i*1];
                    char *sub_i_ex_0 =&explode__2[i*1];
                    char dataOut__0[1];
                    mux(sub_i_ex_0,outLoopP_0,dataOut__0);
                    process_tokens(dataOut__0,sub_i_Se_0,outLoopP_0);
                }
            }
            third_dummy(implode__0);
        }

    }//computationThread

