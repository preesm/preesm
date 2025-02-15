/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
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
#ifndef _PREESM_MAC_BARRIER_H
#define _PREESM_MAC_BARRIER_H

#ifdef __APPLE__

#ifdef __cplusplus
extern "C" {
#endif

#include "TargetConditionals.h"
#include <pthread.h>


#if !defined(PTHREAD_BARRIER_SERIAL_THREAD)
# define PTHREAD_BARRIER_SERIAL_THREAD	(1)
#endif

#if !defined(PTHREAD_PROCESS_PRIVATE)
# define PTHREAD_PROCESS_PRIVATE	(42)
#endif
#if !defined(PTHREAD_PROCESS_SHARED)
# define PTHREAD_PROCESS_SHARED		(43)
#endif

typedef struct {
} pthread_barrierattr_t;

typedef struct {
	pthread_mutex_t mutex;
	pthread_cond_t cond;
	unsigned int limit;
	unsigned int count;
	unsigned int phase;
} pthread_barrier_t;

int pthread_barrierattr_init(pthread_barrierattr_t *attr);
int pthread_barrierattr_destroy(pthread_barrierattr_t *attr);

int pthread_barrierattr_getpshared(const pthread_barrierattr_t *restrict attr,
				   int *restrict pshared);
int pthread_barrierattr_setpshared(pthread_barrierattr_t *attr,
				   int pshared);

int pthread_barrier_init(pthread_barrier_t *restrict barrier,
			 const pthread_barrierattr_t *restrict attr,
			 unsigned int count);
int pthread_barrier_destroy(pthread_barrier_t *barrier);

int pthread_barrier_wait(pthread_barrier_t *barrier);


#ifdef __cplusplus
}
#endif

#endif /* __APPLE__ */
#endif /* PTHREAD_BARRIER_H */
