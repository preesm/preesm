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
