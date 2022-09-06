import math
import model_fifo_zynq
import re
import subprocess
import time

#[[#]]# Graph parameters
top_kernel_name = $PREESM_TOP_KERNEL_NAME
names = [ $PREESM_FIFO_NAMES ]
upper_bound = [ $PREESM_FIFO_SIZES ]
lower_bound = [ $PREESM_FIFO_MIN_SIZES ]
lambdas = [ $PREESM_FIFO_LAMBDAS ]
widths = [ $PREESM_FIFO_WIDTHS ]

#[[#]]# DSE options
use_ressource_wise = True
use_lambdas = True
use_initial_tests = True

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
            file.write('#[[#]]#define ' + names[i] + ' ' + str(int(buffer_sizes[i])) + '\n')

def candidate_buffer_size(lower, upper, width):
    if use_ressource_wise:
        return next_smaller_buffer(lower, upper, width)
    else:
        return dichotomy(lower, upper)

def dichotomy(lower, upper):
    return math.ceil(lower + (upper - lower)/2)

def next_smaller_buffer(lower, upper, width):
    proposed_depth = dichotomy(lower, upper)
    if model_fifo_zynq.bram_usage(proposed_depth, width) != 0:
        proposed_depth = model_fifo_zynq.next_smaller_fifo(upper, width)
    if proposed_depth == lower:
        proposed_depth = upper
    return proposed_depth

def is_improved(candidate, upper, width):
    if use_ressource_wise:
        return is_improved_ressource_wise(candidate, upper, width)
    else:
        return is_improved_token_wise(candidate, upper)

def is_improved_token_wise(candidate, upper):
    return candidate < upper

def is_improved_ressource_wise(candidate, upper, width):
    candidate_cost = model_fifo_zynq.bram_usage(candidate, width)
    if (candidate_cost == 0):
        return is_improved_token_wise(candidate, upper)
    upper_cost = model_fifo_zynq.bram_usage(upper, width)
    return candidate_cost < upper_cost

def iterative_cosim():
    best_ii = run_cosim(upper_bound)
    if(best_ii == -1):
        raise ValueError('Graph deadlocked with original buffer sizes')
    nb_cosim = 1
    if use_initial_tests:
        nb_cosim += initial_tests_cosim(best_ii)
    if use_lambdas:
        nb_cosim += lambda_iterative_cosim(best_ii)
    nb_cosim += sequential_iterative_cosim(best_ii)
    write_buffer_sizes(upper_bound)
    return nb_cosim

def sequential_iterative_cosim(best_ii):
    nb_cosim = 0
    for i in range(len(names)):
        buffer_sizes = [x for x in upper_bound]
        buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i], widths[i])
        while is_improved(buffer_sizes[i], upper_bound[i], widths[i]):
            current_ii = run_cosim(buffer_sizes)
            nb_cosim += 1
            if current_ii <= best_ii and current_ii > 0:
                upper_bound[i] = buffer_sizes[i]
            else:
                lower_bound[i] = buffer_sizes[i]
            buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i], widths[i])
    return nb_cosim

def initial_tests_cosim(best_ii):
    nb_cosim = 0
    for i in range(len(names)):
        buffer_sizes = [x for x in upper_bound]
        buffer_sizes[i] = 5
        if is_improved(buffer_sizes[i], upper_bound[i], widths[i]):
            current_ii = run_cosim(buffer_sizes)
            nb_cosim += 1
            if current_ii <= best_ii and current_ii > 0:
                upper_bound[i] = buffer_sizes[i]
                lambdas[i] = 4
            else:
                lower_bound[i] = buffer_sizes[i]
    return nb_cosim

def lambda_iterative_cosim(best_ii):
    nb_cosim = 0
    while max(lambdas) > 0:
        candidates = [i for (i, c) in zip(range(len(lambdas)), lambdas) if c > 0.5 * max(lambdas)]
        buffer_sizes = [x for x in upper_bound]
        for i in candidates:
            buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i], widths[i])
        current_ii = run_cosim(buffer_sizes)
        nb_cosim += 1
        if(current_ii <= best_ii and current_ii > 0):
            for i in candidates:
                upper_bound[i] = buffer_sizes[i]
                lambdas[i] = lambdas[i] / 2
                if not is_improved(candidate_buffer_size(lower_bound[i], upper_bound[i], widths[i]), upper_bound[i], widths[i]):
                    lambdas[i] = 0
        else:
            for i in candidates:
                buffer_sizes = [x for x in upper_bound]
                buffer_sizes[i] = candidate_buffer_size(lower_bound[i], upper_bound[i], widths[i])
                current_ii = run_cosim(buffer_sizes)
                nb_cosim += 1
                if(current_ii <= best_ii and current_ii > 0):
                    upper_bound[i] = buffer_sizes[i]
                    lambdas[i] = lambdas[i] / 2
                else:
                    lower_bound[i] = buffer_sizes[i]
                    lambdas[i] = 0
    return nb_cosim

if __name__=="__main__":
    start = time.time()
    nb_cosim = iterative_cosim()
    end = time.time()
    print('buffer sizes: ', upper_bound)
    print('nb_cosim: ', nb_cosim)
    print('runtime: ', end - start)
