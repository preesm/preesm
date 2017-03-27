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
    #include "x86_setup.h"
	#include "LOW_LV.h"

    // Buffer declarations
	uchar *init_out_frame_U_i;
	uchar out_end_in_0[342912];
	uchar *init_out_frame_V_i;
	uchar out_end_in_1[342912];
	uchar *init_out_frame_Y_i;
	uchar out_end_in_2[1371648];
	int *init_out_keyframes_0;
	int keyframes_o_end_in[2];
	uchar *init_out_buffer_st_0;
	uchar out_end_in[829440];
	int *init_out_new_buff__0;
	int out_end_in_4[1];
	int *init_out_pos_octet;
	int pos_octet_end_in[1];
	int *init_out_new_buff_i;
	int out_end_in_3[1];
    int new_buff_o_in[1];
    short InverseQuant_BlkXn_0[16];
    int pos_pos_i[1];
    uchar *out_RefU_i;
    int xsize_o_xsize_i[1];
    uchar *out_data_1;
    REVERSE_EVENT DCT3D_I_DCT3D_I[4096];
    uchar *frame_U_o_in/*[342912]*/;
    struct_VOLsimple *out_VOLsimple_0;
    uchar *frame_Y_o_in/*[1371648]*/;
    uchar *out_RefV_i;
    short InverseQuant_BlkXn_1[16];
    uchar Y_Y[1];
    uchar V_V[1];
    uchar U_U[1];
    uchar vop_complexity_vop_0[5];
    struct_VOLsimple *out_VOLsimple;
    uchar new_buffer_buffer__0[414720];
    uchar *out_data;
    int pos_fin_vlc_pos[1];
    short InverseQuant_BlkXn_3[16];
    uchar *frame_V_o_in/*[342912]*/;
    struct_VOP VOP_VOP[1];
    uchar *out_RefY_i;
    struct_VOLsimple *out_VOLsimple_1;
    int pos_o_pos_vol[1];
    uchar *display_display[6];
    uchar buffer_out_in[829440];
    short InverseQuant_BlkXn_2[16];
    int pos_o_pos_i[1];
    short InverseQuant_BlkXn_4[16];
    short InverseQuant_BlkXn_5[16];
    int MB_pos_i_MB_pos_i[99];
    struct_VOLsimple VOLsimple_in[1];
    int adresse_adresse_pic[1];
    int ysize_o_ysize_i[1];
    uchar *out_data_0;
    int pos_o_pos_o[1];
    uchar XCr_src[6336];
    uchar X4_src_X4[6336];
    uchar XCb_src[6336];
    uchar X3_src_X3[6336];
    int pos_X3_pos_X3[99];
    uchar X1_src_X1[6336];
    uchar X2_src_X2[6336];
    int pos_X2_pos_X2[99];
    int pos_XCr_pos_X[99];
    int pos_XCb_pos_X[99];
    int pos_X1_pos_X1[99];
    int pos_X4_pos_X4[99];
    int VideoPacketHeader__0[1];
    int pos_pos[1];
    char o_i[1];
    int resync_marker_resy_0[1];
    struct_VOP newVOP_VOP_0[1];
    struct_VOP newVOP_VOP[1];
    short fx2_src[128];
    short fx1_src[128];
    short fcolor_src_0[128];
    int pos_fin_vlc_positi_1[1];
    short fcolor_src[128];
    short fx4_src[128];
    int pos_fin_vlc_positi_0[1];
    short fx3_src[128];
    short F_F_in[64];
    short BuffA_[16];
    short F_F_in_1[64];
    short F_F_in_2[64];
    short BuffC_[16];
    int pos_fin_vlc_positi_2[1];
    short BuffD_BuffD[16];
    short F_F_in_0[64];
    short BuffB_BuffB[16];
    short BuffE_BuffE[16];
    int pos_fin_vlc_positi_4[1];
    short InverseQuant_BlkXn_6[16];
    int pos_fin_vlc_positi_3[1];
    short BuffC_BuffC_Cr[16];
    short BuffB_BuffB_Cb[16];
    short F_F_in[64];
    short BuffA_BuffA_Cr[16];
    short BuffB_BuffB_Cr[16];
    short BuffC_BuffC_Cb[16];
    short BuffA_BuffA_Cb[16];
    short F_F_in[64];
    int prediction_directi_0[1];
    short QF_QF[64];
    int dc_scaler_dc_scaler[1];
    short F00pred_F00pred[1];
    short PQF_PQF[64];
    short QPpred_QPpred[1];
    short QFpred_QFpred[7];
    short QPpred_QPpred[1];
    short F00pred_F00pred[1];
    short QFpred_QFpred[7];
    short PQF_PQF[64];
    int dc_scaler_dc_scaler[1];
    short QF_QF[64];
    int prediction_directi_0[1];
    int dc_scaler_dc_scaler[1];
    short QPpred_QPpred[1];
    short QF_QF[64];
    short QFpred_QFpred[7];
    int prediction_directi_0[1];
    short F00pred_F00pred[1];
    short PQF_PQF[64];
    short QF_QF[64];
    short QPpred_QPpred[1];
    short PQF_PQF[64];
    short QFpred_QFpred[7];
    short F00pred_F00pred[1];
    int prediction_directi_0[1];
    int dc_scaler_dc_scaler[1];
    int dc_scaler_dc_scaler[1];
    short F00pred_F00pred[1];
    int prediction_directi_0[1];
    short PQF_PQF[64];
    short QPpred_QPpred[1];
    short QFpred_QFpred[7];
    short QF_QF[64];
    int dc_scaler_dc_scaler[1];
    short QFpred_QFpred[7];
    short QPpred_QPpred[1];
    short PQF_PQF[64];
    short QF_QF[64];
    int prediction_directi_0[1];
    short F00pred_F00pred[1];

	extern int _argc;
	extern char** _argv;
	int vop_coding_type[1];
	int MB_number[1];
	image_type Display_Extract_Image_Y_o[685832];
	int MB_pos_suivant[1];
	int tab_pos_X[4];

	DWORD WINAPI computationThread_Core1( LPVOID lpParam );

    DWORD WINAPI computationThread_Core1( LPVOID lpParam ){
        // Buffer declarations
        long i ;
		int z = 0;
		int j = 0;
		int k = 0;

		//int width;
		//const int edge_size2 = EDGE_SIZE >> 1 ;

			pos_octet_end_in[0] = 829440;
			out_end_in_3[0] = 1;
			out_end_in_4[0] = 1;


        {
            init_MB_pos(MB_pos_i_MB_pos_i);

            init_vlc_tables_I(DCT3D_I_DCT3D_I);

            readm4v_init(_argc,_argv);
            Init_SDL(16/*edge*/, 720/*xpic*/, 576/*ypic*/);
        }

        for(;;){


			init_out_frame_U_i   = &out_end_in_0[0];
			init_out_frame_V_i   = &out_end_in_1[0];
			init_out_frame_Y_i   = &out_end_in_2[0];
			init_out_keyframes_0 = &keyframes_o_end_in[0];
			init_out_buffer_st_0 = &out_end_in[0];
			init_out_new_buff__0 = &out_end_in_4[0];
			init_out_pos_octet   = &pos_octet_end_in[0];
			init_out_new_buff_i  = &out_end_in_3[0];

            readm4v_preesm(414720/*nb_octet_to_read*/, init_out_new_buff_i, new_buffer_buffer__0);
            readm4v_double_buffering_preesm(414720/*nb_octet_to_read*/, init_out_pos_octet, init_out_new_buff__0, new_buffer_buffer__0, init_out_buffer_st_0, buffer_out_in, pos_o_pos_i);
            {//buffer_out
                out_data_1 = &buffer_out_in[0];
                out_data = &buffer_out_in[0];
                out_data_0 = &buffer_out_in[0];
				memcpy(out_end_in, buffer_out_in, 829440*sizeof(uchar)/*size*/);
            }
            VideoObjectLayer_preesm(pos_o_pos_i, 6635520/*length*/, out_data_1, VOLsimple_in, vop_complexity_vop_0, pos_o_pos_vol, xsize_o_xsize_i, ysize_o_ysize_i);
            {//VOPsimple
                out_VOLsimple = &VOLsimple_in[0];
                out_VOLsimple_1 = &VOLsimple_in[0];
                out_VOLsimple_0 = &VOLsimple_in[0];
            }
            VideoObjectPlaneI_preesm(pos_o_pos_vol, out_data, out_VOLsimple, vop_complexity_vop_0, pos_pos_i, VOP_VOP,vop_coding_type);
            init_decode_I_frame_preesm(out_VOLsimple_1, pos_pos_i, init_out_frame_Y_i, init_out_frame_U_i, init_out_frame_V_i, init_out_keyframes_0, InverseQuant_BlkXn_0, InverseQuant_BlkXn_1, InverseQuant_BlkXn_2, InverseQuant_BlkXn_3, InverseQuant_BlkXn_4, InverseQuant_BlkXn_5, adresse_adresse_pic, display_display, keyframes_o_end_in, pos_o_pos_o, tab_pos_X);
            {//decode_I
						int width;
						const int edge_size2 = EDGE_SIZE >> 1 ;
                {//MB_I(x99)
                    for(i = 0; i<99 ; i ++)
                    {//bidon_MB_I(x99)
                        int *inSub_i_pos_X1_pos_0 = &pos_X1_pos_X1 [((i*(1))%99)];
                        int *inSub_i_pos_X2_pos_0 = &pos_X2_pos_X2 [((i*(1))%99)];
                        int *inSub_i_pos_X3_pos_0 = &pos_X3_pos_X3 [((i*(1))%99)];
                        int *inSub_i_pos_X4_pos_0 = &pos_X4_pos_X4 [((i*(1))%99)];
                        int *inSub_i_pos_XCb_po_0 = &pos_XCb_pos_X [((i*(1))%99)];
                        int *inSub_i_pos_XCr_po_0 = &pos_XCr_pos_X [((i*(1))%99)];
                        uchar *inSub_i_X1_src_X1 = &X1_src_X1 [((i*(64))%6336)];
                        uchar *inSub_i_X2_src_X2 = &X2_src_X2 [((i*(64))%6336)];
                        uchar *inSub_i_X3_src_X3 = &X3_src_X3 [((i*(64))%6336)];
                        uchar *inSub_i_X4_src_X4 = &X4_src_X4 [((i*(64))%6336)];
                        uchar *inSub_i_XCb_src = &XCb_src [((i*(64))%6336)];
                        uchar *inSub_i_XCr_src = &XCr_src [((i*(64))%6336)];
                        uchar *outSub_i_out_data_0 = &out_data_0 [((0*(829440))%1)];
                        int *outSub_i_MB_pos_i__0 = &MB_pos_i_MB_pos_i [((i*(1))%99)];
                        struct_VOLsimple *outSub_i_out_VOLsi_0 = &out_VOLsimple_0 [((0*(1))%1)];
                        VideoPacketHeaderI_preesm(outSub_i_out_data_0, pos_o_pos_o, outSub_i_out_VOLsi_0, VOP_VOP, outSub_i_MB_pos_i__0, newVOP_VOP, VideoPacketHeader__0, resync_marker_resy_0);
                        Param_MB_I_preesm(VideoPacketHeader__0, outSub_i_out_data_0, newVOP_VOP, newVOP_VOP_0, pos_pos);						
                        {//MB_INTRA
                            {//Lum
                                short *_BuffA = &BuffA_ [((0*16)%16)];
                                short *_BuffA_0 = &BuffA_ [((0*16)%16)];
                                short *_BuffC = &BuffC_ [((0*16)%16)];
                                short *_BuffC_0 = &BuffC_ [((0*16)%16)];
                                StockBlocksLum_preesm(outSub_i_MB_pos_i__0, resync_marker_resy_0, InverseQuant_BlkXn_1, InverseQuant_BlkXn_2, InverseQuant_BlkXn_3, outSub_i_out_VOLsi_0, BuffA_, BuffB_BuffB, BuffC_, BuffD_BuffD, BuffE_BuffE);
								{//X1
                                    VLCinverseI(5/*N*/, outSub_i_MB_pos_i__0[0], pos_pos[0], outSub_i_out_data_0, DCT3D_I_DCT3D_I, _BuffA, BuffB_BuffB, _BuffC, newVOP_VOP_0, /*pos_fin_vlc_pos*/pos_fin_vlc_positi_2, PQF_PQF, QFpred_QFpred, F00pred_F00pred, QPpred_QPpred, prediction_directi_0);
                                    InverseACDCpred(0/*type*/, PQF_PQF, QFpred_QFpred, F00pred_F00pred[0], QPpred_QPpred[0], newVOP_VOP_0, prediction_directi_0[0], QF_QF, dc_scaler_dc_scaler);
                                    InverseQuantI(QF_QF, newVOP_VOP_0, dc_scaler_dc_scaler[0], outSub_i_out_VOLsi_0, F_F_in, InverseQuant_BlkXn_0);
                                }
                                {//X2
                                    VLCinverseI(4/*N*/, outSub_i_MB_pos_i__0[0], /*pos_fin_vlc_pos[0]*/pos_fin_vlc_positi_2[0], outSub_i_out_data_0, DCT3D_I_DCT3D_I, InverseQuant_BlkXn_0, _BuffC_0, BuffD_BuffD, newVOP_VOP_0, /*pos_fin_vlc_pos*/pos_fin_vlc_positi_3, PQF_PQF, QFpred_QFpred, F00pred_F00pred, QPpred_QPpred, prediction_directi_0);
                                    InverseACDCpred(1/*type*/, PQF_PQF, QFpred_QFpred, F00pred_F00pred[0], QPpred_QPpred[0], newVOP_VOP_0, prediction_directi_0[0], QF_QF, dc_scaler_dc_scaler);
                                    InverseQuantI(QF_QF, newVOP_VOP_0, dc_scaler_dc_scaler[0], outSub_i_out_VOLsi_0, F_F_in_0, InverseQuant_BlkXn_1);
                                }
                                InverseDCT(F_F_in, fx1_src);
                                {//X3
                                    VLCinverseI(3/*N*/, outSub_i_MB_pos_i__0[0], /*pos_fin_vlc_pos[0]*/pos_fin_vlc_positi_3[0], outSub_i_out_data_0, DCT3D_I_DCT3D_I, BuffE_BuffE, _BuffA_0, InverseQuant_BlkXn_0, newVOP_VOP_0, /*pos_fin_vlc_pos*/pos_fin_vlc_positi_4, PQF_PQF, QFpred_QFpred, F00pred_F00pred, QPpred_QPpred, prediction_directi_0);
                                    InverseACDCpred(2/*type*/, PQF_PQF, QFpred_QFpred, F00pred_F00pred[0], QPpred_QPpred[0], newVOP_VOP_0, prediction_directi_0[0], QF_QF, dc_scaler_dc_scaler);
                                    InverseQuantI(QF_QF, newVOP_VOP_0, dc_scaler_dc_scaler[0], outSub_i_out_VOLsi_0, F_F_in_1, InverseQuant_BlkXn_2);
                                }
                                InverseDCT(F_F_in_0, fx2_src);
                                {//X4
                                    VLCinverseI(2/*N*/, outSub_i_MB_pos_i__0[0], /*pos_fin_vlc_pos[0]*/pos_fin_vlc_positi_4[0], outSub_i_out_data_0, DCT3D_I_DCT3D_I, InverseQuant_BlkXn_2, InverseQuant_BlkXn_0, InverseQuant_BlkXn_1, newVOP_VOP_0, /*pos_fin_vlc_pos*/pos_fin_vlc_positi_0, PQF_PQF, QFpred_QFpred, F00pred_F00pred, QPpred_QPpred, prediction_directi_0);
                                    InverseACDCpred(3/*type*/, PQF_PQF, QFpred_QFpred, F00pred_F00pred[0], QPpred_QPpred[0], newVOP_VOP_0, prediction_directi_0[0], QF_QF, dc_scaler_dc_scaler);
                                    InverseQuantI(QF_QF, newVOP_VOP_0, dc_scaler_dc_scaler[0], outSub_i_out_VOLsi_0, F_F_in_2, InverseQuant_BlkXn_3);
                                }
                                InverseDCT(F_F_in_1, fx3_src);
                                InverseDCT(F_F_in_2, fx4_src);
                            }
                            {//Cb
                                StockBlocksCb_preesm(outSub_i_MB_pos_i__0, InverseQuant_BlkXn_4, outSub_i_out_VOLsi_0, resync_marker_resy_0, BuffA_BuffA_Cb, BuffB_BuffB_Cb, BuffC_BuffC_Cb);
                                {//XCb
                                    VLCinverseI(1/*N*/, outSub_i_MB_pos_i__0[0], /*pos_fin_vlc_pos[0]*/pos_fin_vlc_positi_0[0], outSub_i_out_data_0, DCT3D_I_DCT3D_I, BuffA_BuffA_Cb, BuffB_BuffB_Cb, BuffC_BuffC_Cb, newVOP_VOP_0, /*pos_fin_vlc_pos*/pos_fin_vlc_positi_1, PQF_PQF, QFpred_QFpred, F00pred_F00pred, QPpred_QPpred, prediction_directi_0);
									//VLCinverseI (const int N, const int posMB, const int pos_i, const unsigned char *RESTRICT data, const REVERSE_EVENT *RESTRICT DCT3D_I,  const struct_VOP *RESTRICT VOP, int *RESTRICT pos_fin_vlc, short *RESTRICT PQF , short *RESTRICT QFpred, short *RESTRICT F00pred, short *RESTRICT QPpred, int *RESTRICT prediction_direction) ;
                                    InverseACDCpred(4/*type*/, PQF_PQF, QFpred_QFpred, F00pred_F00pred[0], QPpred_QPpred[0], newVOP_VOP_0, prediction_directi_0[0], QF_QF, dc_scaler_dc_scaler);
                                    InverseQuantI(QF_QF, newVOP_VOP_0, dc_scaler_dc_scaler[0], outSub_i_out_VOLsi_0, F_F_in, InverseQuant_BlkXn_4);
                                }
                                InverseDCT(F_F_in, fcolor_src);
                            }
                            block_sat(fx1_src, inSub_i_X1_src_X1);
                            block_sat(fx2_src, inSub_i_X2_src_X2);
                            block_sat(fx3_src, inSub_i_X3_src_X3);
                            block_sat(fx4_src, inSub_i_X4_src_X4);
                            {//Cr
                                StockBlocksCr_preesm(outSub_i_MB_pos_i__0, InverseQuant_BlkXn_5, outSub_i_out_VOLsi_0, resync_marker_resy_0, BuffA_BuffA_Cr, BuffB_BuffB_Cr, BuffC_BuffC_Cr);
								
                                {//XCr
                                    VLCinverseI(0/*N*/, outSub_i_MB_pos_i__0[0], /*pos_fin_vlc_pos[0]*/pos_fin_vlc_positi_1[0], outSub_i_out_data_0, DCT3D_I_DCT3D_I, BuffA_BuffA_Cr, BuffB_BuffB_Cr, BuffC_BuffC_Cr, newVOP_VOP_0, pos_fin_vlc_pos, PQF_PQF, QFpred_QFpred, F00pred_F00pred, QPpred_QPpred, prediction_directi_0);
                                    InverseACDCpred(5/*type*/, PQF_PQF, QFpred_QFpred, F00pred_F00pred[0], QPpred_QPpred[0], newVOP_VOP_0, prediction_directi_0[0], QF_QF, dc_scaler_dc_scaler);
                                    InverseQuantI(QF_QF, newVOP_VOP_0, dc_scaler_dc_scaler[0], outSub_i_out_VOLsi_0, F_F_in, InverseQuant_BlkXn_5);
                                }
                                InverseDCT(F_F_in, fcolor_src_0);
                            }
                            block_sat(fcolor_src, inSub_i_XCb_src);
                            block_sat(fcolor_src_0, inSub_i_XCr_src);

							pos_o_pos_o[0] = pos_fin_vlc_pos[0];
						}
                        Increment_I_pour_sdl(outSub_i_out_VOLsi_0, outSub_i_MB_pos_i__0, inSub_i_pos_X1_pos_0, inSub_i_pos_X2_pos_0, inSub_i_pos_X3_pos_0, inSub_i_pos_X4_pos_0, inSub_i_pos_XCb_po_0, inSub_i_pos_XCr_po_0,tab_pos_X);
                    }
				}
				width = VOLsimple_in -> video_object_layer_width ;
                SDX_Stock_blockLum_in_pict(99/*NB_MB*/, width + 2 * EDGE_SIZE/*720*//*xpic*/, pos_X1_pos_X1, pos_X2_pos_X2, pos_X3_pos_X3, pos_X4_pos_X4, X1_src_X1, X2_src_X2, X3_src_X3, X4_src_X4, display_display[0]);
				width = VOLsimple_in -> video_object_layer_width >> 1 ;
                SDX_Stock_block_in_pict(99/*NB_MB*/, width + 2 * edge_size2/*720*//*xpic*/, pos_XCb_pos_X, XCb_src,display_display[4]);
                SDX_Stock_block_in_pict(99/*NB_MB*/, width + 2 * edge_size2/*720*//*xpic*/, pos_XCr_pos_X, XCr_src,display_display[5]);

				frame_U_o_in = init_out_frame_U_i;
				frame_V_o_in = init_out_frame_V_i;
				frame_Y_o_in = init_out_frame_Y_i;
			}
            New_buffer_preesm(414720/*nb_octet_to_read*/, pos_fin_vlc_pos, new_buff_o_in, pos_octet_end_in);
            {//new_buff_o
				memcpy(out_end_in_4, new_buff_o_in, sizeof(int));
				memcpy(out_end_in_3, new_buff_o_in, sizeof(int));
            }
            {//frame_U
                out_RefU_i = &frame_U_o_in[0];
				memcpy(out_end_in_0, frame_U_o_in, 342912*sizeof(uchar));
            }
            {//frame_V
                out_RefV_i = &frame_V_o_in[0];
				memcpy(out_end_in_1, frame_V_o_in, 342912*sizeof(uchar));
            }
            {//frame_Y
                out_RefY_i = &frame_Y_o_in[0];
				memcpy(out_end_in_2, frame_Y_o_in, 1371648*sizeof(uchar));
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

        return 0;
    }//computationThread
