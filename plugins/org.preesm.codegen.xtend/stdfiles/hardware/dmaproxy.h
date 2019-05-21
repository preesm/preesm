/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2019)
 * Leonardo Suriano <leonardo.suriano@upm.es> (2019)
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
 * DMA proxy kernel module
 * 
 * Author   : Alfonso Rodriguez <alfonso.rodriguezm@upm.es>
 * Date     : July 2017
 * 
 * Features :
 *     - Platform driver + character device
 *     - Targets memcpy operations (requires src and dst addresses)
 *     - Relies on Device Tree (Open Firmware) to get DMA engine info
 *     - mmap()  : provides zero-copy memory allocation (direct access
 *                 from user-space virtual memory to physical memory)
 *     - ioctl() : enables command passing between user-space and
 *                 character device (e.g. to start DMA transfers)
 * 
 */

#ifndef _DMAPROXY_H_
#define _DMAPROXY_H_

#include <linux/ioctl.h>


/*
 * Basic data structure to use dmaproxy char devices via ioctl()
 *
 * @memaddr - memory address
 * @memoff  - memory address offset
 * @hwaddr  - hardware address
 * @hwoff   - hardware address offset
 * @size    - number of bytes to be transferred
 * 
 */
struct dmaproxy_token {
    void *memaddr;
    size_t memoff;
    void *hwaddr;
    size_t hwoff;
    size_t size;
};


/*
 * IOCTL definitions for dmaproxy char devices
 * 
 * dma_mem2hw - start transfer from main memory to hardware device
 * dma_hw2mem - start transfer from hardware device to main memory
 * 
 */


#define DMAPROXY_IOC_MAGIC 'x'

#define DMAPROXY_IOC_DMA_MEM2HW _IOW(DMAPROXY_IOC_MAGIC, 0, struct dmaproxy_token)
#define DMAPROXY_IOC_DMA_HW2MEM _IOW(DMAPROXY_IOC_MAGIC, 1, struct dmaproxy_token)

#define DMAPROXY_IOC_MAXNR 1

#endif /* _DMAPROXY_H_ */
