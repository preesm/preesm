/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
/*
 ============================================================================
 Name        : communication.c
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CECILL-C
 Description :
 ============================================================================
 */

#include "preesm_gen.h"
/**
 * Maximum number of core supported by the communication library.
 * This number is used to allocate the table of semaphores used for intercore
 * synchronization.
 *
 * It is not defined in communication.h, otherwise it would lead to a cyclic inclusion
 * with preesm_gen.h (also including communication.h).
 */
#define MAX_NB_CORES NB_CORES

#include "communication.h"

// note: rk_ struct and functions comes from
// https://stackoverflow.com/questions/27736618/why-are-sem-init-sem-getvalue-sem-destroy-deprecated-on-mac-os-x-and-w

#ifdef _WIN32
 void rk_sema_init(struct rk_sema *s, int value) {
#else
inline void rk_sema_init(struct rk_sema *s, int value) {
#endif
#ifdef __APPLE__
    dispatch_semaphore_t *sem = &s->sem;
    *sem = dispatch_semaphore_create(value);
#else
  sem_init(&s->sem, 0, value);
#endif
}

#ifdef _WIN32
 void rk_sema_wait(struct rk_sema *s) {
#else
inline void rk_sema_wait(struct rk_sema *s) {
#endif
#ifdef __APPLE__
    dispatch_semaphore_wait(s->sem, DISPATCH_TIME_FOREVER);
#else
  int r;
  do {
    r = sem_wait(&s->sem);
  } while (r == -1);
#endif
}

#ifdef _WIN32
 void rk_sema_post(struct rk_sema *s) {
#else
inline void rk_sema_post(struct rk_sema *s) {
#endif
#ifdef __APPLE__
    dispatch_semaphore_signal(s->sem);
#else
  sem_post(&s->sem);
#endif
}

// 8 local semaphore for each core (1 useless per core)
struct rk_sema interCoreSem[MAX_NB_CORES][MAX_NB_CORES];

void communicationInit() {
  int i, j;
  for (i = 0; i < MAX_NB_CORES; i++) {
    for (j = 0; j < MAX_NB_CORES; j++) {
      rk_sema_init(&interCoreSem[i][j], 0);
    }
  }
}

void sendStart(int senderID, int receiverID) {
  rk_sema_post(&interCoreSem[receiverID][senderID]);
}

void sendEnd() {
}

void receiveStart() {
}

void receiveEnd(int senderID, int receiverID) {
  rk_sema_wait(&interCoreSem[receiverID][senderID]);
}
