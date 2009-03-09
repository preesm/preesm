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
            explode(Firstout_0,explode__0,explode__1,explode__3,explode__2);
            {
                char outLoopP_0[1];
                for(i = 0; i<4 ; i ++)
                {
                    char *sub_i_Se_0 =&Second_0_0[i*1];
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
                    char *sub_i_Se_0 =&Second_3_0[i*1];
                    char *sub_i_ex_0 =&explode__2[i*1];
                    char dataOut__0[1];
                    mux(sub_i_ex_0,outLoopP_0,dataOut__0);
                    process_tokens(dataOut__0,sub_i_Se_0,outLoopP_0);
                }
            }
            implode(implode__0,Second_0_0,Second_1_0,Second_2_0,Second_3_0);
            third_dummy(implode__0);
        }

    }//computationThread

