#include <stdio.h>

#include "ezsift-common.h"

void counterOctaveDownN(int nOctavesDownN, OUT int * iter) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  for (int i = 0; i < nOctavesDownN; i++) {
    iter[i] = i;
  }
}

void counterPLevels(int parallelismLevel, OUT int * iter) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  for (int i = 0; i < parallelismLevel; i++) {
    iter[i] = i;
  }
}

void ITERATOR_generic(int parallelismLevel, int nbOctaves, int nbLayers,
		      int image_width, int image_height, int imgDouble,
		      int * start_octave, int * stop_octave,
		      int * start_layer, int * stop_layer,
		      int * start_line,  int * stop_line,
		      int * start_col, int * stop_col) {

  start_octave[0] = 0;
  start_layer[0] = 0;
  start_line[0] = 0;
  start_col[0] = 0;

  int w[nbOctaves];
  w[0] = image_width;
  int h[nbOctaves];
  h[0] = image_height;
  if (imgDouble) {
    w[0] *= 2;
    h[0] *= 2;
  }
  for (int i = 1; i < nbOctaves; i++) {
    w[i] = w[i-1] / 2;
    h[i] = h[i-1] / 2;
  }
  if (parallelismLevel == 1) {
    stop_octave[0] = nbOctaves;
    stop_layer[0] = nbLayers;
    stop_line[0] = h[nbOctaves-1];
    stop_col[0] = w[nbOctaves-1];
    return;
  }

  unsigned long iter_space = 0;

  for (int i = 0; i < nbOctaves; i++) {
    iter_space += w[i]*h[i];
  }
  iter_space *= nbLayers;

  unsigned long piter_space = iter_space/parallelismLevel;

  unsigned long citer_space = 0;
  int nOctaves = nbOctaves;
  int nLayers = nbLayers;
  int nLines = h[0];
  int nCols = w[0];
  
  for (int i = 0; i < nbOctaves; i++) {
    long tmp = citer_space + w[i]*h[i]*nbLayers;
    if (tmp <= piter_space) {
      citer_space = tmp;
    }
    if (tmp > piter_space) {
      nOctaves = i;
      break;
    }
  }
  if (citer_space < piter_space) {
    int size = w[nOctaves]*h[nOctaves];
    unsigned long diff = piter_space - citer_space;
    nLayers = diff / size;
    unsigned long offset = nLayers*size;
    citer_space += offset;
    if (diff - offset != 0) {
      nLayers++;
    }
  }
  if (citer_space < piter_space) {
    int size = w[nOctaves];
    unsigned long diff = piter_space - citer_space;
    nLines = diff / size;
    unsigned long offset = nLines*size;
    citer_space += offset;
    if (diff - offset != 0) {
      nLines++;
    }
  }

  if (citer_space < piter_space) {
    int size = 1;
    unsigned long diff = piter_space - citer_space;
    nCols = diff / size;
    citer_space += nCols*size;
  }
    
  stop_octave[0] = nOctaves + 1;
  stop_layer[0] = nLayers;
  stop_line[0] = nLines;
  stop_col[0] = nCols;

  unsigned long oiter_space = piter_space;
  for (int p = 1; p < parallelismLevel - 1; p++) {
    long prev_lack = oiter_space - citer_space;
    oiter_space = piter_space + prev_lack;
    int hC = h[stop_octave[p-1]-1];
    int cC = w[stop_octave[p-1]-1];
    citer_space = 0;
    nOctaves = nbOctaves;
    nLayers = nbLayers;
    nLines = hC;
    nCols = cC;

    int tmp = stop_col[p - 1] % cC; 
    start_col[p] = tmp;
    short endsLine = 0;
    long min = tmin_s(oiter_space, (size_t) (cC - tmp));
    nCols = tmp + (int) min;
    citer_space += min;
    if (nCols == cC) {
      endsLine = 1;
    }
    stop_col[p] = nCols;

    
    tmp = (stop_col[p - 1] == cC) ? stop_line[p-1] % hC : stop_line[p-1] - 1;
    start_line[p] = tmp;
    tmp += endsLine;
    short endsLayer = 0;
    min = tmin_s((oiter_space - citer_space)/cC, (size_t) (hC - tmp));
    nLines = tmp + (int) min;
    citer_space += min*cC;
    if (nLines == hC) {
      endsLayer = 1;
    } else if (citer_space < oiter_space) {
      nLines++;
    }
    stop_line[p] = nLines;

        
    tmp = ((stop_col[p - 1] == cC) && (stop_line[p - 1] == hC)) ? stop_layer[p-1] % nbLayers : stop_layer[p-1] - 1;
    start_layer[p] = tmp;
    tmp += endsLayer;
    short endsOctave = 0;
    min = tmin_s((oiter_space - citer_space)/(hC*cC), (size_t) (nbLayers - tmp));
    nLayers = tmp + (int) min;
    citer_space += min*cC*hC;
    if (nLayers == nbLayers) {
      endsOctave = 1;
    } else if (citer_space < oiter_space) {
      nLayers++;
    }
    stop_layer[p] = nLayers;

    
    start_octave[p] = ((stop_col[p - 1] == cC) && (stop_line[p - 1] == hC) && (stop_layer[p - 1] == nbLayers)) ? stop_octave[p-1] : stop_octave[p-1] - 1;
    nOctaves = start_octave[p]+1;
    if (citer_space < oiter_space) {
      for (int i = start_octave[p] + endsOctave; i < nbOctaves; i++) {
	long tmp = citer_space + w[i]*h[i]*nbLayers;
	if (tmp <= oiter_space) {
	  citer_space = tmp;
	}
	if (tmp >= oiter_space) {
	  nOctaves = i + 1;
	  break;
	}
      }
    }
    stop_octave[p] = nOctaves;

    if (stop_octave[p] > stop_octave[p-1]) {
      hC = h[stop_octave[p]-1];
      cC = w[stop_octave[p]-1];
      nLines = hC;
      nCols = cC;
    }
    
    if (citer_space < oiter_space && stop_octave[p] > stop_octave[p-1]) {
      int size = hC*cC;
      unsigned long diff = oiter_space - citer_space;
      nLayers = diff / size;
      unsigned long offset = nLayers*size;
      citer_space += offset;
      if (diff - offset != 0) {
	nLayers++;
      }
    }
    stop_layer[p] = nLayers;

    if (citer_space < oiter_space && (stop_octave[p] > stop_octave[p-1] || stop_layer[p] > stop_layer[p-1])) {
      int size = cC;
      unsigned long diff = oiter_space - citer_space;
      nLines = diff / size;
      unsigned long offset = nLines*size;
      citer_space += offset;
      if (diff - offset != 0) {
	nLines++;
      }
    }
    stop_line[p] = nLines;


    if (citer_space < oiter_space && (stop_octave[p] > stop_octave[p-1] || stop_layer[p] > stop_layer[p-1] || stop_line[p] > stop_line[p-1])) {
      int size = 1;
      unsigned long diff = oiter_space - citer_space;
      nCols = diff / size;
      unsigned long offset = nCols*size;
      citer_space += offset;
    }
    stop_col[p] = nCols;
  }

  int hC = h[stop_octave[parallelismLevel-2]-1];
  int cC = w[stop_octave[parallelismLevel-2]-1];
  start_col[parallelismLevel - 1] = stop_col[parallelismLevel - 2] % cC;
  start_line[parallelismLevel - 1] = (stop_col[parallelismLevel - 2] == cC) ? stop_line[parallelismLevel - 2] % hC: stop_line[parallelismLevel - 2] - 1;
  start_layer[parallelismLevel - 1] = (stop_col[parallelismLevel - 2] == cC) && (stop_line[parallelismLevel - 2] == hC) ? stop_layer[parallelismLevel - 2] % nbLayers : stop_layer[parallelismLevel - 2] - 1;
  start_octave[parallelismLevel - 1] = (stop_col[parallelismLevel - 2] == cC) && (stop_line[parallelismLevel - 2] == hC) &&  (stop_layer[parallelismLevel - 2] == nbLayers) ? stop_octave[parallelismLevel - 2] : stop_octave[parallelismLevel - 2] - 1;
  
  stop_octave[parallelismLevel - 1] = nbOctaves;
  stop_layer[parallelismLevel - 1] = nbLayers;
  stop_line[parallelismLevel - 1] = h[nbOctaves - 1];
  stop_col[parallelismLevel - 1] = w[nbOctaves - 1];
  
}



