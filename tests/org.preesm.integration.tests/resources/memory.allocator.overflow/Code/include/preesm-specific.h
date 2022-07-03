#ifndef PREESM_SPECIFIC_H
#define PREESM_SPECIFIC_H

#define IN
#define OUT


// can't be reomved since it is used in ezsift-preesm.h
// to statically allocate struct memory -> will be changed once there is preesm-params.h codegen
#define SIFT_nBins 128 //corresponds to DEGREE_OF_DESCRIPTORS in ezsift.h or, SIFT_DESCR_WIDTH * SIFT_DESCR_WIDTH * SIFT_DESCR_HIST_BINS in extract_descriptor.c


#endif
