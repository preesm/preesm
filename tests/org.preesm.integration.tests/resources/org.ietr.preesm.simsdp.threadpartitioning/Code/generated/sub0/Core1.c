/**
 * @file Core1.c
 * @generated by CPrinter
 * @date Thu Jul 18 12:03:35 CEST 2024
 *
 * Code generated for processing element Core1 (ID=1).
 */
#include "preesm_gen0.h"// Core Global Declaration
#include "sub0.h"
extern pthread_barrier_t iter_barrier0;
extern int initNode0;

extern char *const Data_Acquisition_0__Brd_Acq_Im_0__0; // Data_Acquisition_0 > Brd_Acq_Im_0 size:= 3276800*char defined in Core0
extern double *const out_0_0_2_3__MAD_Computation_raw_data_im_i__0; // Brd_Acq_Im_0_out_0_0_2_3 > srv_0_0_MAD_Computation_raw_data_im_i size:= 59392*double defined in Core0
extern double *const out_0_1_2_3__MAD_Computation_raw_data_im_i__0; // Brd_Acq_Im_0_out_0_1_2_3 > srv_0_1_MAD_Computation_raw_data_im_i size:= 59392*double defined in Core0
extern double *const out_0_2_2_3__MAD_Computation_raw_data_im_i__0; // Brd_Acq_Im_0_out_0_2_2_3 > srv_0_2_MAD_Computation_raw_data_im_i size:= 59392*double defined in Core0
extern double *const out_1_1_3__in__1;  // snk_out_6 size:= 231424*double defined in Core0
extern double *const out_1_1__in__0;  // snk_out_0 size:= 409600*double defined in Core0
extern double *const out_2_2__raw_data_im_i__0; // Brd_Acq_Im_0_out_2_2 > Plot_RnI_Histo_0_raw_data_im_i size:= 409600*double defined in Core0
extern double *const out_3_3__in__1;  // snk_out_1 size:= 409600*double defined in Core0
extern double *const out_4_4__in__1;  // snk_out_2 size:= 409600*double defined in Core0
extern double *const raw_data_im_o__in__0; // Data_Acquisition_0_raw_data_im_o > Brd_Acq_Im_0_in size:= 409600*double defined in Core0
extern char *const Brd_Acq_Im_0__snk_out_0_0__0;  // Brd_Acq_Im_0 > snk_out_0_0 size:= 3276800*char defined in Core0
extern char *const Brd_Acq_Im_0__Plot_RnI_Histo_0__0; // Brd_Acq_Im_0 > Plot_RnI_Histo_0 size:= 3276800*char defined in Core0
extern char *const Brd_Acq_Im_0__snk_out_1_0__0;  // Brd_Acq_Im_0 > snk_out_1_0 size:= 3276800*char defined in Core0
extern char *const Brd_Acq_Im_0__snk_out_2_0__0;  // Brd_Acq_Im_0 > snk_out_2_0 size:= 3276800*char defined in Core0
extern char *const Brd_Acq_Im_0__snk_out_6_0__0;  // Brd_Acq_Im_0 > snk_out_6_0 size:= 1851392*char defined in Core0
extern char *const Brd_Acq_Im_0__srv_0_1__0;  // Brd_Acq_Im_0 > srv_0_1 size:= 475136*char defined in Core0
extern char *const Brd_Acq_Real_0__srv_0_0__0;  // Brd_Acq_Real_0 > srv_0_0 size:= 475136*char defined in Core0
extern double *const out_0_0_2_3__MAD_Computation_raw_data_real_i__0; // Brd_Acq_Real_0_out_0_0_2_3 > srv_0_0_MAD_Computation_raw_data_real_i size:= 59392*double defined in Core0
extern double *const MAD_Computation_mad_R_o__in_0__0; // srv_0_0_MAD_Computation_mad_R_o > implode_snk_out_8_0_in_in_0 size:= 59392*double defined in Core0
extern double *const MAD_Computation_mad_I_o__in_0__0; // srv_0_0_MAD_Computation_mad_I_o > implode_snk_out_9_0_in_in_0 size:= 59392*double defined in Core0
extern char *const srv_0_0__implode_snk_out_9_0_in__0; // srv_0_0 > implode_snk_out_9_0_in size:= 475136*char defined in Core0
extern char *const Brd_Acq_Real_0__srv_0_2__0;  // Brd_Acq_Real_0 > srv_0_2 size:= 475136*char defined in Core0
extern double *const out_0_2_2_3__MAD_Computation_raw_data_real_i__0; // Brd_Acq_Real_0_out_0_2_2_3 > srv_0_2_MAD_Computation_raw_data_real_i size:= 59392*double defined in Core0
extern double *const MAD_Computation_mad_R_o__in_2__0; // srv_0_2_MAD_Computation_mad_R_o > implode_snk_out_8_0_in_in_2 size:= 59392*double defined in Core0
extern double *const MAD_Computation_mad_I_o__in_2__0; // srv_0_2_MAD_Computation_mad_I_o > implode_snk_out_9_0_in_in_2 size:= 59392*double defined in Core0
extern char *const srv_0_2__implode_snk_out_9_0_in__0; // srv_0_2 > implode_snk_out_9_0_in size:= 475136*char defined in Core0
extern char *const srv_0_1__implode_snk_out_8_0_in__0; // srv_0_1 > implode_snk_out_8_0_in size:= 475136*char defined in Core0
extern double *const MAD_Computation_mad_R_o__in_1__0; // srv_0_1_MAD_Computation_mad_R_o > implode_snk_out_8_0_in_in_1 size:= 59392*double defined in Core0
extern double *const MAD_Computation_mad_R_o__in__0;  // snk_out_8 size:= 178176*double defined in Core0

// Core Global Definitions

void* computationThread_Core1(void *arg) {
  ThreadParams0 *params = (ThreadParams0*) arg;
  double *MAD_Computation_mad_R_o__in__0 = params->snk_out_8;
  double *out_1_1__in__0 = params->snk_out_0;
  double *out_3_3__in__1 = params->snk_out_1;
  double *out_4_4__in__1 = params->snk_out_2;
  double *out_2_2__in__0 = params->snk_out_3;
  double *out_3_3__in__0 = params->snk_out_4;
  double *out_4_4__in__0 = params->snk_out_5;
  double *out_1_1_3__in__1 = params->snk_out_6;
  double *out_1_1_3__in__0 = params->snk_out_7;
  double *MAD_Computation_mad_I_o__in__0 = params->snk_out_9;

  if (initNode0 == 1) {
    // Initialisation(s)

    srv_0Init(); // srv_0_0

  }
  // Begin the execution loop
  pthread_barrier_wait(&iter_barrier0);

  // loop body
  receiveStart(); // Core0 > Core1
  receiveEnd(0, 1); // Core0 > Core1
  // Broadcast Brd_Acq_Im_0

  {
    // memcpy #0
    memcpy(out_0_0_2_3__MAD_Computation_raw_data_im_i__0 + 0, raw_data_im_o__in__0 + 0, 475136); // 59392 * double
    // memcpy #0
    memcpy(out_0_1_2_3__MAD_Computation_raw_data_im_i__0 + 0, raw_data_im_o__in__0 + 59392, 475136); // 59392 * double
    // memcpy #0
    memcpy(out_0_2_2_3__MAD_Computation_raw_data_im_i__0 + 0, raw_data_im_o__in__0 + 118784, 475136); // 59392 * double
    // memcpy #0
    memcpy(out_1_1_3__in__1 + 0, raw_data_im_o__in__0 + 178176, 1851392); // 231424 * double
    // memcpy #0
    memcpy(out_1_1__in__0 + 0, raw_data_im_o__in__0 + 0, 3276800); // 409600 * double
    // memcpy #0
    memcpy(out_2_2__raw_data_im_i__0 + 0, raw_data_im_o__in__0 + 0, 3276800); // 409600 * double
    // memcpy #0
    memcpy(out_3_3__in__1 + 0, raw_data_im_o__in__0 + 0, 3276800); // 409600 * double
    // memcpy #0
    memcpy(out_4_4__in__1 + 0, raw_data_im_o__in__0 + 0, 3276800); // 409600 * double
  }

  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  sendStart(1, 0); // Core1 > Core0
  sendEnd(); // Core1 > Core0
  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  receiveStart(); // Core0 > Core1
  receiveEnd(0, 1); // Core0 > Core1
  Cluster_sub0_srv_0(2048/*N_SAMPLES*/, 3/*SIGMA*/, out_0_0_2_3__MAD_Computation_raw_data_real_i__0,
      out_0_0_2_3__MAD_Computation_raw_data_im_i__0, MAD_Computation_mad_R_o__in_0__0,
      MAD_Computation_mad_I_o__in_0__0); // srv_0_0

  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  receiveStart(); // Core0 > Core1
  receiveEnd(0, 1); // Core0 > Core1
  Cluster_sub0_srv_0(2048/*N_SAMPLES*/, 3/*SIGMA*/, out_0_2_2_3__MAD_Computation_raw_data_real_i__0,
      out_0_2_2_3__MAD_Computation_raw_data_im_i__0, MAD_Computation_mad_R_o__in_2__0,
      MAD_Computation_mad_I_o__in_2__0); // srv_0_2

  sendStart(1, 2); // Core1 > Core2
  sendEnd(); // Core1 > Core2
  receiveStart(); // Core2 > Core1
  receiveEnd(2, 1); // Core2 > Core1
  // Join implode_snk_out_8_0_in

  {
    memcpy(MAD_Computation_mad_R_o__in__0 + 0, MAD_Computation_mad_R_o__in_0__0 + 0, 475136); // 59392 * double
    memcpy(MAD_Computation_mad_R_o__in__0 + 59392, MAD_Computation_mad_R_o__in_1__0 + 0, 475136); // 59392 * double
    memcpy(MAD_Computation_mad_R_o__in__0 + 118784, MAD_Computation_mad_R_o__in_2__0 + 0, 475136); // 59392 * double
  }
  snk_out_8(MAD_Computation_mad_R_o__in__0); // snk_out_8_0

  // loop footer
  pthread_barrier_wait(&iter_barrier0);

  return NULL;
}

