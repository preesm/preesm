/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
/*
 * define.h
 *
 *  Created on: 9 sept. 2013
 *      Author: rparois
 */

#ifndef DEFINE_H_
#define DEFINE_H_

/* define socket for linux or windows */
#if defined (WIN32)
    #include <winsock2.h>
    typedef int socklen_t;
#elif defined (linux)
    #include <sys/types.h>
    #include <sys/socket.h>
    #include <netinet/in.h>
    #include <arpa/inet.h>
    #include <unistd.h>
    #define INVALID_SOCKET -1
    #define SOCKET_ERROR -1
    #define closesocket(s) close(s)
    typedef int SOCKET;
    typedef struct sockaddr_in SOCKADDR_IN;
    typedef struct sockaddr SOCKADDR;
#endif

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

/* Standard headers */
#include <ti/syslink/Std.h>

/* OSAL & Utils headers */
#include <ti/syslink/utils/Trace.h>
#include <ti/syslink/utils/OsalPrint.h>
#include <ti/syslink/utils/Memory.h>
#include <ti/syslink/utils/String.h>
#include <ti/syslink/utils/Cache.h>

#include <ti/syslink/ProcMgr.h>
#include <ti/syslink/SysLink.h>

/* Module level headers */
#include <ti/ipc/MultiProc.h>
#include <ti/ipc/HeapMemMP.h>
#include <ti/ipc/SharedRegion.h>

/* Module level headers */
#include <ti/ipc/MessageQ.h>
#include <ti/ipc/Notify.h>
/* Ipc header */
#include <ti/ipc/Ipc.h>

#include <ti/syslink/inc/_MultiProc.h>

#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include "ProcMgrApp.h"

/* App info tag ID */
#define APP_INFO_TAG        0xBABA0000
#define APPNOTIFY_EVENT_NO                11u
/* key to generate data pattern for messages */
#define PATTERNKEY               1u

#define MSGQ_NAME		"MSGQ_"
#define HEAP_ALIGN  	128
#define HEAP_MSGSIZE  	128-32
#define HEAP_NUMMSGS  	10
#define HEAPID  		0
#define NUMLOOPS  		1000
#define HEAP_NAME  		"HeapMemMP"

#define BUFFER_SIZE_MAX 3000*1500*3

#define HEAP_SIZE		0x02000000
#define HEAP_NAME  		"HeapMemMP"

#endif /* DEFINE_H_ */
