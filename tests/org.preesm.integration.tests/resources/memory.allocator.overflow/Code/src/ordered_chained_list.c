#include "ordered_chained_list.h"

#include <stdio.h>

void initMemKpt(struct OrderedKptList * list, size_t nbElts, struct ElementOrdList * elts, struct SiftKeypoint * mem) {
  list->allEl = elts;
  list->allVal = mem;
  list->beginIndex = 0;
  list->size = 0;
  list->max = nbElts;
}

int getSizeKpt(struct OrderedKptList * list) {
  return list->size;
}

struct SiftKeypoint * getUnorderedValsKpt(struct OrderedKptList * list) {
  return list->allVal;
}


// the comparator must return -1 if value to insert is below the value in the list, 1 for the opposite
// and 0 if equality
void addElementKpt(struct OrderedKptList * list, struct SiftKeypoint * val, int (*comparisonFunction)(struct SiftKeypoint * val_in_list, struct SiftKeypoint * val_to_add)) {
  if (list->size == 0) {
    list->size = 1;
    list->allEl[0].next = NULL;
    list->allEl[0].elementIndex = 0;
    memcpy(list->allVal, val, sizeof(struct SiftKeypoint));
    return;
  }

  short goForward = 1;
  struct ElementOrdList * el = &(list->allEl[list->beginIndex]);
  struct ElementOrdList * prev_el = NULL;
  while (el != NULL) {
    goForward = (*comparisonFunction)(&(list->allVal[el->elementIndex]), val);
    if (goForward == 1) {
      prev_el = el;
      el = el->next;
    } else {
      break;
    }
  }
  /* if (goForward == 0) { */
  /*   return; */
  /* } */

  size_t copyIndex = list->size;

  if (prev_el == NULL) {
    if (list->size == list->max) {
      return;
    } else {
      list->beginIndex = copyIndex;
      list->allEl[copyIndex].elementIndex = copyIndex;
      list->size++;
    }
  } else {
    if (list->size == list->max) {
      copyIndex = list->beginIndex;
      if (prev_el->elementIndex == copyIndex) {
	list->beginIndex = copyIndex;
      } else {
	list->beginIndex = list->allEl[copyIndex].next->elementIndex;
	prev_el->next = &(list->allEl[copyIndex]);
      }
    } else {
      list->allEl[copyIndex].elementIndex = copyIndex;
      prev_el->next = &(list->allEl[copyIndex]);
      list->size++;
    }
  }
  
  memcpy(&(list->allVal[copyIndex]), val, sizeof(struct SiftKeypoint));
  list->allEl[copyIndex].next = el;

  /* fprintf(stderr, "List starting at: %lu (over %lu elts -- start ptr %lu):\n", list->beginIndex, list->size, (unsigned long) (list->allEl)); */
  /* for (size_t i = 0; i < getSizeKpt(list); i++) { */
  /*   fprintf(stderr, "mag: %f\tnext: %lu\tindex: %lu\n", list->allVal[i].mag, (unsigned long) list->allEl[i].next, list->allEl[i].elementIndex); */
  /* } */
  /* fprintf(stderr, "\n"); */
}

/* -------- for MatchPair ---------- */


void initMemMatch(struct OrderedMatchList * list, size_t nbElts, struct ElementOrdList * elts, struct MatchPair * mem) {
  list->allEl = elts;
  list->allVal = mem;
  list->beginIndex = 0;
  list->size = 0;
  list->max = nbElts;
}

int getSizeMatch(struct OrderedMatchList * list) {
  return list->size;
}

struct MatchPair * getUnorderedValsMatch(struct OrderedMatchList * list) {
  return list->allVal;
}


// the comparator must return -1 if value to insert is below the value in the list, 1 for the opposite
// and 0 if equality
void addElementMatch(struct OrderedMatchList * list, struct MatchPair * val, int (*comparisonFunction)(struct MatchPair * val_in_list, struct MatchPair * val_to_add)) {
  if (list->size == 0) {
    list->size = 1;
    list->allEl[0].next = NULL;
    list->allEl[0].elementIndex = 0;
    memcpy(list->allVal, val, sizeof(struct MatchPair));
    return;
  }

  short goForward = 1;
  struct ElementOrdList * el = &(list->allEl[list->beginIndex]);
  struct ElementOrdList * prev_el = NULL;
  while (el != NULL) {
    goForward = (*comparisonFunction)(&(list->allVal[el->elementIndex]), val);
    if (goForward == 1) {
      prev_el = el;
      el = el->next;
    } else {
      break;
    }
  }
  if (goForward == 0) {
    return;
  }

  int copyIndex = list->size;

  if (prev_el == NULL) {
    if (list->size == list->max) {
      return;
    } else {
      list->beginIndex = copyIndex;
      list->allEl[copyIndex].elementIndex = copyIndex;
      list->size++;
    }
  } else {
    if (list->size == list->max) {
      copyIndex = list->beginIndex;
      if (prev_el->elementIndex == copyIndex) {
	list->beginIndex = copyIndex;
      } else {
	list->beginIndex = list->allEl[copyIndex].next->elementIndex;
	prev_el->next = &(list->allEl[copyIndex]);
      }
    } else {
      list->allEl[copyIndex].elementIndex = copyIndex;
      prev_el->next = &(list->allEl[copyIndex]);
      list->size++;
    }
  }
  
  memcpy(&(list->allVal[copyIndex]), val, sizeof(struct MatchPair));    
  list->allEl[copyIndex].next = el;  
}

