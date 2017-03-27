    #include "x86_setup.h"
	#include "HIGH_LV.h"

    // Buffer declarations

    uchar frame_Y_o_in[1371648];
    uchar frame_U_o_in[342912];
    uchar frame_V_o_in[342912];
    uchar out_end_in_0[1371648];
	uchar out_end_in_1[342912];
    uchar out_end_in_2[342912];
    uchar *init_out_frame_Y_i;
	uchar *init_out_frame_U_i;
    uchar *init_out_frame_V_i;
	uchar out_end_in[829440];
	uchar *init_out_buffer_st_0;
	uchar buffer_out_in[829440];
    int *init_out_new_buff_i;
	int new_buff_o_in[1];
    int *init_out_new_buff__0;
    int out_end_in_3[1];
    int out_end_in_4[1];
    int pos_octet_end_in[1];
    int *init_out_pos_octet;
    int keyframes_o_end_in[2];
    int *init_out_keyframes_0;
	int pos_o_pos_i[1];
    int pos_fin_vlc_pos[1];
    uchar *out_RefU_i;
    struct_VOLsimple *out_VOLsimple_0;
    uchar *out_data_0;
    uchar RefY_o_RefY_i[1371648];
    uchar vop_complexity_vop_0[5];
    int pos_o_pos_vol[1];
    struct_VOLsimple VOLsimple_in[1];
    int ysize_o_ysize_i[1];
    int pos_pos[1];
    int vop_coding_type_vo_0[1];
    uchar new_buffer_buffer__0[414720];
    uchar RefV_o_RefV_i[342912];
    int adresse_adresse_pic[1];
    struct_VOP VOP_VOP[1];
    uchar *out_RefY_i;
    uchar *out_data_1;
    uchar RefU_o_RefU_i[342912];
    uchar *out_RefV_i;
    struct_VOLsimple *out_VOLsimple;
    uchar *out_data;
    REVERSE_EVENT DCT3D_I_DCT3D_I[4096];
    int xsize_o_xsize_i[1];
	image_type Display_Extract_Image_Y_o[685832];

	extern int _argc;
	extern char** _argv;

    DWORD WINAPI computationThread_Core1( LPVOID lpParam );

    DWORD WINAPI computationThread_Core1( LPVOID lpParam ){
        // Buffer declarations

		int z = 0;
		int k = 0; //ajouter//
		int i = 0; //ajouter//
        {
			  pos_octet_end_in[0] = 829440;
			  out_end_in_3[0] = 1;
			  out_end_in_4[0] = 1;

         	  init_vlc_tables_I(DCT3D_I_DCT3D_I);
              readm4v_init(_argc,_argv);
              Init_SDL(16/*edge*/, 720/*xpic*/, 576/*ypic*/);
        }

        for(i=0;i<50;i++){

			init_out_frame_Y_i = &out_end_in_0[0];
			init_out_frame_U_i = &out_end_in_1[0];
			init_out_frame_V_i = &out_end_in_2[0];
			init_out_buffer_st_0 = &out_end_in[0];
			init_out_new_buff_i = &out_end_in_3[0];
			init_out_new_buff__0 = &out_end_in_4[0];
			init_out_pos_octet = &pos_octet_end_in[0];
			init_out_keyframes_0 = &keyframes_o_end_in[0];

			
            readm4v_preesm(414720/*nb_octet_to_read*/, init_out_new_buff_i, new_buffer_buffer__0);

			readm4v_double_buffering_preesm(414720/*nb_octet_to_read*/, init_out_pos_octet, init_out_new_buff__0, new_buffer_buffer__0, init_out_buffer_st_0, buffer_out_in, pos_o_pos_i);
            {//buffer_out
                out_data_1 = &buffer_out_in[0];
                out_data = &buffer_out_in[0];
                out_data_0 = &buffer_out_in[0];
				memcpy(out_end_in,buffer_out_in,829440*sizeof(unsigned char));
            }

            VideoObjectLayer_preesm(pos_o_pos_i, 6635520/*length*/, out_data_1, VOLsimple_in, vop_complexity_vop_0, pos_o_pos_vol, xsize_o_xsize_i, ysize_o_ysize_i);
            {//VOPsimple
                out_VOLsimple = &VOLsimple_in[0];
                out_VOLsimple_0 = &VOLsimple_in[0];
            }

            VideoObjectPlaneI_preesm(pos_o_pos_vol, out_data_0, out_VOLsimple_0, vop_complexity_vop_0, pos_pos, VOP_VOP, vop_coding_type_vo_0);

            decode_I_frame_preesm(out_data, out_VOLsimple, pos_pos, VOP_VOP, DCT3D_I_DCT3D_I, pos_fin_vlc_pos, adresse_adresse_pic, init_out_frame_Y_i, init_out_frame_U_i, init_out_frame_V_i, init_out_keyframes_0, frame_Y_o_in, frame_U_o_in, frame_V_o_in, keyframes_o_end_in, vop_coding_type_vo_0);

			New_buffer_preesm(414720/*nb_octet_to_read*/, pos_fin_vlc_pos, new_buff_o_in, pos_octet_end_in);
            {//new_buff_o
				memcpy(out_end_in_3,new_buff_o_in,sizeof(int));
				memcpy(out_end_in_4,new_buff_o_in,sizeof(int));
            }

            {//frame_U
                out_RefU_i = &frame_U_o_in[0];
				memcpy(out_end_in_1,frame_U_o_in,342912*sizeof(unsigned char));
            }
            {//frame_V
                out_RefV_i = &frame_V_o_in[0];
				memcpy(out_end_in_2,frame_V_o_in,342912*sizeof(unsigned char));
            }
            {//frame_Y
                out_RefY_i = &frame_Y_o_in[0];
				memcpy(out_end_in_0,frame_Y_o_in,1371648*sizeof(unsigned char));
            }
			if(k == 0) {k=1;}
			else{
			 extract_picture_preesm(xsize_o_xsize_i,ysize_o_ysize_i, 16/*edge*/, 0/*crop*/, out_RefY_i, out_RefU_i, out_RefV_i, adresse_adresse_pic,Display_Extract_Image_Y_o);	 
			 SDL_Display_preesm(16,Display_Extract_Image_Y_o);
			 }
			}

        {
            CloseSDLDisplay();
        }
		system("pause");
        return 0;
    }//computationThread

