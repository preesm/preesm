/**
 * @file Core1.c
 * @generated by CPrinter
 * @date Mon Nov 06 09:07:08 CET 2023
 *
 * Code generated for processing element Core1 (ID=1).
 */

#include "preesm_gen.h"
// Core Global Declaration
extern pthread_barrier_t iter_barrier;
extern int preesmStopThreads;

#ifdef PREESM_MD5_UPDATE
extern struct rk_sema preesmPrintSema;
#endif
extern double *const out__in_0__0;  // src_in_2_0_out > urc_0_0_in_0 size:= 178176*double defined in Core0
extern double *const out__in_0__1;  // src_in_3_0_out > urc_1_0_in_0 size:= 178176*double defined in Core0
extern double *const out_0__in_1__0; // urc_5_0_out_0 > explode_urc_5_0_out_0_in_1 size:= 405504*double defined in Core0
extern double *const out_1__raw_data_im_i__1; // urc_5_0_out_1 > explode_urc_5_0_out_1_raw_data_im_i size:= 4096*double defined in Core0
extern char *const urc_5_0__explode_urc_5_0_out_1__0; // urc_5_0 > explode_urc_5_0_out_1 size:= 32768*char defined in Core0
extern char *const urc_2_0__explode_urc_2_0_out_1__0; // urc_2_0 > explode_urc_2_0_out_1 size:= 32768*char defined in Core0
extern double *const out_1_0__raw_data_real_i__0; // explode_urc_2_0_out_1_out_1_0 > MAD_Computation2_0_raw_data_real_i size:= 2048*double defined in Core0
extern double *const out_1_1__raw_data_real_i__0; // explode_urc_2_0_out_1_out_1_1 > MAD_Computation2_1_raw_data_real_i size:= 2048*double defined in Core0
extern double *const out_1__raw_data_real_i__0; // urc_2_0_out_1 > explode_urc_2_0_out_1_raw_data_real_i size:= 4096*double defined in Core0
extern char *const urc_3_0__explode_urc_3_0_out_1__0; // urc_3_0 > explode_urc_3_0_out_1 size:= 32768*char defined in Core0
extern double *const out_1_0__raw_data_im_i__0; // explode_urc_3_0_out_1_out_1_0 > MAD_Computation2_0_raw_data_im_i size:= 2048*double defined in Core0
extern double *const out_1_1__raw_data_im_i__0; // explode_urc_3_0_out_1_out_1_1 > MAD_Computation2_1_raw_data_im_i size:= 2048*double defined in Core0
extern double *const out_1__raw_data_im_i__0; // urc_3_0_out_1 > explode_urc_3_0_out_1_raw_data_im_i size:= 4096*double defined in Core0
extern double *const out_0_0__in_1__0; // explode_urc_5_0_out_0_out_0_0 > srv_17_0_in_1 size:= 135168*double defined in Core0
extern double *const out_0_1__in_1__0; // explode_urc_5_0_out_0_out_0_1 > srv_17_1_in_1 size:= 135168*double defined in Core0
extern double *const out_0_2__in_1__0; // explode_urc_5_0_out_0_out_0_2 > srv_17_2_in_1 size:= 135168*double defined in Core0
extern char *const explode_urc_5_0_out_0__srv_17_0__0; // explode_urc_5_0_out_0 > srv_17_0 size:= 1081344*char defined in Core0
extern char *const explode_urc_5_0_out_0__srv_17_2__0; // explode_urc_5_0_out_0 > srv_17_2 size:= 1081344*char defined in Core0
extern double *const mad_R_o__in_1_0_3__0; // MAD_Computation2_0_mad_R_o > Join_eu_MAD_Computation0_0_in_1_0_3 size:= 2048*double defined in Core0
extern double *const mad_I_o__in_1_0_3__0; // MAD_Computation2_0_mad_I_o > Join_eu_MAD_Computation1_0_in_1_0_3 size:= 2048*double defined in Core0
extern char *const MAD_Computation2_0__Join_eu_MAD_Computation1_0__0; // MAD_Computation2_0 > Join_eu_MAD_Computation1_0 size:= 16384*char defined in Core0
extern double *const mad_R_o__in_1_1_3__0; // MAD_Computation2_1_mad_R_o > Join_eu_MAD_Computation0_0_in_1_1_3 size:= 2048*double defined in Core0
extern double *const mad_I_o__in_1_1_3__0; // MAD_Computation2_1_mad_I_o > Join_eu_MAD_Computation1_0_in_1_1_3 size:= 2048*double defined in Core0
extern char *const MAD_Computation2_1__Join_eu_MAD_Computation1_0__0; // MAD_Computation2_1 > Join_eu_MAD_Computation1_0 size:= 16384*char defined in Core0
extern char *const explode_urc_2_0_out_0__srv_14_0__0; // explode_urc_2_0_out_0 > srv_14_0 size:= 606208*char defined in Core0
extern char *const explode_urc_3_0_out_0__srv_14_0__0; // explode_urc_3_0_out_0 > srv_14_0 size:= 606208*char defined in Core0
extern char *const STD_Computation2_0__implode_urc_11_0_in_1__0; // STD_Computation2_0 > implode_urc_11_0_in_1 size:= 16384*char defined in Core0
extern char *const STD_Computation2_1__implode_urc_11_0_in_1__0; // STD_Computation2_1 > implode_urc_11_0_in_1 size:= 16384*char defined in Core0
extern double *const std_I_o__in_1_0__0; // STD_Computation2_0_std_I_o > implode_urc_11_0_in_1_in_1_0 size:= 2048*double defined in Core0
extern double *const std_I_o__in_1_1__0; // STD_Computation2_1_std_I_o > implode_urc_11_0_in_1_in_1_1 size:= 2048*double defined in Core0
extern double *const std_I_o__in_1__0; // implode_urc_11_0_in_1_std_I_o > urc_11_0_in_1 size:= 4096*double defined in Core0
extern double *const out_0_0__in_0__5; // explode_urc_2_0_out_0_out_0_0 > srv_14_0_in_0 size:= 75776*double defined in Core0
extern double *const out_0_0__in_1__1; // explode_urc_3_0_out_0_out_0_0 > srv_14_0_in_1 size:= 75776*double defined in Core0
extern double *const out_0__in_0_0_2__4; // srv_14_0_out_0 > Join_eu_MAD_Computation0_0_in_0_0_2 size:= 75776*double defined in Core0
extern double *const out_1__in_0_0_2__0; // srv_14_0_out_1 > Join_eu_MAD_Computation1_0_in_0_0_2 size:= 75776*double defined in Core0
extern char *const srv_14_0__Join_eu_MAD_Computation1_0__0; // srv_14_0 > Join_eu_MAD_Computation1_0 size:= 606208*char defined in Core0
extern char *const explode_urc_4_0_out_0__srv_17_1__0; // explode_urc_4_0_out_0 > srv_17_1 size:= 1081344*char defined in Core0
extern double *const out_0_1__in_0__0; // explode_urc_4_0_out_0_out_0_1 > srv_17_1_in_0 size:= 135168*double defined in Core0
extern double *const out_0__in_0_1__0; // srv_17_1_out_0 > implode_urc_10_0_in_0_in_0_1 size:= 135168*double defined in Core0
extern double *const out_1__in_0_1__0; // srv_17_1_out_1 > implode_urc_11_0_in_0_in_0_1 size:= 135168*double defined in Core0
extern char *const srv_17_1__implode_urc_10_0_in_0__0; // srv_17_1 > implode_urc_10_0_in_0 size:= 1081344*char defined in Core0
extern char *const srv_14_1__Join_eu_MAD_Computation0_0__0; // srv_14_1 > Join_eu_MAD_Computation0_0 size:= 606208*char defined in Core0
extern char *const srv_14_2__Join_eu_MAD_Computation0_0__0; // srv_14_2 > Join_eu_MAD_Computation0_0 size:= 606208*char defined in Core0
extern double *const out_0__in_0_1_2__4; // srv_14_1_out_0 > Join_eu_MAD_Computation0_0_in_0_1_2 size:= 75776*double defined in Core0
extern double *const out_0__in_0_2_2__4; // srv_14_2_out_0 > Join_eu_MAD_Computation0_0_in_0_2_2 size:= 75776*double defined in Core0
extern double *const out__in_1__0; // Join_eu_MAD_Computation0_0_out > urc_1_0_in_1 size:= 231424*double defined in Core0
extern char *const srv_17_2__implode_urc_11_0_in_0__0; // srv_17_2 > implode_urc_11_0_in_0 size:= 1081344*char defined in Core0
extern char *const Join_eu_MAD_Computation1_0__urc_0_0__0; // Join_eu_MAD_Computation1_0 > urc_0_0 size:= 1851392*char defined in Core0
extern double *const out__in_1__1; // Join_eu_MAD_Computation1_0_out > urc_0_0_in_1 size:= 231424*double defined in Core0
extern double *const out_0__in_0__5; // urc_0_0_out_0 > explode_urc_0_0_out_0_in_0 size:= 405504*double defined in Core0
extern double *const out_1__in__3;  // urc_0_0_out_1 > explode_urc_0_0_out_1_in size:= 4096*double defined in Core0
extern char *const urc_0_0__explode_urc_0_0_out_0__0; // urc_0_0 > explode_urc_0_0_out_0 size:= 3244032*char defined in Core0
extern double *const out_0__in_0__2; // urc_1_0_out_0 > explode_urc_1_0_out_0_in_0 size:= 405504*double defined in Core0
extern double *const out_1__in__0;  // urc_1_0_out_1 > explode_urc_1_0_out_1_in size:= 4096*double defined in Core0
extern char *const urc_1_0__explode_urc_1_0_out_0__0; // urc_1_0 > explode_urc_1_0_out_0 size:= 3244032*char defined in Core0
extern double *const out_1_0__in__3; // explode_urc_0_0_out_1_out_1_0 > Brd_MAD_I2_0_in size:= 2048*double defined in Core0
extern double *const out_1_1__in__3; // explode_urc_0_0_out_1_out_1_1 > Brd_MAD_I2_1_in size:= 2048*double defined in Core0
extern double *const out_1_0__in__0; // explode_urc_1_0_out_1_out_1_0 > Brd_MAD_R2_0_in size:= 2048*double defined in Core0
extern double *const out_1_1__in__0; // explode_urc_1_0_out_1_out_1_1 > Brd_MAD_R2_1_in size:= 2048*double defined in Core0
extern double *const out_0_0__in_1_0_3__2; // Brd_MAD_I2_0_out_0_0 > Join_eu_Brd_MAD_I0_0_in_1_0_3 size:= 2048*double defined in Core0
extern double *const out_1_1__in_1_0__1; // Brd_MAD_I2_0_out_1_1 > implode_urc_6_0_in_1_in_1_0 size:= 2048*double defined in Core0
extern char *const Brd_MAD_I2_0__Join_eu_Brd_MAD_I0_0__0; // Brd_MAD_I2_0 > Join_eu_Brd_MAD_I0_0 size:= 16384*char defined in Core0
extern char *const Brd_MAD_I2_0__implode_urc_6_0_in_1__0; // Brd_MAD_I2_0 > implode_urc_6_0_in_1 size:= 16384*char defined in Core0
extern double *const out_0_0__in_1_1_3__2; // Brd_MAD_I2_1_out_0_0 > Join_eu_Brd_MAD_I0_0_in_1_1_3 size:= 2048*double defined in Core0
extern double *const out_1_1__in_1_1__1; // Brd_MAD_I2_1_out_1_1 > implode_urc_6_0_in_1_in_1_1 size:= 2048*double defined in Core0
extern char *const Brd_MAD_I2_1__Join_eu_Brd_MAD_I0_0__0; // Brd_MAD_I2_1 > Join_eu_Brd_MAD_I0_0 size:= 16384*char defined in Core0
extern char *const Brd_MAD_I2_1__implode_urc_6_0_in_1__0; // Brd_MAD_I2_1 > implode_urc_6_0_in_1 size:= 16384*char defined in Core0
extern double *const out_0_0__in_1_0_3__0; // Brd_MAD_R2_0_out_0_0 > Join_eu_Brd_MAD_R0_0_in_1_0_3 size:= 2048*double defined in Core0
extern double *const out_2_1__in_1_0__0; // Brd_MAD_R2_0_out_2_1 > implode_urc_9_0_in_1_in_1_0 size:= 2048*double defined in Core0
extern char *const Brd_MAD_R2_0__implode_urc_9_0_in_1__0; // Brd_MAD_R2_0 > implode_urc_9_0_in_1 size:= 16384*char defined in Core0
extern char *const Brd_MAD_R2_0__Join_eu_Brd_MAD_R0_0__0; // Brd_MAD_R2_0 > Join_eu_Brd_MAD_R0_0 size:= 16384*char defined in Core0
extern double *const out_0_0__in_1_1_3__0; // Brd_MAD_R2_1_out_0_0 > Join_eu_Brd_MAD_R0_0_in_1_1_3 size:= 2048*double defined in Core0
extern double *const out_2_1__in_1_1__0; // Brd_MAD_R2_1_out_2_1 > implode_urc_9_0_in_1_in_1_1 size:= 2048*double defined in Core0
extern char *const Brd_MAD_R2_1__implode_urc_9_0_in_1__0; // Brd_MAD_R2_1 > implode_urc_9_0_in_1 size:= 16384*char defined in Core0
extern char *const Brd_MAD_R2_1__Join_eu_Brd_MAD_R0_0__0; // Brd_MAD_R2_1 > Join_eu_Brd_MAD_R0_0 size:= 16384*char defined in Core0
extern char *const srv_12_0__implode_urc_6_0_in_0__0; // srv_12_0 > implode_urc_6_0_in_0 size:= 1081344*char defined in Core0
extern char *const srv_12_1__implode_urc_6_0_in_0__0; // srv_12_1 > implode_urc_6_0_in_0 size:= 1081344*char defined in Core0
extern char *const srv_12_2__implode_urc_6_0_in_0__0; // srv_12_2 > implode_urc_6_0_in_0 size:= 1081344*char defined in Core0
extern double *const out_1__in_0_0__3; // srv_12_0_out_1 > implode_urc_6_0_in_0_in_0_0 size:= 135168*double defined in Core0
extern double *const out_1__in_0_1__3; // srv_12_1_out_1 > implode_urc_6_0_in_0_in_0_1 size:= 135168*double defined in Core0
extern double *const out_1__in_0_2__4; // srv_12_2_out_1 > implode_urc_6_0_in_0_in_0_2 size:= 135168*double defined in Core0
extern double *const out_1__in_0__3;  // implode_urc_6_0_in_0_out_1 > urc_6_0_in_0 size:= 405504*double defined in Core0
extern char *const implode_urc_6_0_in_0__urc_6_0__0; // implode_urc_6_0_in_0 > urc_6_0 size:= 3244032*char defined in Core0
extern char *const srv_17_0__implode_urc_11_0_in_0__0; // srv_17_0 > implode_urc_11_0_in_0 size:= 1081344*char defined in Core0
extern double *const out_1__in_0_0__0; // srv_17_0_out_1 > implode_urc_11_0_in_0_in_0_0 size:= 135168*double defined in Core0
extern double *const out_1__in_0_2__0; // srv_17_2_out_1 > implode_urc_11_0_in_0_in_0_2 size:= 135168*double defined in Core0
extern double *const out_1__in_0__0; // implode_urc_11_0_in_0_out_1 > urc_11_0_in_0 size:= 405504*double defined in Core0
extern double *const out_0__in_0__4; // urc_11_0_out_0 > explode_urc_11_0_out_0_in_0 size:= 405504*double defined in Core0
extern double *const out_1__in__2;  // urc_11_0_out_1 > explode_urc_11_0_out_1_in size:= 4096*double defined in Core0
extern double *const out_0_0__in_0__1; // explode_urc_11_0_out_0_out_0_0 > srv_13_0_in_0 size:= 135168*double defined in Core0
extern double *const out_0_1__in_0__1; // explode_urc_11_0_out_0_out_0_1 > srv_13_1_in_0 size:= 135168*double defined in Core0
extern double *const out_0_2__in_0__1; // explode_urc_11_0_out_0_out_0_2 > srv_13_2_in_0 size:= 135168*double defined in Core0
extern double *const out_1_0__in__2; // explode_urc_11_0_out_1_out_1_0 > Brd_STD_I2_0_in size:= 2048*double defined in Core0
extern double *const out_1_1__in__2; // explode_urc_11_0_out_1_out_1_1 > Brd_STD_I2_1_in size:= 2048*double defined in Core0
extern double *const out_0_0__in_1_0_3__1; // Brd_STD_I2_0_out_0_0 > Join_eu_Brd_STD_I0_0_in_1_0_3 size:= 2048*double defined in Core0
extern double *const out_1_1__in_1_0__0; // Brd_STD_I2_0_out_1_1 > implode_urc_7_0_in_1_in_1_0 size:= 2048*double defined in Core0
extern char *const Brd_STD_I2_0__implode_urc_7_0_in_1__0; // Brd_STD_I2_0 > implode_urc_7_0_in_1 size:= 16384*char defined in Core0
extern double *const out_0_0__in_1_1_3__1; // Brd_STD_I2_1_out_0_0 > Join_eu_Brd_STD_I0_0_in_1_1_3 size:= 2048*double defined in Core0
extern double *const out_1_1__in_1_1__0; // Brd_STD_I2_1_out_1_1 > implode_urc_7_0_in_1_in_1_1 size:= 2048*double defined in Core0
extern char *const Brd_STD_I2_1__implode_urc_7_0_in_1__0; // Brd_STD_I2_1 > implode_urc_7_0_in_1 size:= 16384*char defined in Core0
extern char *const explode_urc_10_0_out_1__Brd_STD_R2_1__0; // explode_urc_10_0_out_1 > Brd_STD_R2_1 size:= 16384*char defined in Core0
extern double *const out_1_0__in_1_1_3__0; // Brd_STD_R2_1_out_1_0 > Join_eu_Brd_STD_R0_0_in_1_1_3 size:= 2048*double defined in Core0
extern double *const out_2_1__in_1_1__1; // Brd_STD_R2_1_out_2_1 > implode_urc_8_0_in_1_in_1_1 size:= 2048*double defined in Core0
extern double *const out_1_1__in__1; // explode_urc_10_0_out_1_out_1_1 > Brd_STD_R2_1_in size:= 2048*double defined in Core0
extern char *const Brd_STD_R2_1__Join_eu_Brd_STD_R0_0__0; // Brd_STD_R2_1 > Join_eu_Brd_STD_R0_0 size:= 16384*char defined in Core0
extern char *const Brd_STD_R2_1__implode_urc_8_0_in_1__0; // Brd_STD_R2_1 > implode_urc_8_0_in_1 size:= 16384*char defined in Core0
extern double *const out_0__in_0_0_2__0; // srv_13_0_out_0 > Join_eu_Brd_STD_I0_0_in_0_0_2 size:= 135168*double defined in Core0
extern double *const out_1__in_0_0__1; // srv_13_0_out_1 > implode_urc_7_0_in_0_in_0_0 size:= 135168*double defined in Core0
extern double *const out_0__in_0_1_2__0; // srv_13_1_out_0 > Join_eu_Brd_STD_I0_0_in_0_1_2 size:= 135168*double defined in Core0
extern double *const out_1__in_0_1__1; // srv_13_1_out_1 > implode_urc_7_0_in_0_in_0_1 size:= 135168*double defined in Core0
extern double *const out_0__in_0_2_2__0; // srv_13_2_out_0 > Join_eu_Brd_STD_I0_0_in_0_2_2 size:= 135168*double defined in Core0
extern double *const out_1__in_0_2__1; // srv_13_2_out_1 > implode_urc_7_0_in_0_in_0_2 size:= 135168*double defined in Core0
extern char *const explode_urc_10_0_out_0__srv_15_1__0; // explode_urc_10_0_out_0 > srv_15_1 size:= 1081344*char defined in Core0
extern double *const out_0_1__in_0__3; // explode_urc_10_0_out_0_out_0_1 > srv_15_1_in_0 size:= 135168*double defined in Core0
extern double *const out_0__in_0_1_2__3; // srv_15_1_out_0 > Join_eu_Brd_STD_R0_0_in_0_1_2 size:= 135168*double defined in Core0
extern double *const out_1__in_0_1__4; // srv_15_1_out_1 > implode_urc_8_0_in_0_in_0_1 size:= 135168*double defined in Core0
extern char *const srv_15_1__Join_eu_Brd_STD_R0_0__0; // srv_15_1 > Join_eu_Brd_STD_R0_0 size:= 1081344*char defined in Core0
extern char *const srv_15_1__implode_urc_8_0_in_0__0; // srv_15_1 > implode_urc_8_0_in_0 size:= 1081344*char defined in Core0
extern double *const out__std_I_i__0; // Join_eu_Brd_STD_I0_0_out > Plot_Threshold_0_std_I_i size:= 409600*double defined in Core0
extern char *const Join_eu_Brd_STD_I0_0__Plot_Threshold_0__0; // Join_eu_Brd_STD_I0_0 > Plot_Threshold_0 size:= 3276800*char defined in Core0
extern double *const out_1__in_0__1;  // implode_urc_7_0_in_0_out_1 > urc_7_0_in_0 size:= 405504*double defined in Core0
extern char *const implode_urc_7_0_in_1__urc_7_0__0; // implode_urc_7_0_in_1 > urc_7_0 size:= 32768*char defined in Core0
extern double *const out_1__in_1__0;  // implode_urc_7_0_in_1_out_1 > urc_7_0_in_1 size:= 4096*double defined in Core0

// Core Global Definitions

void* computationThread_Core1(void *arg) {
  if (arg != NULL) {
    printf("Warning: expecting NULL arguments\n");
    fflush (stdout);
  }

#ifdef PREESM_MD5_UPDATE
	PREESM_MD5_CTX preesm_md5_ctx_out_1__in_1__0;
	PREESM_MD5_Init(&preesm_md5_ctx_out_1__in_1__0);
	PREESM_MD5_CTX preesm_md5_ctx_out_1__in_0__1;
	PREESM_MD5_Init(&preesm_md5_ctx_out_1__in_0__1);
#endif
  // Initialisation(s)

  // Begin the execution loop
  pthread_barrier_wait(&iter_barrier);
#ifdef PREESM_LOOP_SIZE // Case of a finite loop
	int index;
	for(index=0;index<PREESM_LOOP_SIZE && !preesmStopThreads;index++){
#else // Default case of an infinite loop
  while (!preesmStopThreads) {
#endif
    // loop body
    src_in_2(out__in_0__0); // src_in_2_0

    src_in_3(out__in_0__1); // src_in_3_0

    urc_5(out_0__in_1__0, out_1__raw_data_im_i__1); // urc_5_0

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    // Fork explode_urc_2_0_out_1

    {
      memcpy(out_1_0__raw_data_real_i__0 + 0, out_1__raw_data_real_i__0 + 0, 16384); // 2048 * double
      memcpy(out_1_1__raw_data_real_i__0 + 0, out_1__raw_data_real_i__0 + 2048, 16384); // 2048 * double
    }
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    // Fork explode_urc_3_0_out_1

    {
      memcpy(out_1_0__raw_data_im_i__0 + 0, out_1__raw_data_im_i__0 + 0, 16384); // 2048 * double
      memcpy(out_1_1__raw_data_im_i__0 + 0, out_1__raw_data_im_i__0 + 2048, 16384); // 2048 * double
    }
    // Fork explode_urc_5_0_out_0

    {
      memcpy(out_0_0__in_1__0 + 0, out_0__in_1__0 + 0, 1081344); // 135168 * double
      memcpy(out_0_1__in_1__0 + 0, out_0__in_1__0 + 135168, 1081344); // 135168 * double
      memcpy(out_0_2__in_1__0 + 0, out_0__in_1__0 + 270336, 1081344); // 135168 * double
    }
    sendStart(1, 0); // Core1 > Core0
    sendEnd(); // Core1 > Core0
    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    MADCpt(2048/*N_SAMPLES*/, 3/*SIGMA*/, out_1_0__raw_data_real_i__0, out_1_0__raw_data_im_i__0, mad_R_o__in_1_0_3__0,
        mad_I_o__in_1_0_3__0); // MAD_Computation2_0

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    MADCpt(2048/*N_SAMPLES*/, 3/*SIGMA*/, out_1_1__raw_data_real_i__0, out_1_1__raw_data_im_i__0, mad_R_o__in_1_1_3__0,
        mad_I_o__in_1_1_3__0); // MAD_Computation2_1

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    // Join implode_urc_11_0_in_1

    {
      memcpy(std_I_o__in_1__0 + 0, std_I_o__in_1_0__0 + 0, 16384); // 2048 * double
      memcpy(std_I_o__in_1__0 + 2048, std_I_o__in_1_1__0 + 0, 16384); // 2048 * double
    }
    srv_14(2048/*cfg_0*/, 3/*cfg_1*/, out_0_0__in_0__5, out_0_0__in_1__1, out_0__in_0_0_2__4, out_1__in_0_0_2__0); // srv_14_0

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    receiveStart(); // Core0 > Core1
    receiveEnd(0, 1); // Core0 > Core1
    srv_17(2048/*cfg_0*/, 3/*cfg_1*/, out_0_1__in_0__0, out_0_1__in_1__0, out_0__in_0_1__0, out_1__in_0_1__0); // srv_17_1

    sendStart(1, 0); // Core1 > Core0
    sendEnd(); // Core1 > Core0
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core0 > Core1
    receiveEnd(0, 1); // Core0 > Core1
    // Join Join_eu_MAD_Computation0_0

    {
      memcpy(out__in_1__0 + 0, out_0__in_0_0_2__4 + 0, 606208); // 75776 * double
      memcpy(out__in_1__0 + 75776, out_0__in_0_1_2__4 + 0, 606208); // 75776 * double
      memcpy(out__in_1__0 + 151552, out_0__in_0_2_2__4 + 0, 606208); // 75776 * double
      memcpy(out__in_1__0 + 227328, mad_R_o__in_1_0_3__0 + 0, 16384); // 2048 * double
      memcpy(out__in_1__0 + 229376, mad_R_o__in_1_1_3__0 + 0, 16384); // 2048 * double
    }
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    urc_0(out__in_0__0, out__in_1__1, out_0__in_0__5, out_1__in__3); // urc_0_0

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    urc_1(out__in_0__1, out__in_1__0, out_0__in_0__2, out_1__in__0); // urc_1_0

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    // Fork explode_urc_0_0_out_1

    {
      memcpy(out_1_0__in__3 + 0, out_1__in__3 + 0, 16384); // 2048 * double
      memcpy(out_1_1__in__3 + 0, out_1__in__3 + 2048, 16384); // 2048 * double
    }
    // Fork explode_urc_1_0_out_1

    {
      memcpy(out_1_0__in__0 + 0, out_1__in__0 + 0, 16384); // 2048 * double
      memcpy(out_1_1__in__0 + 0, out_1__in__0 + 2048, 16384); // 2048 * double
    }
    // Broadcast Brd_MAD_I2_0

    {
      // memcpy #0
      memcpy(out_0_0__in_1_0_3__2 + 0, out_1_0__in__3 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_1_1__in_1_0__1 + 0, out_1_0__in__3 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    // Broadcast Brd_MAD_I2_1

    {
      // memcpy #0
      memcpy(out_0_0__in_1_1_3__2 + 0, out_1_1__in__3 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_1_1__in_1_1__1 + 0, out_1_1__in__3 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    // Broadcast Brd_MAD_R2_0

    {
      // memcpy #0
      memcpy(out_0_0__in_1_0_3__0 + 0, out_1_0__in__0 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_2_1__in_1_0__0 + 0, out_1_0__in__0 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    // Broadcast Brd_MAD_R2_1

    {
      // memcpy #0
      memcpy(out_0_0__in_1_1_3__0 + 0, out_1_1__in__0 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_2_1__in_1_1__0 + 0, out_1_1__in__0 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    // Join implode_urc_6_0_in_0

    {
      memcpy(out_1__in_0__3 + 0, out_1__in_0_0__3 + 0, 1081344); // 135168 * double
      memcpy(out_1__in_0__3 + 135168, out_1__in_0_1__3 + 0, 1081344); // 135168 * double
      memcpy(out_1__in_0__3 + 270336, out_1__in_0_2__4 + 0, 1081344); // 135168 * double
    }
    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    receiveStart(); // Core0 > Core1
    receiveEnd(0, 1); // Core0 > Core1
    // Join implode_urc_11_0_in_0

    {
      memcpy(out_1__in_0__0 + 0, out_1__in_0_0__0 + 0, 1081344); // 135168 * double
      memcpy(out_1__in_0__0 + 135168, out_1__in_0_1__0 + 0, 1081344); // 135168 * double
      memcpy(out_1__in_0__0 + 270336, out_1__in_0_2__0 + 0, 1081344); // 135168 * double
    }
    urc_11(out_1__in_0__0, std_I_o__in_1__0, out_0__in_0__4, out_1__in__2); // urc_11_0

    // Fork explode_urc_11_0_out_0

    {
      memcpy(out_0_0__in_0__1 + 0, out_0__in_0__4 + 0, 1081344); // 135168 * double
      memcpy(out_0_1__in_0__1 + 0, out_0__in_0__4 + 135168, 1081344); // 135168 * double
      memcpy(out_0_2__in_0__1 + 0, out_0__in_0__4 + 270336, 1081344); // 135168 * double
    }
    // Fork explode_urc_11_0_out_1

    {
      memcpy(out_1_0__in__2 + 0, out_1__in__2 + 0, 16384); // 2048 * double
      memcpy(out_1_1__in__2 + 0, out_1__in__2 + 2048, 16384); // 2048 * double
    }
    // Broadcast Brd_STD_I2_0

    {
      // memcpy #0
      memcpy(out_0_0__in_1_0_3__1 + 0, out_1_0__in__2 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_1_1__in_1_0__0 + 0, out_1_0__in__2 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    // Broadcast Brd_STD_I2_1

    {
      // memcpy #0
      memcpy(out_0_0__in_1_1_3__1 + 0, out_1_1__in__2 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_1_1__in_1_1__0 + 0, out_1_1__in__2 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    receiveStart(); // Core0 > Core1
    receiveEnd(0, 1); // Core0 > Core1
    // Broadcast Brd_STD_R2_1

    {
      // memcpy #0
      memcpy(out_1_0__in_1_1_3__0 + 0, out_1_1__in__1 + 0, 16384); // 2048 * double
      // memcpy #0
      memcpy(out_2_1__in_1_1__1 + 0, out_1_1__in__1 + 0, 16384); // 2048 * double
    }

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    sendStart(1, 0); // Core1 > Core0
    sendEnd(); // Core1 > Core0
    srv_13(2048/*cfg_0*/, 200/*cfg_1*/, out_0_0__in_0__1, out_0__in_0_0_2__0, out_1__in_0_0__1); // srv_13_0

    srv_13(2048/*cfg_0*/, 200/*cfg_1*/, out_0_1__in_0__1, out_0__in_0_1_2__0, out_1__in_0_1__1); // srv_13_1

    srv_13(2048/*cfg_0*/, 200/*cfg_1*/, out_0_2__in_0__1, out_0__in_0_2_2__0, out_1__in_0_2__1); // srv_13_2

    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    srv_15(2048/*cfg_0*/, 200/*cfg_1*/, out_0_1__in_0__3, out_0__in_0_1_2__3, out_1__in_0_1__4); // srv_15_1

    sendStart(1, 2); // Core1 > Core2
    sendEnd(); // Core1 > Core2
    sendStart(1, 0); // Core1 > Core0
    sendEnd(); // Core1 > Core0
    // Join Join_eu_Brd_STD_I0_0

    {
      memcpy(out__std_I_i__0 + 0, out_0__in_0_0_2__0 + 0, 1081344); // 135168 * double
      memcpy(out__std_I_i__0 + 135168, out_0__in_0_1_2__0 + 0, 1081344); // 135168 * double
      memcpy(out__std_I_i__0 + 270336, out_0__in_0_2_2__0 + 0, 1081344); // 135168 * double
      memcpy(out__std_I_i__0 + 405504, out_0_0__in_1_0_3__1 + 0, 16384); // 2048 * double
      memcpy(out__std_I_i__0 + 407552, out_0_0__in_1_1_3__1 + 0, 16384); // 2048 * double
    }
    sendStart(1, 0); // Core1 > Core0
    sendEnd(); // Core1 > Core0
    // Join implode_urc_7_0_in_0

    {
      memcpy(out_1__in_0__1 + 0, out_1__in_0_0__1 + 0, 1081344); // 135168 * double
      memcpy(out_1__in_0__1 + 135168, out_1__in_0_1__1 + 0, 1081344); // 135168 * double
      memcpy(out_1__in_0__1 + 270336, out_1__in_0_2__1 + 0, 1081344); // 135168 * double
    }
    receiveStart(); // Core2 > Core1
    receiveEnd(2, 1); // Core2 > Core1
    urc_7(out_1__in_0__1, out_1__in_1__0); // urc_7_0
#ifdef PREESM_MD5_UPDATE
		PREESM_MD5_Update(&preesm_md5_ctx_out_1__in_1__0,(char *)out_1__in_1__0, 32768);
		PREESM_MD5_Update(&preesm_md5_ctx_out_1__in_0__1,(char *)out_1__in_0__1, 3244032);
		#endif

    // loop footer
    pthread_barrier_wait(&iter_barrier);

  }

#ifdef PREESM_MD5_UPDATE
	// Print MD5
	rk_sema_wait(&preesmPrintSema);
	unsigned char preesm_md5_chars_final[20] = { 0 };
	PREESM_MD5_Final(preesm_md5_chars_final, &preesm_md5_ctx_out_1__in_1__0);
	printf("preesm_md5_out_1__in_1__0 : ");
	for (int i = 16; i > 0; i -= 1){
		printf("%02x", *(preesm_md5_chars_final + i - 1));
	}
	printf("\n");
	fflush(stdout);
	PREESM_MD5_Final(preesm_md5_chars_final, &preesm_md5_ctx_out_1__in_0__1);
	printf("preesm_md5_out_1__in_0__1 : ");
	for (int i = 16; i > 0; i -= 1){
		printf("%02x", *(preesm_md5_chars_final + i - 1));
	}
	printf("\n");
	fflush(stdout);
	rk_sema_post(&preesmPrintSema);
#endif

  return NULL;
}
