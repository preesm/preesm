from pynq import Overlay
from pynq import allocate

import numpy as np
import time

#[[#]]# Load the kernels
overlay = Overlay('/home/xilinx/jupyter_notebooks/preesm/${APPLI_NAME}.bit')
mem_read = overlay.${KERNEL_NAME_READ}_0
mem_write = overlay.${KERNEL_NAME_WRITE}_0


#[[#]]# Define constants
$PREESM_CONSTANTS
    
    
#[[#]]# Buffer initialization
#[[#]]# TODO update dtype
#[[#]]# see: https://numpy.org/doc/stable/user/basics.types.html
#[[#]]# see: https://numpy.org/doc/stable/reference/arrays.dtypes.html
#[[#]]# TODO fill data
$PREESM_BUFFER_INIT

        
#[[#]]# Buffer mapping to FPGA
$PREESM_BUFFER_MAPPING


#[[#]]# Start read and write
start = time.time()
mem_read.register_map.CTRL.AP_START = 1
mem_write.register_map.CTRL.AP_START = 1
end = time.time()
    
#[[#]]# Active wait
while mem_write.register_map.CTRL.AP_DONE == 0:
    end = time.time()
print ("time: ", end - start)
    
#[[#]]# TODO check results
