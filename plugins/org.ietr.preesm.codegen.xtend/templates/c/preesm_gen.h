/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
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
	Name        : preesm_gen.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#[[#]]#ifndef _PREESM_PREESM_GEN_H
#[[#]]#define _PREESM_PREESM_GEN_H

#[[#]]#define _GNU_SOURCE
#[[#]]#ifdef _WIN32
#[[#]]#include <windows.h>
#[[#]]#include <pthread.h>
#[[#]]#else
// For Linux
// Pthread barriers are defined in POSIX 2001 version
// For the 1990 revision compliance the defined value of _POSIX_VERSION should be 1.
// For the 1995 revision compliance the defined value of _POSIX_VERSION should be 199506L.
// For the 2001 revision compliance the defined value of _POSIX_VERSION should be 200112L.
#[[#]]#ifdef _POSIX_C_SOURCE
#[[#]]#undef _POSIX_C_SOURCE
#[[#]]#endif

#[[#]]#ifdef _XOPEN_SOURCE
#[[#]]#undef _XOPEN_SOURCE
#[[#]]#endif

#[[#]]#define _POSIX_C_SOURCE 200112L
#[[#]]#define _XOPEN_SOURCE 600
#[[#]]#include <pthread.h>
#[[#]]#include <unistd.h>
#[[#]]#endif

#[[#]]#ifdef __APPLE__
#[[#]]#include "TargetConditionals.h"
#[[#]]#include "mac_barrier.h"
#[[#]]#endif

#[[#]]#include <stdio.h>
#[[#]]#include <string.h>
#[[#]]#include <stdint.h>

#[[#]]#include "communication.h"
#[[#]]#include "dump.h"
#[[#]]#include "fifo.h"

$USER_INCLUDES

#[[#]]#endif
