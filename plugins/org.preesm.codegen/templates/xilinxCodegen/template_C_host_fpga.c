/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Mickael Dardaillon [mickael.dardaillon@insa-rennes.fr] (2022 - 2024)
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


// Generic includes
#[[#]]#include <stdio.h>
#[[#]]#include <stdlib.h>
#[[#]]#include "xil_cache.h"
#[[#]]#include "xil_printf.h"

$PREESM_INCLUDES

int main(int argc, char **argv) {
	print("Platform started\n\r");

	// TODO Initialize input and output buffers
$BUFFER_INITIALIZATION

	print("Transferring data\n\r");
	// Initialize mem_read to send data to kernel
	$XMEM_READ_KERNEL_NAME mem_read;
	${XMEM_READ_KERNEL_NAME}_Initialize(&mem_read, $XMEM_READ_KERNEL_DEVICE_ID);
$INPUT_BUFFER_INTERFACE

	// Initialize mem_write to receive data from kernel
	$XMEM_WRITE_KERNEL_NAME mem_write;
	${XMEM_WRITE_KERNEL_NAME}_Initialize(&mem_write, $XMEM_WRITE_KERNEL_DEVICE_ID);
$OUTPUT_BUFFER_INTERFACE

	// Flush input buffer to transfer to non-coherent memories
$INPUT_BUFFER_FLUSHING

	print("Starting kernel\n\r");
	// Start input and output interfaces
	${XMEM_READ_KERNEL_NAME}_Start(&mem_read);
	${XMEM_WRITE_KERNEL_NAME}_Start(&mem_write);
	while (!${XMEM_WRITE_KERNEL_NAME}_IsDone(&mem_write)) {
		print("Waiting for kernel output\n\r");
	}

	// Flush output buffer to transfer from non-coherent memories
	print("Getting results\n\r");
$OUTPUT_BUFFER_FLUSHING

	// TODO Read results

	print("Stopping platform\n\r");
	return EXIT_SUCCESS;
}
