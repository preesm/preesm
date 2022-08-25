import math
import re
import subprocess
import time

top_kernel_name = $PREESM_TOP_KERNEL_NAME
names = [ $PREESM_FIFO_NAMES ]
upper_bound = [ $PREESM_FIFO_SIZES ]
lower_bound = [ $PREESM_FIFO_MIN_SIZES ]
lambdas = [ $PREESM_FIFO_LAMBDAS ]

def run_cosim(buffer_sizes):
    write_buffer_sizes(buffer_sizes)
    subprocess.run(['vitis_hls', 'scripts/script_hls.tcl',  'cosim', top_kernel_name, top_kernel_name + '.cpp'])
    return get_cosim_ii()

def get_cosim_ii():
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

def candidate_buffer_size(lower, upper):
    return dichotomy(lower, upper)

def dichotomy(lower, upper):
    return math.ceil(lower + (upper - lower)/2)

def is_improved(candidate, upper):
    return candidate < upper

def iterative_cosim():
    best_ii = run_cosim(upper_bound)
    return greedy_iterative_cosim(best_ii) + 1

def sequential_iterative_cosim(best_ii):
    nb_cosim = 0
    for i in range(len(names)):
        buffer_sizes = [x for x in upper_bound]
        buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i])
        while is_improved(buffer_sizes[i], upper_bound[i]):
            current_ii = run_cosim(buffer_sizes)
            nb_cosim += 1
            if current_ii == best_ii:
                upper_bound[i] = buffer_sizes[i]
            else:
                lower_bound[i] = buffer_sizes[i]
            buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i])
    write_buffer_sizes(upper_bound)
    return nb_cosim

def greedy_iterative_cosim(best_ii):
    nb_cosim = 0
    while max(lambdas) > 0:
        candidates = [i for (i, c) in zip(range(len(lambdas)), lambdas) if c > 0.5 * max(lambdas)]
        buffer_sizes = [x for x in upper_bound]
        for i in candidates:
            buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i])
        current_ii = run_cosim(buffer_sizes)
        nb_cosim += 1
        if(current_ii == best_ii):
            for i in candidates:
                upper_bound[i] = buffer_sizes[i]
                lambdas[i] = lambdas[i] / 2
                if not is_improved(candidate_buffer_size(lower_bound[i], upper_bound[i]), upper_bound[i]):
                    lambdas[i] = 0
        else:
            buffer_sizes = [x for x in upper_bound]
            for i in candidates:
                buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i])
                current_ii = run_cosim(buffer_sizes)
                nb_cosim += 1
                if(current_ii == best_ii):
                    upper_bound[i] = buffer_sizes[i]
                    lambdas[i] = lambdas[i] / 2
                else:
                    lower_bound[i] = buffer_sizes[i]
                    lambdas[i] = 0
                    break
    return sequential_iterative_cosim(best_ii) + nb_cosim

if __name__=="__main__":
    start = time.time()
    nb_cosim = iterative_cosim()
    end = time.time()
    print('nb_cosim: ' + str(nb_cosim))
    print('runtime: ' + str(end - start))
