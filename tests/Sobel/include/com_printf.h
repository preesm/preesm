#ifndef COM_PRINTF_H
#define COM_PRINTF_H

#include "com.h"

extern char* textActor[IDCount];

#ifdef A9
/**
 * Initialize the master side of the Printf protocol
 */
void comPrintfInitMaster();

/**
 * Clear the master side of the Printf protocol
 */
void comPrintfClearMaster();
#endif

/**
 * Initialize the slave side of the Printf protocol
 * @param com the communicator of the operator
 */
void comPrintfInitSlave(communicator *com);

/**
 * Clear the slave side of the Printf protocol
 * @param com the communicator of the operator
 */
void comPrintfClearSlave(communicator *com);

/**
 * Print a message from any operator to the Linux shell
 * @param com the communicator of the operator
 * @param _format Classic printf multiple arguments
 */
void comPrintf(communicator* com, char *_format, ...);


#endif//COM_PRINTF_H
