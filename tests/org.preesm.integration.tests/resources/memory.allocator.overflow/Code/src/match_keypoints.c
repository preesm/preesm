#include <float.h>

#include "ezsift-preesm.h"
#include "vvector.h"
#include "ordered_chained_list.h"

// write a MERGE func that will check the unicity

// Helper callback function for merge match list.
int comparison_match_pair (struct MatchPair * first, struct MatchPair * second){ 
  if (first->c1 == second->c1 && first->r1 == second->r1 &&
      first->c2 == second->c2 && first->r2 == second->r2) {
    return 0;
  }
  return 1;
}

int compareMatchPairThreshold(struct MatchPair * match_in_list, struct MatchPair * match_to_add) {
  if (match_to_add->threshold < match_in_list->threshold) {
    return -1;
  } else if (match_to_add->threshold > match_in_list->threshold) {
    return 1;
  }
  return 0;
}


// to call after the two complete SIFT, or after one keypoint list is finished and then one by one?
// may need to parallelize the list access

// Match keypoints from two images, using brutal force method.
// Use Euclidean distance as matching score. 
int match_keypoints(int nBins, int nLocalMatchMax,
		    IN struct SiftKeypoint * kpt_list1,
		    IN struct SiftKeypoint * kpt_list2,
		    IN int * nbKeypoints1, IN int * nbKeypoints2,
		    OUT struct MatchPair * matches,
		    OUT int * nbMatches) {

  struct MatchPair mp;

  struct OrderedMatchList match_list;
  struct ElementOrdList elts[nLocalMatchMax];
  initMemMatch(&match_list, nLocalMatchMax, elts, matches);

  
  for (int index1 = 0; index1 < *nbKeypoints1; ++index1) {
    struct SiftKeypoint * kpt1 = kpt_list1 + index1;
    // Position of the matched feature.
    int r1 = (int) kpt1->r;
    int c1 = (int) kpt1->c;

    float * descr1 = kpt1->descriptors;
    float score1 = FLT_MAX; // highest score
    float score2 = FLT_MAX; // 2nd highest score

    // Position of the matched feature.
    int r2, c2;
    for (int index2 = 0; index2 < *nbKeypoints2; ++index2) {
      struct SiftKeypoint * kpt2 = kpt_list2 + index2;
      float score = 0;
      float * descr2 =  kpt2->descriptors;
      float dif;
      for (int i = 0; i < nBins; i ++) {
	dif = descr1[i] - descr2[i];
	score += dif * dif;
      }

      if (score < score1){
	score2 = score1;
	score1 = score;
	r2 = (int) kpt2->r;
	c2 = (int) kpt2->c;
      } else if(score < score2){
	score2 = score;
      }
    }

#if SIFT_USE_FAST_FUNC
    float thr = fast_sqrt_f(score1/score2); 
#else
      float thr = sqrtf(score1/score2); 
#endif
	if (thr < SIFT_MATCH_NNDR_THR)
	{
	  mp.r1 = r1;
	  mp.c1 = c1;
	  mp.r2 = r2;
	  mp.c2 = c2;
	  mp.threshold = thr;
	  
	  addElementMatch(&match_list, &mp, &compareMatchPairThreshold);
	}
  }

#if PRINT_MATCH_KEYPOINTS
  int match_list_size = getSizeMatch(match_list);
  for (int index = 0; index < match_list_size; ++index){
    struct MatchPair * p = &(getUnorderedValsMatch(match_list)[index]);
    printf("\tMatch %3d: (%4d, %4d) -> (%4d, %4d)\n", index, p->r1, p->c1, p->r2, p->c2);
  }
#endif

  return 0;
}
