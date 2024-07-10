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
#include "mac_barrier.h"

#include <errno.h>

#ifdef __APPLE__

#define __unused __attribute__((unused))

int
pthread_barrierattr_init(pthread_barrierattr_t *attr __unused)
{
	return 0;
}

int
pthread_barrierattr_destroy(pthread_barrierattr_t *attr __unused)
{
	return 0;
}

int
pthread_barrierattr_getpshared(const pthread_barrierattr_t *restrict attr __unused,
			       int *restrict pshared)
{
	*pshared = PTHREAD_PROCESS_PRIVATE;
	return 0;
}

int
pthread_barrierattr_setpshared(pthread_barrierattr_t *attr __unused,
			       int pshared)
{
	if (pshared != PTHREAD_PROCESS_PRIVATE) {
		errno = EINVAL;
		return -1;
	}
	return 0;
}

int
pthread_barrier_init(pthread_barrier_t *restrict barrier,
		     const pthread_barrierattr_t *restrict attr __unused,
		     unsigned count)
{
	if (count == 0) {
		errno = EINVAL;
		return -1;
	}

	if (pthread_mutex_init(&barrier->mutex, 0) < 0) {
		return -1;
	}
	if (pthread_cond_init(&barrier->cond, 0) < 0) {
		int errno_save = errno;
		pthread_mutex_destroy(&barrier->mutex);
		errno = errno_save;
		return -1;
	}

	barrier->limit = count;
	barrier->count = 0;
	barrier->phase = 0;

	return 0;
}

int
pthread_barrier_destroy(pthread_barrier_t *barrier)
{
    pthread_mutex_destroy(&barrier->mutex);
    pthread_cond_destroy(&barrier->cond);
    return 0;
}

int
pthread_barrier_wait(pthread_barrier_t *barrier)
{
	pthread_mutex_lock(&barrier->mutex);
	barrier->count++;
	if (barrier->count >= barrier->limit) {
		barrier->phase++;
		barrier->count = 0;
		pthread_cond_broadcast(&barrier->cond);
		pthread_mutex_unlock(&barrier->mutex);
		return PTHREAD_BARRIER_SERIAL_THREAD;
	} else {
		unsigned phase = barrier->phase;
		do
			pthread_cond_wait(&barrier->cond, &barrier->mutex);
		while (phase == barrier->phase);
		pthread_mutex_unlock(&barrier->mutex);
		return 0;
	}
}

#endif /* __APPLE__ */
