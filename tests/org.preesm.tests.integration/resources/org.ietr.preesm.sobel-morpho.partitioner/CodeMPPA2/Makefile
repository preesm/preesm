# Copyright (C) 2016 Kalray SA. All rights reserved.
# This code is Kalray proprietary and confidential.
# Any use of the code for whatever purpose is subject to
# specific written permission of Kalray SA.

#default path but it can be changed if wanted
PREESM_GENERATED_CODE_PATH := generated
PREESM_KERNEL_SRC_CODE_PATH := src
PREESM_KERNEL_INCLUDE_PATH := include

CLUSTER_MAIN_FILE := $(PREESM_GENERATED_CODE_PATH)/cluster_main.c 
GENERATED_FILES := $(PREESM_GENERATED_CODE_PATH)/clock.c $(PREESM_GENERATED_CODE_PATH)/fifo.c $(PREESM_GENERATED_CODE_PATH)/dump.c $(PREESM_GENERATED_CODE_PATH)/communication_mppa.c $(PREESM_GENERATED_CODE_PATH)/memory.c
CLUSTER_FILES := $(CLUSTER_MAIN_FILE) $(GENERATED_FILES)

IO_CODE_SOURCE := $(shell find $(PREESM_GENERATED_CODE_PATH) -name 'IO.c')


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
ifeq ($(nb_cluster), 5)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin
else
ifeq ($(nb_cluster), 6)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin
else
ifeq ($(nb_cluster), 7)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin
else
ifeq ($(nb_cluster), 8)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin
else
ifeq ($(nb_cluster), 9)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin 
else
ifeq ($(nb_cluster), 10)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin 
else
ifeq ($(nb_cluster), 11)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin 
else
ifeq ($(nb_cluster), 12)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin 
else
ifeq ($(nb_cluster), 13)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin 
else
ifeq ($(nb_cluster), 14)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin cluster13_bin 
else
ifeq ($(nb_cluster), 15)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin cluster13_bin cluster14_bin 
else
ifeq ($(nb_cluster), 16)
PREESM_CLUSTER_LIST := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin cluster13_bin cluster14_bin cluster15_bin 
endif # 16
endif # 15
endif # 14
endif # 13
endif # 12
endif # 11
endif # 10
endif # 9
endif # 8
endif # 7
endif # 6
endif # 5
endif # 4
endif # 3
endif # 2
endif # 1

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

sim_arch ?= k1b

# Host rules
host-bin : host_bin
host_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/host_main.c
host-cflags := -I./host
host-lflags := -lmppa_remote -lpcie -lpthread 
host-bin    := host_bin

# Cluster rules
cluster-bin := cluster0_bin cluster1_bin cluster2_bin cluster3_bin cluster4_bin cluster5_bin cluster6_bin cluster7_bin cluster8_bin cluster9_bin cluster10_bin cluster11_bin cluster12_bin cluster13_bin cluster14_bin cluster15_bin
cluster-system := $(PREESM_CLUSTER_OS)

cluster0_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster00.c $(wildcard $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster1_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster01.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster2_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster02.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster3_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster03.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster4_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster04.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster5_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster05.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster6_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster06.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster7_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster07.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster8_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster08.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster9_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster09.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster10_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster10.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster11_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster11.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster12_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster12.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster13_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster13.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster14_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster14.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
cluster15_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/Cluster15.c $(wildcard  $(CLUSTER_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])



cluster-cflags := -g -DNB_CLUSTER=$(nb_cluster) -mhypervisor -std=gnu99 -DMPPA_TRACE_ENABLE -O3 -I $(PREESM_GENERATED_CODE_PATH) -I $(PREESM_KERNEL_INCLUDE_PATH) -ffunction-sections -fdata-sections -fno-common -fno-PIC -Wall -fopenmp 
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
cluster-lflags += -Wl,--defsym=KSTACK_SIZE=0x400 -Wl,--defsym=USER_STACK_SIZE=0x3000 -Wl,--defsym=BOOTSTACK_SIZE=0x800
endif
cluster-lflags += -mhypervisor -lmppa_remote -lmppa_async -lmppa_request_engine -lmppapower -lmppanoc -lmpparouting -fopenmp 
#cluster-lflags += -Wl,--wrap=memset -Wl,--wrap=memcpy 
cluster-lflags += -Wl,--gc-section

# IO rules
io-bin := io_bin
io_bin-srcs := $(PREESM_GENERATED_CODE_PATH)/io_main.c $(IO_CODE_SOURCE) $(wildcard  $(GENERATED_FILES)) $(wildcard  $(PREESM_KERNEL_SRC_CODE_PATH)/*.[c,S])
io-cflags := -std=gnu99 -DMPPA_TRACE_ENABLE -O2 #-DNDEBUG  

io-cflags += -g -mhypervisor -Wall -Wextra -I.  -I$(K1_INSTALL_DIR)/noc/include -I./common -I$(K1_INSTALL_DIR)/std_libs/io/include  -I$(K1_INSTALL_DIR)/std_libs/include -I $(PREESM_KERNEL_INCLUDE_PATH) -I $(PREESM_GENERATED_CODE_PATH) -fopenmp 
io-lflags := -mhypervisor  -lmppa_request_engine -lmppa_remote -lmppa_async -lmppapower -lmppanoc -lmpparouting -lpcie_queue 
io-lflags += -lvbsp -lutask $($(ARCH)_io_lflags)
io-lflags += -Wl,--defsym=USER_STACK_SIZE=0x4000 -fopenmp 

# Global rules
mppa-bin    := multibin
multibin-objs := io_bin $(PREESM_CLUSTER_LIST)


no_run:no_run-PREESM_ASYNC_Template

run_hw: hw_test-PREESM_ASYNC_Template
run_sim: sim_test-PREESM_ASYNC_Template
run_pcie_hw: hw_pcie_test-PREESM_ASYNC_Template
run_pcie_display_hw: hw_pcie_display_test-PREESM_ASYNC_Template

hw_test-PREESM_ASYNC_Template : multibin
	@echo "hw_test : PREESM_ASYNC_Template"
	$(K1_TOOLCHAIN_DIR)/bin/k1-jtag-runner --multibinary=$(O)/bin/multibin.mpk --exec-multibin IODDR0:io_bin

no_run-PREESM_ASYNC_Template : multibin host_bin
	@echo "hw_pcie_test : PREESM_ASYNC_Template"

hw_pcie_test-PREESM_ASYNC_Template : multibin host_bin
	@echo "hw_pcie_test : PREESM_ASYNC_Template"
	$(O)/bin/host_bin $(O)/bin/multibin.mpk io_bin

hw_pcie_display_test-PREESM_ASYNC_Template : multibin host_bin
	@echo "hw_pcie_display_test : PREESM_ASYNC_Template"
	$(O)/bin/host_bin $(O)/bin/multibin.mpk io_bin | ffplay -f rawvideo -pix_fmt yuv420p -s 640x480 -i /dev/stdin

sim_test-PREESM_ASYNC_Template : multibin
	@echo "sim_test : PREESM_ASYNC_Template" 
	$(K1_TOOLCHAIN_DIR)/bin/k1-pciesim-runner -f "-p" ./output/bin/host_bin ./output/bin/multibin.mpk io_bin

include $(K1_TOOLCHAIN_DIR)/share/make/Makefile.kalray
