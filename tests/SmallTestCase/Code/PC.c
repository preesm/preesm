    #include "../src/x86.h"

    // Buffer declarations
    char Firstout_0[16];
    char Secondou_0[16];

    void computationThread_PC(void);

    void computationThread_PC(void){
        // Buffer declarations
        long i ;
        long j ;

        for(;;){
            first_dummy(Firstout_0,3/*firstParam*/);
            for(i = 0; i<4 ; i ++)
            {
                char outLoopP_0[1];
                for(j = 0; j<4 ; j ++)
                {
                    char dataOut__0[1];
                    mux(sub_j_su_0[j],outLoopP_0,dataOut__0);
                    process_tokens(dataOut__0,sub_j_su_0[j],outLoopP_0);
                }
            }
            for(i = 0; i<16 ; i ++)
            {
                third_dummy(sub_i_Se_0[i]);
            }
        }

    }//computationThread

