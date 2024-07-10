/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
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

#ifndef _PREESM_PREESM_GEN_MPPA_H
#define _PREESM_PREESM_GEN_MPPA_H

#ifdef __cplusplus
extern "C" {
#endif

#define SHARED_SEGMENT_ID (10)
#define SYNC_BASE_SEGMENT_ID (11)
#define INTERCC_BASE_SEGMENT_ID (30)
#ifdef __bare__
#include "utask.h"
#else
#endif

#include "preesm_gen.h"
#include "communication_mppa.h"

#if defined(__k1io__)
#ifndef PREESM_NB_CORES_IO
#define PREESM_NB_CORES_IO 1
#endif
#else
#ifndef PREESM_NB_CORES_CC
#define PREESM_NB_CORES_CC 1
#endif
#endif

#if defined(__k1io__)
#if PREESM_NB_CORES_IO > 3
#undef PREESM_NB_CORES_IO
#define PREESM_NB_CORES_IO 3
#endif
#else
#if PREESM_NB_CORES_CC > 16
#undef PREESM_NB_CORES_CC
#define PREESM_NB_CORES_CC 16
#endif
#endif

#define Shared (0x90000000ULL) 	/* Shared buffer in DDR */

#ifdef __cplusplus
}
#endif

#endif
