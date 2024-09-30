/**
 * @file Core0.c
 * @generated by CPrinter
 * @date Mon Oct 07 15:30:18 CEST 2024
 *
 * Code generated for processing element Core0 (ID=0).
 */ // Core Global Declaration
#include "sub0.h"

extern pthread_barrier_t iter_barrier0;
extern int initNode0;

#ifdef PREESM_MD5_UPDATE
extern struct rk_sema preesmPrintSema;
#endif

// Core Global Definitions
char Shared[39321600]; //  size:= 39321600*char
char *const Data_Acquisition_0__Brd_Acq_Real_0__0 = (char*) (Shared + 0); // Data_Acquisition_0 > Brd_Acq_Real_0 size:= 3276800*char
char *const Data_Acquisition_0__Brd_Acq_Im_0__0 = (char*) (Shared + 3276800); // Data_Acquisition_0 > Brd_Acq_Im_0 size:= 3276800*char
char *const Brd_Acq_Real_0__Plot_RnI_Histo_0__0 = (char*) (Shared + 6553600); // Brd_Acq_Real_0 > Plot_RnI_Histo_0 size:= 3276800*char
char *const Brd_Acq_Real_0__snk_out_3_0__0 = (char*) (Shared + 9830400); // Brd_Acq_Real_0 > snk_out_3_0 size:= 3276800*char
char *const Brd_Acq_Real_0__snk_out_4_0__0 = (char*) (Shared + 13107200); // Brd_Acq_Real_0 > snk_out_4_0 size:= 3276800*char
char *const Brd_Acq_Real_0__snk_out_5_0__0 = (char*) (Shared + 16384000); // Brd_Acq_Real_0 > snk_out_5_0 size:= 3276800*char
char *const Brd_Acq_Im_0__snk_out_0_0__0 = (char*) (Shared + 19660800); // Brd_Acq_Im_0 > snk_out_0_0 size:= 3276800*char
char *const Brd_Acq_Im_0__Plot_RnI_Histo_0__0 = (char*) (Shared + 22937600); // Brd_Acq_Im_0 > Plot_RnI_Histo_0 size:= 3276800*char
char *const Brd_Acq_Im_0__snk_out_1_0__0 = (char*) (Shared + 26214400); // Brd_Acq_Im_0 > snk_out_1_0 size:= 3276800*char
char *const Brd_Acq_Im_0__snk_out_2_0__0 = (char*) (Shared + 29491200); // Brd_Acq_Im_0 > snk_out_2_0 size:= 3276800*char
char *const Brd_Acq_Real_0__snk_out_7_0__0 = (char*) (Shared + 32768000); // Brd_Acq_Real_0 > snk_out_7_0 size:= 1851392*char
char *const Brd_Acq_Im_0__snk_out_6_0__0 = (char*) (Shared + 34619392); // Brd_Acq_Im_0 > snk_out_6_0 size:= 1851392*char
char *const implode_snk_out_9_0_in__snk_out_9_0__0 = (char*) (Shared + 0); // implode_snk_out_9_0_in > snk_out_9_0 size:= 1425408*char
char *const implode_snk_out_8_0_in__snk_out_8_0__0 = (char*) (Shared + 1425408); // implode_snk_out_8_0_in > snk_out_8_0 size:= 1425408*char
char *const Brd_Acq_Real_0__srv_0_0__0 = (char*) (Shared + 36470784);  // Brd_Acq_Real_0 > srv_0_0 size:= 475136*char
char *const Brd_Acq_Real_0__srv_0_1__0 = (char*) (Shared + 36945920);  // Brd_Acq_Real_0 > srv_0_1 size:= 475136*char
char *const Brd_Acq_Real_0__srv_0_2__0 = (char*) (Shared + 37421056);  // Brd_Acq_Real_0 > srv_0_2 size:= 475136*char
char *const Brd_Acq_Im_0__srv_0_0__0 = (char*) (Shared + 37896192);  // Brd_Acq_Im_0 > srv_0_0 size:= 475136*char
char *const Brd_Acq_Im_0__srv_0_1__0 = (char*) (Shared + 38371328);  // Brd_Acq_Im_0 > srv_0_1 size:= 475136*char
char *const Brd_Acq_Im_0__srv_0_2__0 = (char*) (Shared + 38846464);  // Brd_Acq_Im_0 > srv_0_2 size:= 475136*char
char *const srv_0_0__implode_snk_out_8_0_in__0 = (char*) (Shared + 2850816); // srv_0_0 > implode_snk_out_8_0_in size:= 475136*char
char *const srv_0_0__implode_snk_out_9_0_in__0 = (char*) (Shared + 3325952); // srv_0_0 > implode_snk_out_9_0_in size:= 475136*char
char *const srv_0_2__implode_snk_out_8_0_in__0 = (char*) (Shared + 3801088); // srv_0_2 > implode_snk_out_8_0_in size:= 475136*char
char *const srv_0_2__implode_snk_out_9_0_in__0 = (char*) (Shared + 4276224); // srv_0_2 > implode_snk_out_9_0_in size:= 475136*char
char *const srv_0_1__implode_snk_out_8_0_in__0 = (char*) (Shared + 4751360); // srv_0_1 > implode_snk_out_8_0_in size:= 475136*char
char *const srv_0_1__implode_snk_out_9_0_in__0 = (char*) (Shared + 5226496); // srv_0_1 > implode_snk_out_9_0_in size:= 475136*char
double *const raw_data_real_o__in__0 = (double*) (Shared + 0); // Data_Acquisition_0_raw_data_real_o > Brd_Acq_Real_0_in size:= 409600*double
double *const raw_data_im_o__in__0 = (double*) (Shared + 3276800); // Data_Acquisition_0_raw_data_im_o > Brd_Acq_Im_0_in size:= 409600*double
double *const out_0_0__raw_data_real_i__0 = (double*) (Shared + 6553600); // Brd_Acq_Real_0_out_0_0 > Plot_RnI_Histo_0_raw_data_real_i size:= 409600*double
double *const out_2_2__in__0 = (double*) (Shared + 9830400);  // snk_out_3 size:= 409600*double
double *const out_3_3__in__0 = (double*) (Shared + 13107200);  // snk_out_4 size:= 409600*double
double *const out_4_4__in__0 = (double*) (Shared + 16384000);  // snk_out_5 size:= 409600*double
double *const out_1_1__in__0 = (double*) (Shared + 19660800);  // snk_out size:= 409600*double
double *const out_2_2__raw_data_im_i__0 = (double*) (Shared + 22937600); // Brd_Acq_Im_0_out_2_2 > Plot_RnI_Histo_0_raw_data_im_i size:= 409600*double
double *const out_3_3__in__1 = (double*) (Shared + 26214400);  // snk_out_1 size:= 409600*double
double *const out_4_4__in__1 = (double*) (Shared + 29491200);  // snk_out_2 size:= 409600*double
double *const out_1_1_3__in__0 = (double*) (Shared + 32768000);  // snk_out_7 size:= 231424*double
double *const out_1_1_3__in__1 = (double*) (Shared + 34619392);  // snk_out_6 size:= 231424*double
double *const MAD_Computation_mad_I_o__in__0 = (double*) (Shared + 0);  // snk_out_9 size:= 178176*double
double *const MAD_Computation_mad_R_o__in__0 = (double*) (Shared + 1425408);  // snk_out_8 size:= 178176*double
double *const out_0_0_2_3__MAD_Computation_raw_data_real_i__0 = (double*) (Shared + 36470784); // Brd_Acq_Real_0_out_0_0_2_3 > srv_0_0_MAD_Computation_raw_data_real_i size:= 59392*double
double *const out_0_1_2_3__MAD_Computation_raw_data_real_i__0 = (double*) (Shared + 36945920); // Brd_Acq_Real_0_out_0_1_2_3 > srv_0_1_MAD_Computation_raw_data_real_i size:= 59392*double
double *const out_0_2_2_3__MAD_Computation_raw_data_real_i__0 = (double*) (Shared + 37421056); // Brd_Acq_Real_0_out_0_2_2_3 > srv_0_2_MAD_Computation_raw_data_real_i size:= 59392*double
double *const out_0_0_2_3__MAD_Computation_raw_data_im_i__0 = (double*) (Shared + 37896192); // Brd_Acq_Im_0_out_0_0_2_3 > srv_0_0_MAD_Computation_raw_data_im_i size:= 59392*double
double *const out_0_1_2_3__MAD_Computation_raw_data_im_i__0 = (double*) (Shared + 38371328); // Brd_Acq_Im_0_out_0_1_2_3 > srv_0_1_MAD_Computation_raw_data_im_i size:= 59392*double
double *const out_0_2_2_3__MAD_Computation_raw_data_im_i__0 = (double*) (Shared + 38846464); // Brd_Acq_Im_0_out_0_2_2_3 > srv_0_2_MAD_Computation_raw_data_im_i size:= 59392*double
double *const MAD_Computation_mad_R_o__in_0__0 = (double*) (Shared + 2850816); // srv_0_0_MAD_Computation_mad_R_o > implode_snk_out_8_0_in_in_0 size:= 59392*double
double *const MAD_Computation_mad_I_o__in_0__0 = (double*) (Shared + 3325952); // srv_0_0_MAD_Computation_mad_I_o > implode_snk_out_9_0_in_in_0 size:= 59392*double
double *const MAD_Computation_mad_R_o__in_2__0 = (double*) (Shared + 3801088); // srv_0_2_MAD_Computation_mad_R_o > implode_snk_out_8_0_in_in_2 size:= 59392*double
double *const MAD_Computation_mad_I_o__in_2__0 = (double*) (Shared + 4276224); // srv_0_2_MAD_Computation_mad_I_o > implode_snk_out_9_0_in_in_2 size:= 59392*double
double *const MAD_Computation_mad_R_o__in_1__0 = (double*) (Shared + 4751360); // srv_0_1_MAD_Computation_mad_R_o > implode_snk_out_8_0_in_in_1 size:= 59392*double
double *const MAD_Computation_mad_I_o__in_1__0 = (double*) (Shared + 5226496); // srv_0_1_MAD_Computation_mad_I_o > implode_snk_out_9_0_in_in_1 size:= 59392*double

void* computationThread_Core0(void *arg) {
  ThreadParams0 *params = (ThreadParams0*) arg;
  double *out_1_1__in__0 = params->snk_out;
  double *out_3_3__in__1 = params->snk_out_1;
  double *out_4_4__in__1 = params->snk_out_2;
  double *out_2_2__in__0 = params->snk_out_3;
  double *out_3_3__in__0 = params->snk_out_4;
  double *out_4_4__in__0 = params->snk_out_5;
  double *out_1_1_3__in__1 = params->snk_out_6;
  double *out_1_1_3__in__0 = params->snk_out_7;
  double *MAD_Computation_mad_R_o__in__0 = params->snk_out_8;
  double *MAD_Computation_mad_I_o__in__0 = params->snk_out_9;

#ifdef PREESM_MD5_UPDATE
	PREESM_MD5_CTX preesm_md5_ctx_out_2_2__raw_data_im_i__0;
	PREESM_MD5_Init(&preesm_md5_ctx_out_2_2__raw_data_im_i__0);
	PREESM_MD5_CTX preesm_md5_ctx_out_0_0__raw_data_real_i__0;
	PREESM_MD5_Init(&preesm_md5_ctx_out_0_0__raw_data_real_i__0);
#endif}
  // Begin the execution loop
  pthread_barrier_wait(&iter_barrier0);

  // loop body
  DataAcq(200/*N_BLOCKS*/, 2048/*N_SAMPLES*/, 409600/*SIZE*/, 4096/*HEADER_SIZE*/, raw_data_real_o__in__0,
      raw_data_im_o__in__0); // Data_Acquisition_0

  sendStart(0, 1); // Core0 > Core1
  sendEnd(); // Core0 > Core1
  // Broadcast Brd_Acq_Real_0

  {
    // memcpy #0
    memcpy(out_0_0__raw_data_real_i__0 + 0, raw_data_real_o__in__0 + 0, 3276800); // 409600 * double
    // memcpy #0
    memcpy(out_0_0_2_3__MAD_Computation_raw_data_real_i__0 + 0, raw_data_real_o__in__0 + 0, 475136); // 59392 * double
    // memcpy #0
    memcpy(out_0_1_2_3__MAD_Computation_raw_data_real_i__0 + 0, raw_data_real_o__in__0 + 59392, 475136); // 59392 * double
    // memcpy #0
    memcpy(out_0_2_2_3__MAD_Computation_raw_data_real_i__0 + 0, raw_data_real_o__in__0 + 118784, 475136); // 59392 * double
    // memcpy #0
    memcpy(out_1_1_3__in__0 + 0, raw_data_real_o__in__0 + 178176, 1851392); // 231424 * double
    // memcpy #0
    memcpy(out_2_2__in__0 + 0, raw_data_real_o__in__0 + 0, 3276800); // 409600 * double
    // memcpy #0
    memcpy(out_3_3__in__0 + 0, raw_data_real_o__in__0 + 0, 3276800); // 409600 * double
    // memcpy #0
    memcpy(out_4_4__in__0 + 0, raw_data_real_o__in__0 + 0, 3276800); // 409600 * double
  }

  sendStart(0, 2); // Core0 > Core2
  sendEnd(); // Core0 > Core2
  sendStart(0, 2); // Core0 > Core2
  sendEnd(); // Core0 > Core2
  sendStart(0, 2); // Core0 > Core2
  sendEnd(); // Core0 > Core2
  sendStart(0, 2); // Core0 > Core2
  sendEnd(); // Core0 > Core2
  sendStart(0, 1); // Core0 > Core1
  sendEnd(); // Core0 > Core1
  sendStart(0, 2); // Core0 > Core2
  sendEnd(); // Core0 > Core2
  sendStart(0, 1); // Core0 > Core1
  sendEnd(); // Core0 > Core1
  receiveStart(); // Core1 > Core0
  receiveEnd(1, 0); // Core1 > Core0
  PlotRnIHisto(409600/*SIZE*/, 1/*DISPLAY*/, out_0_0__raw_data_real_i__0, out_2_2__raw_data_im_i__0); // Plot_RnI_Histo_0
#ifdef PREESM_MD5_UPDATE
		PREESM_MD5_Update(&preesm_md5_ctx_out_2_2__raw_data_im_i__0,(char *)out_2_2__raw_data_im_i__0, 3276800);
		PREESM_MD5_Update(&preesm_md5_ctx_out_0_0__raw_data_real_i__0,(char *)out_0_0__raw_data_real_i__0, 3276800);
		#endif

  // loop footer
  pthread_barrier_wait(&iter_barrier0);

#ifdef PREESM_MD5_UPDATE
	// Print MD5
	rk_sema_wait(&preesmPrintSema);
	unsigned char preesm_md5_chars_final[20] = { 0 };
	PREESM_MD5_Final(preesm_md5_chars_final, &preesm_md5_ctx_out_2_2__raw_data_im_i__0);
	printf("preesm_md5_out_2_2__raw_data_im_i__0 : ");
	for (int i = 16; i > 0; i -= 1){
		printf("%02x", *(preesm_md5_chars_final + i - 1));
	}
	printf("\n");
	fflush(stdout);
	PREESM_MD5_Final(preesm_md5_chars_final, &preesm_md5_ctx_out_0_0__raw_data_real_i__0);
	printf("preesm_md5_out_0_0__raw_data_real_i__0 : ");
	for (int i = 16; i > 0; i -= 1){
		printf("%02x", *(preesm_md5_chars_final + i - 1));
	}
	printf("\n");
	fflush(stdout);
	rk_sema_post(&preesmPrintSema);
#endif

  return NULL;
}

