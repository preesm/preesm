import math
import re
import subprocess
import time

top_kernel_name = $PREESM_TOP_KERNEL_NAME
names = [ $PREESM_FIFO_NAMES ]
upper_bound = [ $PREESM_FIFO_SIZES ]
lower_bound = [ $PREESM_FIFO_MIN_SIZES ]

def run_cosim():
    subprocess.run(['vitis_hls', 'scripts/script_hls.tcl',  'cosim', top_kernel_name, top_kernel_name + '.cpp'])

def parse_cosim_results():
    with open(top_kernel_name + '/solution1/sim/report/' + top_kernel_name + '_cosim.rpt') as file:
        result = file.readlines()[10]
        result = result.split('|')
        if(re.search('Pass', result[2])):
             return int(result[7].strip())
        else:
            return -1

def write_buffer_sizes(buffer_sizes):
    with open('PreesmAutoDefinedSizes.h', 'a') as file:
        for i in range(len(names)):
            file.write('#[[#]]#define ' + names[i] + ' ' + str(buffer_sizes[i]) + '\n')

def dichotomy(lower, upper):
    return math.ceil(lower + (upper - lower)/2)

if __name__=="__main__":
    start = time.time()
    nb_cosim = 1
    run_cosim()
    best_throughput = parse_cosim_results()
    for i in range(len(names)):
        buffer_sizes = upper_bound.copy()
        buffer_sizes[i] = dichotomy(lower_bound[i], upper_bound[i])
        while(buffer_sizes[i] < upper_bound[i]):
            write_buffer_sizes(buffer_sizes)
            run_cosim()
            nb_cosim += 1
            if(parse_cosim_results() == best_throughput):
                upper_bound[i] = buffer_sizes[i]
            else:
                lower_bound[i] = buffer_sizes[i]
            buffer_sizes[i] = dichotomy(lower_bound[i], upper_bound[i])
    end = time.time()
    write_buffer_sizes(upper_bound)
    print('nb_cosim: ' + str(nb_cosim))
    print('runtime: ' + str(end - start))

