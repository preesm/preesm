/*
 * Copyright (c) 2009, Texas Instruments Incorporated
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * *  Neither the name of Texas Instruments Incorporated nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
/*
 *  ======== DucatiAmmu.cfg ========
 *
 *  An example configuration script used by both the server and client
 *  applications running on either Bios6 or Linux.
 */

var AMMU = xdc.useModule('ti.sysbios.hal.ammu.AMMU');

/*********************** Small Pages *************************/
/* smallPages[0] & smallPages[1] are auto-programmed by h/w */

/* Overwrite smallPage[1] so that 16K is covered. H/w reset value configures
 * only 4K */
AMMU.smallPages[1].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[1].logicalAddress = 0x40000000;
AMMU.smallPages[1].translatedAddress = 0x55080000;
AMMU.smallPages[1].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[1].size = AMMU.Small_16K;

/* L2RAM: 64K mapped using 4 smallPages(16K); cacheable; translated */
/* config small page[2] to map 16K VA 0x20000000 to PA 0x55020000  */
AMMU.smallPages[2].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[2].logicalAddress = 0x20000000;
AMMU.smallPages[2].translatedAddress = 0x55020000;
AMMU.smallPages[2].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[2].L1_writePolicy = AMMU.WritePolicy_WRITE_BACK;
AMMU.smallPages[2].L1_allocate = AMMU.AllocatePolicy_ALLOCATE;
AMMU.smallPages[2].L1_posted = AMMU.PostedPolicy_POSTED;
AMMU.smallPages[2].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.smallPages[2].size = AMMU.Small_16K;

/* config small page[3] to map 16K VA 0x20004000 to PA 0x55024000 */
AMMU.smallPages[3].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[3].logicalAddress = 0x20004000;
AMMU.smallPages[3].translatedAddress = 0x55024000;
AMMU.smallPages[3].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[3].L1_writePolicy = AMMU.WritePolicy_WRITE_BACK;
AMMU.smallPages[3].L1_allocate = AMMU.AllocatePolicy_ALLOCATE;
AMMU.smallPages[3].L1_posted = AMMU.PostedPolicy_POSTED;
AMMU.smallPages[3].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.smallPages[3].size = AMMU.Small_16K;

/* config small page[4] to map 16K VA 0x20008000 to PA 0x55028000 */
AMMU.smallPages[4].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[4].logicalAddress = 0x20008000;
AMMU.smallPages[4].translatedAddress = 0x55028000;
AMMU.smallPages[4].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[4].L1_writePolicy = AMMU.WritePolicy_WRITE_BACK;
AMMU.smallPages[4].L1_allocate = AMMU.AllocatePolicy_ALLOCATE;
AMMU.smallPages[4].L1_posted = AMMU.PostedPolicy_POSTED;
AMMU.smallPages[4].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.smallPages[4].size = AMMU.Small_16K;

/* config small page[5] to map 16K VA 0x2000C000 to PA 0x5502C000 */
AMMU.smallPages[5].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[5].logicalAddress = 0x2000C000;
AMMU.smallPages[5].translatedAddress = 0x5502C000;
AMMU.smallPages[5].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[5].L1_writePolicy = AMMU.WritePolicy_WRITE_BACK;
AMMU.smallPages[5].L1_allocate = AMMU.AllocatePolicy_ALLOCATE;
AMMU.smallPages[5].L1_posted = AMMU.PostedPolicy_POSTED;
AMMU.smallPages[5].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.smallPages[5].size = AMMU.Small_16K;

/* ISS: Use 4 small pages(3 4K and 1 16K) for various ISP registers; non-cacheable; translated */
/* config small page[6] to map 16K VA 0x50000000 to PA 0x55040000 */
/* non cacheable by default */
AMMU.smallPages[6].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[6].logicalAddress = 0x50000000;
AMMU.smallPages[6].translatedAddress = 0x55040000;
AMMU.smallPages[6].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[6].size = AMMU.Small_16K;

/* config small page[7] to map 4K VA 0x50010000 to PA 0x55050000 */
/* non cacheable by default */
AMMU.smallPages[7].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[7].logicalAddress = 0x50010000;
AMMU.smallPages[7].translatedAddress = 0x55050000;
AMMU.smallPages[7].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[7].size = AMMU.Small_4K;

/* config small page[8] to map 4K VA 0x50011000 to PA 0x55051000 */
/* non cacheable by default */
AMMU.smallPages[8].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[8].logicalAddress = 0x50011000;
AMMU.smallPages[8].translatedAddress = 0x55051000;
AMMU.smallPages[8].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[8].size = AMMU.Small_4K;

/* config small page[9] to map 4K VA 0x50020000 to PA 0x55060000 */
/* non cacheable by default */
AMMU.smallPages[9].pageEnabled = AMMU.Enable_YES;
AMMU.smallPages[9].logicalAddress = 0x50020000;
AMMU.smallPages[9].translatedAddress = 0x55060000;
AMMU.smallPages[9].translationEnabled = AMMU.Enable_YES;
AMMU.smallPages[9].size = AMMU.Small_4K;


/*********************** Medium Pages *************************/
/* ISS: The entire ISS register space using a medium page (256K); cacheable; translated */
/* config medium page[0] to map 256K VA 0x50000000 to PA 0x55040000 */
/* Make it L1 cacheable */
AMMU.mediumPages[0].pageEnabled = AMMU.Enable_YES;
AMMU.mediumPages[0].logicalAddress = 0x50000000;
AMMU.mediumPages[0].translatedAddress = 0x55040000;
AMMU.mediumPages[0].translationEnabled = AMMU.Enable_YES;
AMMU.mediumPages[0].size = AMMU.Medium_256K;
AMMU.mediumPages[0].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.mediumPages[0].L1_posted = AMMU.PostedPolicy_POSTED;


/*********************** Large Pages *************************/
/* Instruction Code: Large page  (512M); cacheable */
/* config large page[0] to map 512MB VA 0x0 to L3 0x0 */
AMMU.largePages[0].pageEnabled = AMMU.Enable_YES;
AMMU.largePages[0].logicalAddress = 0x0;
AMMU.largePages[0].translationEnabled = AMMU.Enable_NO;
AMMU.largePages[0].size = AMMU.Large_512M;
AMMU.largePages[0].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.largePages[0].L1_posted = AMMU.PostedPolicy_POSTED;

/* TILER & DMM regions: Large page (512M); cacheable */
/* config large page[1] to map 512MB VA 0x60000000 to L3 0x60000000 */
AMMU.largePages[1].pageEnabled = AMMU.Enable_YES;
AMMU.largePages[1].logicalAddress = 0x60000000;
AMMU.largePages[1].translationEnabled = AMMU.Enable_NO;
AMMU.largePages[1].size = AMMU.Large_512M;
AMMU.largePages[1].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.largePages[1].L1_posted = AMMU.PostedPolicy_POSTED;

/* Private, Shared and IPC Data regions: Large page (512M); cacheable */
/* config large page[2] to map 512MB VA 0x80000000 to L3 0x80000000 */
AMMU.largePages[2].pageEnabled = AMMU.Enable_YES;
AMMU.largePages[2].logicalAddress = 0x80000000;
AMMU.largePages[2].translationEnabled = AMMU.Enable_NO;
AMMU.largePages[2].size = AMMU.Large_512M;
AMMU.largePages[2].L1_cacheable = AMMU.CachePolicy_CACHEABLE;
AMMU.largePages[2].L1_posted = AMMU.PostedPolicy_POSTED;

/* Peripheral regions: Large Page (512M); non-cacheable */
/* config large page[3] to map 512MB VA 0xA0000000 to L3 0xA0000000 */
AMMU.largePages[3].pageEnabled = AMMU.Enable_YES;
AMMU.largePages[3].logicalAddress = 0xA0000000;
AMMU.largePages[3].translationEnabled = AMMU.Enable_NO;
AMMU.largePages[3].size = AMMU.Large_512M;
AMMU.largePages[3].L1_cacheable = AMMU.CachePolicy_NON_CACHEABLE;
AMMU.largePages[3].L1_posted = AMMU.PostedPolicy_POSTED;

/*
 *  @(#) ti.sdo.ipc; 1, 0, 0, 0,187; 4-14-2009 21:57:21; /db/vtree/library/trees/ipc/ipc-b33x/src/
 */