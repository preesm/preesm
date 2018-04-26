#ifndef PREESM_SPECIFIC_H
#define PREESM_SPECIFIC_H

#define IN
#define OUT

// this values are computed for a specific input image size

#define SIFT_IMAGE_W 800 //400
#define SIFT_IMAGE_H 640 //320
#define SIFT_IMAGE_MAX 800 //400 

#define SIFT_IMG_TOT 512000 //128000
#define SIFT_IMG_TOT_RGB 1536000 //384000 check if useful

#define SIFT_pLevels 4 //UPDATE
#define SIFT_nOctaves 7 //6
#define SIFT_nDogLayers 5
#define SIFT_nGpyrLayers 6 //(SIFT_nDogLayers + 1)
#define SIFT_nLayers 3 //(SIFT_nGpyrLayers - 3)

#define SIFT_nBins 128 // corresponds to DEGREE_OF_DESCRIPTORS in ezsift.h
#define SIFT_nHistBins 360

#define SIFT_gWmax 21 // max gaussian blur filter size (= complex formula in preesm ...)

#define SIFT_localKPTmax 750 //UPDATE // max local number of keypoints = SIFT_MAX_KEYPOINTS/parallelismLevels
#define SIFT_localMatchMax 200 //UPDATE // max local number of matches = SIFT_MAX_MATCHES/parallelismLevels

#define SIFT_DUMP_DESCRIPTOR 1

#endif
