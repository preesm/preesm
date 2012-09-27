    #include "../include/x86.h"

    // Buffer declarations
    i32 data_data__0[128]; //Generator -> explode_Generator_data_0
    i32 data_out_d_1[128]; //r_implode_Displayer_data2Displayer_0 -> Displayer
    i32 data_data__3[64]; //explode_Generator_data_0 -> s_explode_Generator_data_0Transformer_0_1_0
    i32 data_end_in[64]; //explode_Generator_data -> s_explode_Generator_dataexplode_Generator_data_end_data_0
    i32 data_data__2[64]; //explode_Generator_data -> s_explode_Generator_dataTransformer_1_0
    i32 data_data__1[128]; //Generator -> explode_Generator_data
    i32 init_out_d_0[64]; //Transformer_0_0_init_data_in -> s_Transformer_0_0_init_data_inTransformer_0_0_0
    i32 *data_end_i_0; //explode_Generator_data_0 -> explode_Generator_data_0_end_data
    i32 data_out_d_0[128]; //r_implode_Displayer_data1Displayer_0 -> Displayer
    Fifo Transformer_0_0_init_data_in ;
    Fifo Transformer_0_0_init_data_in ;

    void *computationThread_CortexA9_1(void *arg);

    void *computationThread_CortexA9_1(void *arg){
        // Buffer declarations

        { // Initialization phase number 1
            new_fifo(&Transformer_0_0_init_data_in, sizeof(i32)/*size*/, 64/*fifo_size*/); //
            sendData(msg,CortexA9_1,CortexA9_2,init_out_d_0,64*sizeof(i32)); // s_Transformer_0_0_init_data_inTransformer_0_0_0
            Generator_init_function1(1/*param*/, data_data__1); //Generator
            {//explode_Generator_data
                memcpy(data_data__2, &data_data__1[0], 64*sizeof(i32)/*size*/); //
                memcpy(data_end_in, &data_data__1[64], 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_1,CortexA9_2,data_data__2,64*sizeof(i32)); // s_explode_Generator_dataTransformer_1_0
            sendData(msg,CortexA9_1,CortexA9_2,data_end_in,64*sizeof(i32)); // s_explode_Generator_dataexplode_Generator_data_end_data_0
            {//explode_Generator_data_0
                data_end_i_0 = &data_data__0[128];
                memcpy(data_data__3, &data_data__0[0], 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_1,CortexA9_2,data_data__3,64*sizeof(i32)); // s_explode_Generator_data_0Transformer_0_1_0
            push(&Transformer_0_0_init_data_in, data_end_i_0, 64/*nb_token*/); //
        }

        { // Initialization phase number 0
            new_fifo(&Transformer_0_0_init_data_in, sizeof(i32)/*size*/, 64/*fifo_size*/); //
            sendData(msg,CortexA9_1,CortexA9_2,init_out_d_0,64*sizeof(i32)); // s_Transformer_0_0_init_data_inTransformer_0_0_0
            Generator_init_function0(1/*param*/, data_data__1); //Generator
            {//explode_Generator_data
                memcpy(data_data__2, &data_data__1[0], 64*sizeof(i32)/*size*/); //
                memcpy(data_end_in, &data_data__1[64], 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_1,CortexA9_2,data_data__2,64*sizeof(i32)); // s_explode_Generator_dataTransformer_1_0
            sendData(msg,CortexA9_1,CortexA9_2,data_end_in,64*sizeof(i32)); // s_explode_Generator_dataexplode_Generator_data_end_data_0
            {//explode_Generator_data_0
                data_end_i_0 = &data_data__0[128];
                memcpy(data_data__3, &data_data__0[0], 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_1,CortexA9_2,data_data__3,64*sizeof(i32)); // s_explode_Generator_data_0Transformer_0_1_0
            push(&Transformer_0_0_init_data_in, data_end_i_0, 64/*nb_token*/); //
        }

        for(;;){ // Main loop of computation
            Generator_generate(1/*param*/, data_data__1); //Generator
            pull(&Transformer_0_0_init_data_in, init_out_d_0, 64/*nb_token*/); //
            {//explode_Generator_data
                memcpy(data_data__2, &data_data__1[0], 64*sizeof(i32)/*size*/); //
                memcpy(data_end_in, &data_data__1[64], 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_1,CortexA9_2,data_data__2,64*sizeof(i32)); // s_explode_Generator_dataTransformer_1_0
            sendData(msg,CortexA9_1,CortexA9_2,data_end_in,64*sizeof(i32)); // s_explode_Generator_dataexplode_Generator_data_end_data_0
            {//explode_Generator_data_0
                data_end_i_0 = &data_data__0[128];
                memcpy(data_data__3, &data_data__0[0], 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_1,CortexA9_2,data_data__3,64*sizeof(i32)); // s_explode_Generator_data_0Transformer_0_1_0
            push(&Transformer_0_0_init_data_in, data_end_i_0, 64/*nb_token*/); //
            receiveData(msg,CortexA9_2,CortexA9_1,data_out_d_0,128*sizeof(i32)); // r_implode_Displayer_data1Displayer_0
            receiveData(msg,CortexA9_2,CortexA9_1,data_out_d_1,128*sizeof(i32)); // r_implode_Displayer_data2Displayer_0
            Displayer_display(data_out_d_0, data_out_d_1); //Displayer
        }

        return 0;
    }//computationThread

