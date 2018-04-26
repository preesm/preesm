#include <string.h>
#include <stdio.h>

#include "ezsift-preesm.h"

void filename1(int FilePathLength, OUT char * filename) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  //  strncpy(filename, "/home/ahonorat/Documents/imagesTestsSIFT/bike-3840x2400.pgm", (size_t) FilePathLength);
  strncpy(filename, "../../data/img1.pgm", (size_t) FilePathLength);
  // strncpy(filename, "../../data/down2x1.pgm", (size_t) FilePathLength);
}
