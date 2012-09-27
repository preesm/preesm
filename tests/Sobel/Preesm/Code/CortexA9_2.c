    #include "../include/x86.h"

    // Buffer declarations
    i32 data_end_i_1[64]; //r_explode_Generator_dataexplode_Generator_data_end_data_0 -> explode_Generator_data_end_data
    i32 data_out_d_7[128]; //implode_Displayer_data2 -> s_implode_Displayer_data2Displayer_0
    i32 data_out_d_4[64]; //Transformer_1 -> implode_Displayer_data1
    i32 data_out_d_3[64]; //Transformer_0_0 -> implode_Displayer_data2
    i32 data_out_d_2[64]; //Transformer_0 -> implode_Displayer_data1
    i32 data_out_d_5[64]; //Transformer_0_1 -> implode_Displayer_data2
    i32 data_data__5[64]; //r_explode_Generator_data_0Transformer_0_1_0 -> Transformer_0_1
    i32 init_out_d_2[64]; //r_Transformer_0_0_init_data_inTransformer_0_0_0 -> Transformer_0_0
    i32 init_out_d_1[64]; //Transformer_0_init_data_in -> Transformer_0
    i32 data_out_d_6[128]; //implode_Displayer_data1 -> s_implode_Displayer_data1Displayer_0
    i32 data_data__4[64]; //r_explode_Generator_dataTransformer_1_0 -> Transformer_1
    Fifo Transformer_0_init_data_in ;
    Fifo Transformer_0_init_data_in ;

    void *computationThread_CortexA9_2(void *arg);

    void *computationThread_CortexA9_2(void *arg){
        // Buffer declarations

        { // Initialization phase number 1
            new_fifo(&Transformer_0_init_data_in, sizeof(i32)/*size*/, 64/*fifo_size*/); //
            push(&Transformer_0_init_data_in, data_end_i_1, 64/*nb_token*/); //
            {//implode_Displayer_data1
                memcpy(&data_out_d_6[0], data_out_d_2, 64*sizeof(i32)/*size*/); //
                memcpy(&data_out_d_6[64], data_out_d_4, 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_2,CortexA9_1,data_out_d_6,128*sizeof(i32)); // s_implode_Displayer_data1Displayer_0
            {//implode_Displayer_data2
                memcpy(&data_out_d_7[0], data_out_d_3, 64*sizeof(i32)/*size*/); //
                memcpy(&data_out_d_7[64], data_out_d_5, 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_2,CortexA9_1,data_out_d_7,128*sizeof(i32)); // s_implode_Displayer_data2Displayer_0
        }

        { // Initialization phase number 0
            new_fifo(&Transformer_0_init_data_in, sizeof(i32)/*size*/, 64/*fifo_size*/); //
            push(&Transformer_0_init_data_in, data_end_i_1, 64/*nb_token*/); //
            {//implode_Displayer_data1
                memcpy(&data_out_d_6[0], data_out_d_2, 64*sizeof(i32)/*size*/); //
                memcpy(&data_out_d_6[64], data_out_d_4, 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_2,CortexA9_1,data_out_d_6,128*sizeof(i32)); // s_implode_Displayer_data1Displayer_0
            {//implode_Displayer_data2
                memcpy(&data_out_d_7[0], data_out_d_3, 64*sizeof(i32)/*size*/); //
                memcpy(&data_out_d_7[64], data_out_d_5, 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_2,CortexA9_1,data_out_d_7,128*sizeof(i32)); // s_implode_Displayer_data2Displayer_0
        }

        for(;;){ // Main loop of computation
            pull(&Transformer_0_init_data_in, init_out_d_1, 64/*nb_token*/); //
            Transformer_transform(2/*OFFSET*/, init_out_d_1, data_out_d_2); //Transformer_0
            receiveData(msg,CortexA9_1,CortexA9_2,init_out_d_2,64*sizeof(i32)); // r_Transformer_0_0_init_data_inTransformer_0_0_0
            Transformer_transform(1/*OFFSET*/, init_out_d_2, data_out_d_3); //Transformer_0_0
            receiveData(msg,CortexA9_1,CortexA9_2,data_data__5,64*sizeof(i32)); // r_explode_Generator_data_0Transformer_0_1_0
            Transformer_transform(1/*OFFSET*/, data_data__5, data_out_d_5); //Transformer_0_1
            receiveData(msg,CortexA9_1,CortexA9_2,data_data__4,64*sizeof(i32)); // r_explode_Generator_dataTransformer_1_0
            Transformer_transform(2/*OFFSET*/, data_data__4, data_out_d_4); //Transformer_1
            push(&Transformer_0_init_data_in, data_end_i_1, 64/*nb_token*/); //
            {//implode_Displayer_data1
                memcpy(&data_out_d_6[0], data_out_d_2, 64*sizeof(i32)/*size*/); //
                memcpy(&data_out_d_6[64], data_out_d_4, 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_2,CortexA9_1,data_out_d_6,128*sizeof(i32)); // s_implode_Displayer_data1Displayer_0
            {//implode_Displayer_data2
                memcpy(&data_out_d_7[0], data_out_d_3, 64*sizeof(i32)/*size*/); //
                memcpy(&data_out_d_7[64], data_out_d_5, 64*sizeof(i32)/*size*/); //
            }
            sendData(msg,CortexA9_2,CortexA9_1,data_out_d_7,128*sizeof(i32)); // s_implode_Displayer_data2Displayer_0
        }

        return 0;
    }//computationThread

