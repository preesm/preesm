    #include "x86_setup.h"
	#include "testcomSources.h"

    // Buffer declarations
    struct_VOLsimple VOLsimple_in[1];
    uchar *out_RefV_i;
    uchar frame_V_o_in[342912];
    struct_VOLsimple *out_VOLsimple_1;
    uchar init_out_frame_Y_i[1371648];
    char *out_VOLsimple_0;
    int pos_fin_vlc_pos[1];
    uchar init_out_buffer_st_0[829440];
    struct_VOP VOP_VOP[1];
    int adresse_adresse_pic[1];
    int MB_pos_i_MB_pos_i[99];
    uchar *out_end_in_2;
    short InverseQuant_BlkXn_3[16];
    int *keyframes_o_end_in;
    uchar *out_RefY_i;
    short InverseQuant_BlkXn_0[16];
    int init_out_new_buff__0[1];
    int *out_end_in_3;
    uchar RefV_o_RefV_i[1];
    int init_out_new_buff_i[1];
    uchar *out_end_in_1;
    uchar frame_Y_o_in[1371648];
    uchar *out_end_in_0;
    uchar buffer_out_in[829440];
    uchar *out_data;
    int pos_o_pos_o[1];
    int ysize_o_ysize_i[1];
    uchar *out_data_0;
    int init_out_keyframes_0[2];
    struct_VOLsimple *out_VOLsimple;
    uchar RefY_o_RefY_i[1];
    short InverseQuant_BlkXn_1[16];
    uchar *out_data_1;
    uchar frame_U_o_in[342912];
    int pos_pos_i[1];
    uchar  display_display[6];
    int new_buff_o_in[1];
    uchar RefU_o_RefU_i[1];
    uchar *out_end_in;
    short InverseQuant_BlkXn_2[16];
    uchar *out_RefU_i;
    int xsize_o_xsize_i[1];
    uchar init_out_frame_U_i[342912];
    int tab_pos_X_tab_pos_X[4];
    short InverseQuant_BlkXn_5[16];
    int pos_o_pos_vol[1];
    int *pos_octet_end_in;
    uchar vop_complexity_vop_0[5];
    int init_out_pos_octet[1];
    int pos_o_pos_i[1];
    int *out_end_in_4;
    REVERSE_EVENT DCT3D_I_DCT3D_I[4096];
    short InverseQuant_BlkXn_4[16];
    uchar init_out_frame_V_i[342912];
    uchar new_buffer_buffer__0[414720];
    short Cr_BuffC_Cr_BuffC[16];
    short Cr_BuffB_Cr_BuffB[16];
    int VideoPacketHeader__0[1];
    short Lum_BuffC_Lum_BuffC[16];
    short Cb_BuffB_Cb_BuffB[16];
    short Lum_BuffD_Lum_BuffD[16];
    short Cr_BuffA_Cr_BuffA[16];
    short Lum_BuffE_Lum_BuffE[16];
    short Cb_BuffA_Cb_BuffA[16];
    short Cb_BuffC_Cb_BuffC[16];
    struct_VOP newVOP_VOP[1];
    int pos_pos[1];
    struct_VOP newVOP_VOP_0[1];
    short Lum_BuffB_Lum_BuffB[16];
    int resync_marker_[1];
    short Lum_BuffA_Lum_BuffA[16];

    DWORD WINAPI computationThread_Core1( LPVOID lpParam );

    DWORD WINAPI computationThread_Core1( LPVOID lpParam ){
        // Buffer declarations
        long i ;

        {
            init_MB_pos(MB_pos_i_MB_pos_i);
            init_init_decode_I_init_frame_U_i(init_out_frame_U_i);
            init_init_decode_I_init_frame_V_i(init_out_frame_V_i);
            init_init_decode_I_init_frame_Y_i(init_out_frame_Y_i);
            init_init_decode_I_init_keyframes_i(init_out_keyframes_0);
            init_vlc_tables_I(DCT3D_I_DCT3D_I);
            init_readm4v_double_buffering_init_buffer_state(init_out_buffer_st_0);
            init_readm4v_double_buffering_init_new_buff_i(init_out_new_buff__0);
            init_readm4v_double_buffering_init_pos_octet(init_out_pos_octet);
            init_readm4v_init_new_buff_i(init_out_new_buff_i);
            readm4v_init();
            Init_SDL(16/*edge*/, 720/*xpic*/, 576/*ypic*/);
        }

        for(;;){
            readm4v(414720/*nb_octet_to_read*/, init_out_new_buff_i, new_buffer_buffer__0);
            readm4v_double_buffering(414720/*nb_octet_to_read*/, init_out_pos_octet, init_out_new_buff__0, new_buffer_buffer__0, init_out_buffer_st_0, buffer_out_in, pos_o_pos_i);
            {//buffer_out
                out_data_0 = &buffer_out_in[0];
                out_data = &buffer_out_in[0];
                out_data_1 = &buffer_out_in[0];
                out_end_in = &buffer_out_in[0];
            }
            VideoObjectLayer(pos_o_pos_i, 6635520/*length*/, out_data_0, VOLsimple_in, vop_complexity_vop_0, pos_o_pos_vol, xsize_o_xsize_i, ysize_o_ysize_i);
            {//VOPsimple
                out_VOLsimple = &VOLsimple_in[0];
                out_VOLsimple_1 = &VOLsimple_in[0];
                out_VOLsimple_0 = &VOLsimple_in[0];
            }
            VideoObjectPlaneI(pos_o_pos_vol, out_data, out_VOLsimple, vop_complexity_vop_0, pos_pos_i, VOP_VOP);
            init_decode_I_frame(out_VOLsimple_1, pos_pos_i, init_out_frame_Y_i, init_out_frame_U_i, init_out_frame_V_i, init_out_keyframes_0, InverseQuant_BlkXn_1, InverseQuant_BlkXn_4, InverseQuant_BlkXn_0, InverseQuant_BlkXn_2, InverseQuant_BlkXn_5, InverseQuant_BlkXn_3, adresse_adresse_pic, display_display, keyframes_o_end_in, pos_o_pos_o, tab_pos_X_tab_pos_X);
            {//decode_I
                for(i = 0; i<99 ; i ++)
                {//bidon_decode_I
                    uchar *outSub_i_out_data_1 = &out_data_1 [((i*(829440))%0)];
                    int *outSub_i_MB_pos_i__0 = &MB_pos_i_MB_pos_i [((i*(1))%99)];
                    char *outSub_i_out_VOLsi_0 = &out_VOLsimple_0 [((i*(1))%0)];
                    int *_resync_marker = &resync_marker_ [((0*1)%1)];
                    int *_resync_marker_0 = &resync_marker_ [((0*1)%1)];
                    int *_resync_marker_1 = &resync_marker_ [((0*1)%1)];
                    VideoPacketHeaderI(outSub_i_out_data_1, pos_o_pos_o, outSub_i_out_VOLsi_0, VOP_VOP, outSub_i_MB_pos_i__0, newVOP_VOP, VideoPacketHeader__0, resync_marker_);
                    Param_MB_I(VideoPacketHeader__0, outSub_i_out_data_1, newVOP_VOP, newVOP_VOP_0, pos_pos);
                    StockBlocksLum(outSub_i_MB_pos_i__0, _resync_marker, InverseQuant_BlkXn_4, InverseQuant_BlkXn_0, InverseQuant_BlkXn_2, outSub_i_out_VOLsi_0, Lum_BuffA_Lum_BuffA, Lum_BuffB_Lum_BuffB, Lum_BuffC_Lum_BuffC, Lum_BuffD_Lum_BuffD, Lum_BuffE_Lum_BuffE);
                    StockBlocksCb(outSub_i_MB_pos_i__0, InverseQuant_BlkXn_5, outSub_i_out_VOLsi_0, _resync_marker_0, Cb_BuffA_Cb_BuffA, Cb_BuffB_Cb_BuffB, Cb_BuffC_Cb_BuffC);
                    StockBlocksCr(outSub_i_MB_pos_i__0, InverseQuant_BlkXn_3, outSub_i_out_VOLsi_0, _resync_marker_1, Cr_BuffA_Cr_BuffA, Cr_BuffB_Cr_BuffB, Cr_BuffC_Cr_BuffC);
                    decode_I_une_partie(outSub_i_MB_pos_i__0, outSub_i_out_VOLsi_0, newVOP_VOP_0, outSub_i_out_data_1, display_display, pos_pos, tab_pos_X_tab_pos_X, DCT3D_I_DCT3D_I, InverseQuant_BlkXn_1, InverseQuant_BlkXn_4, InverseQuant_BlkXn_0, InverseQuant_BlkXn_2, InverseQuant_BlkXn_5, InverseQuant_BlkXn_3, Lum_BuffA_Lum_BuffA, Lum_BuffB_Lum_BuffB, Lum_BuffC_Lum_BuffC, Lum_BuffD_Lum_BuffD, Lum_BuffE_Lum_BuffE, Cb_BuffA_Cb_BuffA, Cb_BuffB_Cb_BuffB, Cb_BuffC_Cb_BuffC, Cr_BuffA_Cr_BuffA, Cr_BuffB_Cr_BuffB, Cr_BuffC_Cr_BuffC, frame_U_o_in, frame_V_o_in, frame_Y_o_in, pos_fin_vlc_pos);
                }
            }
            New_buffer(414720/*nb_octet_to_read*/, pos_fin_vlc_pos, new_buff_o_in, pos_octet_end_in);
            {//new_buff_o
                out_end_in_4 = &new_buff_o_in[0];
                out_end_in_3 = &new_buff_o_in[0];
            }
            {//frame_U
                out_RefU_i = &frame_U_o_in[0];
                out_end_in_0 = &frame_U_o_in[0];
            }
            {//frame_V
                out_RefV_i = &frame_V_o_in[0];
                out_end_in_1 = &frame_V_o_in[0];
            }
            {//frame_Y
                out_RefY_i = &frame_Y_o_in[0];
                out_end_in_2 = &frame_Y_o_in[0];
            }
            extract_picture(xsize_o_xsize_i, ysize_o_ysize_i, 16/*edge*/, 0/*crop*/, out_RefY_i, out_RefU_i, out_RefV_i, adresse_adresse_pic, RefY_o_RefY_i, RefU_o_RefU_i, RefV_o_RefV_i);
            SDL_Display(16/*edge*/, 720/*xpic*/, 576/*ypic*/, RefY_o_RefY_i, RefU_o_RefU_i, RefV_o_RefV_i);
        }

        {
            CloseSDLDisplay();
        }

        return 0;
    }//computationThread

