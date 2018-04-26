#include <stdlib.h>
#include <string.h>

#include "ezsift-preesm.h"

#ifndef ORDERED_CHAINED_LIST_H
#define ORDERED_CHAINED_LIST_H

struct ElementOrdList {
  size_t elementIndex; // we can avoid it with pointer arithmetic
  struct ElementOrdList * next;
};

struct OrderedKptList {
  struct ElementOrdList * allEl;//allEl[SIFT_localKPTmax];
  struct SiftKeypoint * allVal;//allVal[SIFT_localKPTmax];
  size_t beginIndex;
  size_t size;
  size_t max;
};


void initMemKpt(struct OrderedKptList * list, size_t nbElts, struct ElementOrdList * elts, struct SiftKeypoint * mem);

int getSizeKpt(struct OrderedKptList * list);

struct SiftKeypoint * getUnorderedValsKpt(struct OrderedKptList * list);


// the comparator must return -1 if value to insert is below the value in the list, 1 for the opposite
// and 0 if equality
void addElementKpt(struct OrderedKptList * list, struct SiftKeypoint * val, int (*comparisonFunction)(struct SiftKeypoint * val_in_list, struct SiftKeypoint * val_to_add));

/* -------- for MatchPair ---------- */

struct OrderedMatchList {
  struct ElementOrdList * allEl;//allEl[SIFT_localMatchMax];
  struct MatchPair * allVal;//allVal[SIFT_localMatchMax];
  size_t beginIndex;
  size_t size;
  size_t max;
};


void initMemMatch(struct OrderedMatchList * list, size_t nbElts, struct ElementOrdList * elts, struct MatchPair * mem);

int getSizeMatch(struct OrderedMatchList * list);

struct MatchPair * getUnorderedValsMatch(struct OrderedMatchList * list);


// the comparator must return -1 if value to insert is below the value in the list, 1 for the opposite
// and 0 if equality
void addElementMatch(struct OrderedMatchList * list, struct MatchPair * val, int (*comparisonFunction)(struct MatchPair * val_in_list, struct MatchPair * val_to_add));


#endif
