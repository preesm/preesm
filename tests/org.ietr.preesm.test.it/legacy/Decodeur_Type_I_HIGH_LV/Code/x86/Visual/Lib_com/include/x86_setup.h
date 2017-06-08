/*******************************************************************************
 * Copyright or © or Copr. 2010 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2010)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
#ifndef X86SETUP
#define X86SETUP

#include <windows.h>
#include "OS_Com.h"
#include "PC_x86_SEM.h"



//#include <stdio.h>
//#include <stdlib.h>
#include <windows.h>

#define semaphore HANDLE

#define MEDIUM_SEND 0
#define MEDIUM_RCV 1
#define TCP 100

/* ID of the core for PC */
#define Core0 	0
#define Core1 	1
#define Core2	2
#define Core3 	3
#define Core4 	4
#define Core5 	5
#define Core6 	6
#define Core7 	7

#define MEDIA_NR 	1
#define CORE_NUMBER 1

Medium Media[MEDIA_NR][MEDIA_NR];
semaphore sem_init[MEDIA_NR][MEDIA_NR];

#endif
