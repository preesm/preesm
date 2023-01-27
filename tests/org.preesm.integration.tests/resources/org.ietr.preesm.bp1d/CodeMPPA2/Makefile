# Copyright (C) 2016 Kalray SA. All rights reserved.
# This code is Kalray proprietary and confidential.
# Any use of the code for whatever purpose is subject to
# specific written permission of Kalray SA.

#default path but it can be changed if wanted
PREESM_GENERATED_CODE_PATH := generated
PREESM_PRIMITIVES_CODE_PATH := cluster/preesm_primitives
PREESM_CLUSTER_CODE_PATH := cluster

ifeq ($(nb_cluster), )
nb_cluster := 1
endif

ifeq ($(nb_cluster), 1)
PREESM_CLUSTER_LIST := cluster0_bin
else
ifeq ($(nb_cluster), 2)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin 
else
ifeq ($(nb_cluster), 3)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin
else
ifeq ($(nb_cluster), 4)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin
else
ifeq ($(nb_cluster), 8)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin
else
ifeq ($(nb_cluster), 16)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin cluster13_bin cluster14_bin cluster15_bin 
endif
endif
endif
endif
endif
endif

PREESM_CLUSTER_OS := bare
ifeq ($(cluster_os),nodeos)
PREESM_CLUSTER_OS := nodeos
endif

ifeq ($(ARCH), )
ARCH := k1b
endif

ifeq ($(BOARD), )
BOARD := developer
endif

system-name := bare
arch        := $(ARCH)
board       := $(BOARD)

sim_arch ?= bostan

# Host rules
host-bin : host_bin
host_bin-srcs := host/host.c
host-cflags := -I./host
host-lflags := -lmppa_remote -lpcie -lpthread 
host-bin    := host_bin

# Cluster rules
cluster-bin := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin cluster13_bin cluster14_bin cluster15_bin
cluster-system := $(PREESM_CLUSTER_OS)

cluster0_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core0.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster1_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core1.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster2_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core2.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster3_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core3.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster4_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core4.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster5_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core5.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster6_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core6.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster7_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core7.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster8_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core8.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster9_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core9.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster10_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core10.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster11_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core11.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster12_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core12.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster13_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core13.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster14_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core14.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])
cluster15_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Core15.c $(wildcard  $(PREESM_PRIMITIVES_CODE_PATH)/*.[c,S]) $(wildcard  $(PREESM_CLUSTER_CODE_PATH)/*.[c,S])



cluster-cflags := -DNB_CLUSTER=$(nb_cluster) -mhypervisor -std=gnu99 -g -DMPPA_TRACE_ENABLE -O3 -I $(PREESM_PRIMITIVES_CODE_PATH) -I $(PREESM_GENERATED_CODE_PATH) -I $(PREESM_CLUSTER_CODE_PATH) -ffunction-sections -fdata-sections -fno-common -fno-PIC -Wall
cluster0_bin-cflags := -DCLUSTER_ID=0
cluster1_bin-cflags := -DCLUSTER_ID=1
cluster2_bin-cflags := -DCLUSTER_ID=2
cluster3_bin-cflags := -DCLUSTER_ID=3
cluster4_bin-cflags := -DCLUSTER_ID=4
cluster5_bin-cflags := -DCLUSTER_ID=5
cluster6_bin-cflags := -DCLUSTER_ID=6
cluster7_bin-cflags := -DCLUSTER_ID=7
cluster8_bin-cflags := -DCLUSTER_ID=8
cluster9_bin-cflags := -DCLUSTER_ID=9
cluster10_bin-cflags := -DCLUSTER_ID=10
cluster11_bin-cflags := -DCLUSTER_ID=11
cluster12_bin-cflags := -DCLUSTER_ID=12
cluster13_bin-cflags := -DCLUSTER_ID=13
cluster14_bin-cflags := -DCLUSTER_ID=14
cluster15_bin-cflags := -DCLUSTER_ID=15

#cluster-lflags := -mhypervisor
ifeq ($(PREESM_CLUSTER_OS),nodeos)
cluster-cflags += -fopenmp
cluster-lflags += -fopenmp -pthread 
#cluster-lflags += -pthread
endif
ifeq ($(PREESM_CLUSTER_OS),bare)
cluster-lflags += -lvbsp -utask
cluster-lflags += -Wl,--defsym=KSTACK_SIZE=0x1000 -Wl,--defsym=USER_STACK_SIZE=0x1000 -Wl,--defsym=BOOTSTACK_SIZE=0x1000
endif
cluster-lflags += -mhypervisor -lmppa_remote -lmppa_async -lmppa_request_engine -lmppapower -lmppanoc -lmpparouting
#cluster-lflags += -Wl,--wrap=memset -Wl,--wrap=memcpy 
cluster-lflags += -Wl,--gc-section

# IO rules
io-bin := io_bin
io_bin-srcs := io/main.c
io-cflags := -DNB_CLUSTER=$(nb_cluster) -std=gnu99 -g -DMPPA_TRACE_ENABLE -O2 #-DNDEBUG  

io-cflags += -mhypervisor -Wall -Wextra -I.  -I$(K1_INSTALL_DIR)/noc/include -I./common -I$(K1_INSTALL_DIR)/std_libs/io/include  -I$(K1_INSTALL_DIR)/std_libs/include
io-lflags := -mhypervisor  -lmppa_request_engine -lmppa_remote -lmppa_async -lmppapower -lmppanoc -lmpparouting -lpcie_queue
io-lflags += -lvbsp -lutask $($(ARCH)_io_lflags)
io-lflags += -Wl,--defsym=USER_STACK_SIZE=0x4000

# Global rules
mppa-bin    := multibin
multibin-objs := io_bin $(PREESM_CLUSTER_LIST)

run_hw: hw_test-PREESM_ASYNC_Template
run_sim: sim_test-PREESM_ASYNC_Template
run_pcie_hw: hw_pcie_test-PREESM_ASYNC_Template
run_pcie_display_hw: hw_pcie_display_test-PREESM_ASYNC_Template

hw_test-PREESM_ASYNC_Template : multibin
	@echo "hw_test : PREESM_ASYNC_Template"
	$(K1_TOOLCHAIN_DIR)/bin/k1-jtag-runner --multibinary=$(O)/bin/multibin.mpk --exec-multibin IODDR0:io_bin

hw_pcie_test-PREESM_ASYNC_Template : multibin host_bin
	@echo "hw_pcie_test : PREESM_ASYNC_Template"
	$(O)/bin/host_bin $(O)/bin/multibin.mpk io_bin

hw_pcie_display_test-PREESM_ASYNC_Template : multibin host_bin
	@echo "hw_pcie_display_test : PREESM_ASYNC_Template"
	$(O)/bin/host_bin $(O)/bin/multibin.mpk io_bin | ffplay -f rawvideo -pix_fmt yuv420p -s 640x480 -i /dev/stdin

sim_test-PREESM_ASYNC_Template : multibin
	@echo "sim_test : PREESM_ASYNC_Template" 
	$(K1_TOOLCHAIN_DIR)/bin/k1-mppa --mboard=$(board) --march=$(sim_arch) --dcache-no-check --bootcluster=ioddr0 --  $(O)/bin/multibin.mpk

include $(K1_TOOLCHAIN_DIR)/share/make/Makefile.kalray
